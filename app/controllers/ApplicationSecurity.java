package controllers;

import models.User;

public class ApplicationSecurity extends Secure.Security {
    static boolean authenticate(String username,
                                String password)
    {
        User user = User.find("username = ?", username).first();
        return user != null && user.validatePassword(password);
    }
}
