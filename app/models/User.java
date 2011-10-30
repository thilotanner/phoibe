package models;

import play.Logger;
import play.data.validation.Email;
import play.data.validation.Required;
import util.hashing.HashingUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Entity
public class User extends EnhancedModel {

    public static String generateSalt()
    {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(String.valueOf(new Date().getTime()).getBytes());
            return HashingUtils.toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Unable to generate sale", e);
            return null;
        }
    }

    public static String generatePasswordHash(String password, String salt)
    {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(password.getBytes());
            messageDigest.update(salt.getBytes());
            return HashingUtils.toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Unable to validate saltedPassword");
            return null;
        }
    }

    @Required
    @Column(unique = true)
    public String username;

    public String firstName;

    public String lastName;

    @Transient
    public String password;

    @Transient
    public String passwordConfirmation;

    public String saltedPassword;

    public String salt;

    @Required
    @Email
    public String email;

    public void setPassword(String password) {
        if(password != null && !password.isEmpty()) {
            salt = generateSalt();
            saltedPassword = generatePasswordHash(password, salt);
        }
    }


    public boolean validatePassword(String password)
    {
        String saltedPasswordToValidate = generatePasswordHash(password, this.salt);
        return this.saltedPassword.equals(saltedPasswordToValidate);
    }
}
