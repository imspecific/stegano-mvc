package com.steganography.service;

import com.steganography.model.User;
import com.steganography.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public LoginResult login(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            return new LoginResult(null, "Please enter username and password.");
        }

        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            return new LoginResult(user, "Welcome to Steganography Application");
        } else {
            return new LoginResult(null, "Invalid Username or password");
        }
    }

    public String register(String firstName, String lastName,
                            String username, String password, String confirmPassword) {

        if (firstName.isBlank() || lastName.isBlank()
                || username.isBlank() || password.isBlank()) {
            return "Please enter valid input..";
        }

        if (!password.equals(confirmPassword)) {
            return "Entered passwords are not matched, enter it again!!!";
        }

        boolean ok = userRepository.registerUser(firstName, lastName, username, password);
        return ok
            ? "Sign up successful, now proceed to Login.."
            : "Sign up unsuccessful";
    }

    public static class LoginResult {
        private final User   user;
        private final String message;

        public LoginResult(User user, String message) {
            this.user    = user;
            this.message = message;
        }

        public boolean isSuccess() { return user != null; }
        public User    getUser()   { return user;    }
        public String  getMessage(){ return message; }
    }
}
