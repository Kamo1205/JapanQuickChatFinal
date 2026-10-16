package com.japanquickchat.japanquickchat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {
    
    @Test
    public void testCheckUserNameCorrect() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.checkUserName());
    }

    @Test
    public void testCheckUserNameIncorrect() {
        Login login = new Login(
                "John",
                "Doe",
                "kyle!!!!!!!",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertFalse(login.checkUserName());
    }

    @Test
    public void testCheckPasswordCorrect() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    public void testCheckPasswordIncorrect() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "password",
                "+27838968976"
        );

        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testCheckCellPhoneNumberCorrect() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    public void testCheckCellPhoneNumberIncorrect() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "08966553"
        );

        assertFalse(login.checkCellPhoneNumber());
    }

    @Test
    public void testRegisterUserSuccessful() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertEquals(
                "Username successfully captured.\n"
                + "Password successfully captured.\n"
                + "Cell phone number successfully added.",
                login.registerUser()
        );
    }

    @Test
    public void testRegisterUserInvalidUsername() {
        Login login = new Login(
                "John",
                "Doe",
                "kyle!!!!!!!",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertEquals(
                "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                login.registerUser()
        );
    }

    @Test
    public void testRegisterUserInvalidPassword() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "password",
                "+27838968976"
        );

        assertEquals(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                login.registerUser()
        );
    }

    @Test
    public void testLoginSuccessful() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertFalse(login.loginUser("wrong_user", "wrong_password"));
    }

    @Test
    public void testReturnLoginStatusSuccessful() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        login.loginUser("kyl_1", "Ch&&sec@ke99!");

        assertEquals(
                "Welcome John, Doe it is great to see you again.",
                login.returnLoginStatus()
        );
    }

    @Test
    public void testReturnLoginStatusFailed() {
        Login login = new Login(
                "John",
                "Doe",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        login.loginUser("wrong_user", "wrong_password");

        assertEquals(
                "Username or password incorrect, please try again.",
                login.returnLoginStatus()
        );
    }
}