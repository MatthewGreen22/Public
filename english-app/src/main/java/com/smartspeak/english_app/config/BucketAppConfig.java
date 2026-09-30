package com.smartspeak.english_app.config;

import com.smartspeak.english_app.ratelimiting.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BucketAppConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor interceptor;

    public BucketAppConfig(RateLimitInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/send-password-reset")
                .addPathPatterns("/resend")
                .addPathPatterns("/verify")
                .addPathPatterns("/register")
                .addPathPatterns("/password-reset")
                .addPathPatterns("/username")
                .addPathPatterns("/login");
    }
}
