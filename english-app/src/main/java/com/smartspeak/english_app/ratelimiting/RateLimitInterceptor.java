package com.smartspeak.english_app.ratelimiting;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Value("${rate.limit}")
    private int RATE_LIMIT; // Максимальное количество запросов, разрешенное в указанный временной интервал.
    @Value("${time.duration.in.minutes}")
    private long TIME_DURATION; // Временной интервал в минутах, в течение которого применяется ограничение скорости.

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Создает новое ведро для ограничения скорости на основе предоставленного API ключа.
     * Этот метод настраивает ведро с заданной скоростью заполнения и емкостью на основе конфигурации приложения.
     *
     * @param apiKey API ключ, для которого создается ведро.
     * @return Новый экземпляр {@link Bucket}, настроенный с указанным ограничением скорости и временным интервалом.
     */
    private Bucket newBucket(String apiKey) {
        Refill refill = Refill.intervally(RATE_LIMIT, Duration.ofMinutes(TIME_DURATION));
        Bandwidth limit = Bandwidth.classic(RATE_LIMIT, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Определяет ведро, связанное с данным API ключом. Если ведро для API ключа не существует,
     * создается новое с использованием {@link #newBucket(String)}.
     *
     * @param apiKey API ключ, для которого необходимо определить ведро.
     * @return {@link Bucket}, связанный с данным API ключом.
     */
    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    /**
     * Перехватывает входящие запросы для применения ограничения скорости на основе IP адреса клиента.
     * Если запрос превышает ограничение скорости, возвращается ответ 429 Too Many Requests.
     * В противном случае запрос разрешается, и в заголовках ответа указывается оставшееся количество токенов.
     *
     * @param request Входящий {@link HttpServletRequest}.
     * @param response {@link HttpServletResponse}, в который метод может записывать информацию.
     * @param handler Обработчик текущего запроса.
     * @return Булево значение, указывающее, должен ли запрос продолжаться. Возвращает false, если превышено ограничение скорости.
     * @throws Exception если во время обработки запроса произошла ошибка.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String remoteIpAddress = request.getHeader("X-Forwarded-For");
        if (remoteIpAddress == null || remoteIpAddress.isEmpty()) {
            remoteIpAddress = request.getRemoteAddr();
        }
        if (remoteIpAddress == null || remoteIpAddress.isEmpty()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Отсутствует заголовок: remoteIpAddress");
            return false;
        }
        Bucket tokenBucket = resolveBucket(remoteIpAddress);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setCharacterEncoding("UTF-8"); // Установка кодировки символов
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Превышено количество запросов в минуту.\"}");
            return false;
        }
    }
}