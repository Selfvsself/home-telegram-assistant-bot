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

    public User findById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is null");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }

    public long findChatIdByUserId(UUID userId) {
        User user = findById(userId);
        return Optional.of(user.getChatId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " has no chatId"));
    }
}