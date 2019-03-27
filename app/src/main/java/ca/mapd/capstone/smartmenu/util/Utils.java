package ca.mapd.capstone.smartmenu.util;

public class Utils {

    public static boolean loginFormIsValid(String email, String password) {
        boolean formValid = true;

        if (email.trim().isEmpty() || !email.matches("^(.+)@(.+)$")) {
            formValid = false;
        }
        if (password.length() < 6) {
            formValid = false;
        }
        return formValid;
    }
}
