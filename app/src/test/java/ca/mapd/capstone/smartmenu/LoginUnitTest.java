package ca.mapd.capstone.smartmenu;

import org.junit.Test;

import ca.mapd.capstone.smartmenu.util.Utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginUnitTest {

    @Test
    public void testLoginValidation() {
        String email = "abcd@mail.ca";
        String password = "asdadas";
        assertTrue(Utils.loginFormIsValid(email, password));
    }

    @Test
    public void testLoginValidationWithEmptyFields() {
        String email = "";
        String password = "";
        assertFalse(Utils.loginFormIsValid(email, password));
    }

}
