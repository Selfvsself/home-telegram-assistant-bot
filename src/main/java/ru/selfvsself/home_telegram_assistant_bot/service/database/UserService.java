package ru.selfvsself.home_telegram_assistant_bot.service.database;

import org.springframework.stereotype.Service;
import ru.selfvsself.home_telegram_assistant_bot.model.database.User;
import ru.selfvsself.home_telegram_assistant_bot.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUserIfNotExists(Long chatId, String name) {
        Optional<User> existingUser = userRepository.findByChatId(chatId);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        User newUser = new User(chatId, name);
        return userRepository.save(newUser);
    }

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }
}