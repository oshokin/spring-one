package ru.geekbrains.server.auth;

import ru.geekbrains.server.User;
import ru.geekbrains.server.persistance.UserRepository;

import java.sql.SQLException;

public class AuthServiceJdbcImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceJdbcImpl() throws SQLException {
        this.userRepository = new UserRepository();
        if (userRepository.getAllUsers().size() == 0) {
            userRepository.insert(new User(-1, "ivan", "123"));
            userRepository.insert(new User(-1, "petr", "345"));
            userRepository.insert(new User(-1, "julia", "789"));
        }
    }

    @Override
    public boolean authUser(User user) {
        try {
            User usr = userRepository.findByLogin(user.getLogin());
            return usr.getId() > 0 && usr.getPassword().equals(user.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
