package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * Genera un hash de una contraseña utilizando BCrypt.
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Verifica una contraseña contra un hash de contraseña.
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
