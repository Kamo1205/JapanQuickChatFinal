
package com.japanquickchat.japanquickchat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    private static final Path JSON_FILE = Path.of("messages.json");

    private byte[] originalJson;

    @BeforeEach
    public void setUp() throws IOException {
        // Keep a backup so tests do not permanently
        // change the user's stored messages.
        originalJson = Files.exists(JSON_FILE)
                ? Files.readAllBytes(JSON_FILE) : null;

        Files.writeString(JSON_FILE, "[]");
        Message.loadStoredMessages();
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (originalJson == null) {
            Files.deleteIfExists(JSON_FILE);
        } else {
            Files.write(JSON_FILE, originalJson);
        }

        Message.loadStoredMessages();
    }

    // PART 2 TESTS

    @Test
    public void testCheckMessageID() {
        Message message = new Message(
                "0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");

        assertTrue(message.checkMessageID());
    }

    @Test
    public void testCheckRecipientCellCorrect() {
        Message message = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell());
    }

    @Test
    public void testCheckRecipientCellIncorrect() {
        Message message = new Message(
                "08575975889",
                "Hi Keegan, did you receive the payment?", 1);

        assertEquals(
                "Cell phone number is incorrectly formatted "
                + "or does not contain an international code. "
                + "Please correct the number and try again.",
                message.checkRecipientCell());
    }

    @Test
    public void testCheckMessageLengthCorrect() {
        Message message = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);

        assertTrue(message.checkMessageLength());
    }

    @Test
    public void testCheckMessageLengthIncorrect() {
        Message message = new Message(
                "+27718693002", "A".repeat(251), 0);

        assertFalse(message.checkMessageLength());
    }

    @Test
    public void testCreateMessageHash() {
        Message message = new Message(
                "0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");

        assertEquals(
                "00:0:HITONIGHT",
                message.createMessageHash());
    }

    @Test
    public void testSendMessage() {
        Message message = new Message(
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);

        assertEquals(
                "Message successfully sent.",
                message.SentMessage("1"));
    }

    @Test
    public void testDisregardMessage() {
        Message message = new Message(
                "+27812345678",
                "Hi Keegan, did you receive the payment?", 1);

        assertEquals(
                "Press O to delete the message.",
                message.SentMessage("2"));
    }

    @Test
    public void testStoreMessage() {
        Message message = new Message(
                "+27718693002", "This is a test message.", 0);

        assertTrue(message.storeMessage());
    }

    // PART 3 TEST DATA

    private void createPart3TestMessages() {

        Message message1 = new Message(
                "1000000001", 1,
                "+27834557896",
                "Did you get the cake?");

        Message message2 = new Message(
                "2000000002", 2,
                "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");

        Message message3 = new Message(
                "3000000003", 3,
                "+27834484567",
                "Yohoooo, I am at your gate.");

        Message message4 = new Message(
                "4000000004", 4,
                "+27838884567",
                "It is dinner time!");

        Message message5 = new Message(
                "5000000005", 5,
                "+27838884567",
                "Ok, I am leaving without you.");

        assertEquals(
                "Message successfully sent.",
                message1.SentMessage("1"));

        assertEquals(
                "Message successfully stored.",
                message2.SentMessage("3"));

        assertEquals(
                "Press O to delete the message.",
                message3.SentMessage("2"));

        assertEquals(
                "Message successfully sent.",
                message4.SentMessage("1"));

        assertEquals(
                "Message successfully stored.",
                message5.SentMessage("3"));
    }

    // PART 3: VERIFY ARRAYS

    @Test
    public void testSentMessagesArray() {
        createPart3TestMessages();

        assertTrue(Message.getSentMessages().stream()
                .anyMatch(m -> m.getMessageText()
                        .equals("Did you get the cake?")));

        assertTrue(Message.getSentMessages().stream()
                .anyMatch(m -> m.getMessageText()
                        .equals("It is dinner time!")));
    }

    @Test
    public void testDisregardedMessagesArray() {
        createPart3TestMessages();

        assertTrue(Message.getDisregardedMessages().stream()
                .anyMatch(m -> m.getMessageText()
                        .equals("Yohoooo, I am at your gate.")));
    }

    @Test
    public void testStoredMessagesArrayFromJson() {
        createPart3TestMessages();

        assertEquals(2, Message.getStoredMessages().size());

        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                Message.getStoredMessages().get(0).getMessageText());

        assertEquals(
                "Ok, I am leaving without you.",
                Message.getStoredMessages().get(1).getMessageText());
    }

    // PART 3: LONGEST MESSAGE

    @Test
    public void testDisplayLongestMessage() {
        createPart3TestMessages();

        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                Message.displayLongestMessage());
    }

    // PART 3: SEARCH MESSAGE ID

    @Test
    public void testSearchMessageID() {
        createPart3TestMessages();

        assertEquals(
                "Recipient: +27838884567\nMessage: It is dinner time!",
                Message.searchMessageID("4000000004"));
    }

    // PART 3: SEARCH BY RECIPIENT

    @Test
    public void testSearchRecipient() {
        createPart3TestMessages();

        String result = Message.searchRecipient("+27838884567");

        assertTrue(result.contains(
                "Where are you? You are late! I have asked you to be on time."));

        assertTrue(result.contains(
                "Ok, I am leaving without you."));
    }

    // PART 3: DELETE USING MESSAGE HASH

    @Test
    public void testDeleteMessageByHash() {
        createPart3TestMessages();

        assertEquals(
                "Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                Message.deleteMessageByHash("20:2:WHERETIME"));

        assertEquals(1, Message.getStoredMessages().size());

        assertEquals(
                "Message ID not found.",
                Message.searchMessageID("2000000002"));
    }

    // PART 3: DISPLAY REPORTS

    @Test
    public void testDisplayStoredReport() {
        createPart3TestMessages();

        String report = Message.displayStoredReport();

        assertTrue(report.contains("Message ID: 2000000002"));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message Hash: 20:2:WHERETIME"));
    }

    @Test
    public void testDisplaySentReport() {
        createPart3TestMessages();

        String report = Message.displaySentReport();

        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time!"));
        assertTrue(report.contains("Message Hash:"));
    }

    @Test
    public void testSearchMessageIDNotFound() {
        assertEquals(
                "Message ID not found.",
                Message.searchMessageID("9999999999"));
    }

    @Test
    public void testDeleteMessageHashNotFound() {
        assertEquals(
                "Message hash not found.",
                Message.deleteMessageByHash("99:9:NOTFOUND"));
    }
}
