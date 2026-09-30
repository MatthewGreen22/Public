package com.smartspeak.english_app.jwt;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Интерфейс репозитория для управления сущностями Token.
 */
public interface TokenRepository extends JpaRepository<Token, Integer> {

    /**
     * Получает все действительные токены, связанные с определенным пользователем.
     *
     * @param id Идентификатор пользователя.
     * @return Список действительных токенов, связанных с указанным пользователем.
     */
    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id\s
            """)
    List<Token> findAllValidTokenByUser(UUID id);
}