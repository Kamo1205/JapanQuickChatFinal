package com.japanquickchat.japanquickchat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testCheckMessageID() {
        Message message = new Message(
                "0012345678",
                0,
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertTrue(message.checkMessageID());
    }

    @Test
    public void testCheckRecipientCellCorrect() {
        Message message = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?",
                0
        );

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell()
        );
    }

    @Test
    public void testCheckRecipientCellIncorrect() {
        Message message = new Message(
                "08575975889",
                "Hi Keegan, did you receive the payment?",
                1
        );

        assertEquals(
                "Cell phone number is incorrectly formatted "
                + "or does not contain an international code. "
                + "Please correct the number and try again.",
                message.checkRecipientCell()
        );
    }

    @Test
    public void testCheckMessageLengthCorrect() {
        Message message = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?",
                0
        );

        assertTrue(message.checkMessageLength());
    }

    @Test
    public void testCheckMessageLengthIncorrect() {
        String longText = "A".repeat(251);

        Message message = new Message(
                "+27718693002",
                longText,
                0
        );

        assertFalse(message.checkMessageLength());
    }

    @Test
    public void testCreateMessageHash() {
        Message message = new Message(
                "0012345678",
                0,
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertEquals(
                "00:0:HITONIGHT",
                message.createMessageHash()
        );
    }

    @Test
    public void testSendMessage() {
        Message message = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?",
                0
        );

        assertEquals(
                "Message successfully sent.",
                message.SentMessage("1")
        );
    }

    @Test
    public void testDisregardMessage() {
        Message message = new Message(
                "+27812345678",
                "Hi Keegan, did you receive the payment?",
                1
        );

        assertEquals(
                "Press O to delete the message.",
                message.SentMessage("2")
        );
    }

    @Test
    public void testStoreMessage() {
        Message message = new Message(
                "+27718693002",
                "This is a test message.",
                0
        );

        assertTrue(message.storeMessage());
    }
}