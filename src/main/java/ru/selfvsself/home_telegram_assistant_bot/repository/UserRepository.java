package ru.selfvsself.home_telegram_assistant_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selfvsself.home_telegram_assistant_bot.model.database.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);

    Optional<User> findById(UUID userId);
}
