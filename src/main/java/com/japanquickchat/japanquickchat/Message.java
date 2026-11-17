
package com.japanquickchat.japanquickchat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Message {

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;

    private static int totalMessages = 0;

    // PART 3: REQUIRED ARRAYS
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+27\\d{9}$");

    private static final Path JSON_FILE = Path.of("messages.json");

    public Message(String recipient, String messageText, int messageNumber) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    public Message(String messageID, int messageNumber,
                   String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    private static String generateMessageID() {
        String id;
        do {
            id = String.valueOf(
                    ThreadLocalRandom.current().nextLong(
                            1_000_000_000L, 10_000_000_000L));
        } while (messageIDs.contains(id));

        return id;
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("\\d{10}");
    }

    public String checkRecipientCell() {
        if (recipient != null
                && PHONE_PATTERN.matcher(recipient).matches()) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted "
                + "or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    public boolean checkMessageLength() {
        return messageText != null && messageText.length() <= 250;
    }

    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2
                || messageText == null || messageText.trim().isEmpty()) {
            return "";
        }

        String[] words = messageText.trim().split("\\s+");

        String first = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String last = words[words.length - 1]
                .replaceAll("[^a-zA-Z0-9]", "");

        return messageID.substring(0, 2) + ":"
                + messageNumber + ":"
                + (first + last).toUpperCase();
    }

    private void registerMessage() {
        if (!messageIDs.contains(messageID)) {
            messageIDs.add(messageID);
        }
        if (!messageHashes.contains(messageHash)) {
            messageHashes.add(messageHash);
        }
    }

    // PART 2: SEND, DISREGARD OR STORE
    public String SentMessage(String choice) {
        if (!checkMessageLength()) {
            return "Please enter a message of less than 250 characters.";
        }

        if (!checkMessageID()) {
            return "Invalid message ID.";
        }

        if (recipient == null
                || !PHONE_PATTERN.matcher(recipient).matches()) {
            return checkRecipientCell();
        }

        if (choice == null) {
            return "Invalid option.";
        }

        switch (choice.trim().toLowerCase()) {
            case "1":
            case "send":
                if (!sentMessages.contains(this)) {
                    sentMessages.add(this);
                    totalMessages++;
                }
                registerMessage();
                return "Message successfully sent.";

            case "2":
            case "disregard":
            case "discard":
                if (!disregardedMessages.contains(this)) {
                    disregardedMessages.add(this);
                }
                registerMessage();
                return "Press O to delete the message.";

            case "3":
            case "store":
                return storeMessage()
                        ? "Message successfully stored."
                        : "Message could not be stored.";

            default:
                return "Invalid option.";
        }
    }

    // PART 3: READ STORED MESSAGES FROM JSON
    public static List<Message> readStoredMessages() {
        List<Message> messages = new ArrayList<>();

        if (!Files.exists(JSON_FILE)) {
            return messages;
        }

        try {
            String json = Files.readString(JSON_FILE);

            if (json.trim().isEmpty()) {
                return messages;
            }

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Message>>() {}.getType();
            List<Message> loaded = gson.fromJson(json, type);

            if (loaded != null) {
                for (Message message : loaded) {
                    if (message != null) {
                        message.messageHash = message.createMessageHash();
                        messages.add(message);
                    }
                }
            }

        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read stored messages: "
                    + e.getMessage());
        }

        return messages;
    }

    public static void loadStoredMessages() {
        storedMessages.clear();
        storedMessages.addAll(readStoredMessages());

        for (Message message : storedMessages) {
            message.registerMessage();
        }
    }

    // PART 2 + 3: SAVE MESSAGE TO JSON
    public boolean storeMessage() {
        if (!checkMessageID() || !checkMessageLength()
                || recipient == null
                || !PHONE_PATTERN.matcher(recipient).matches()) {
            return false;
        }

        try {
            List<Message> messages = readStoredMessages();

            boolean exists = false;
            for (Message message : messages) {
                if (message.messageID.equals(messageID)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                messages.add(this);
            }

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting().create();

            Files.writeString(JSON_FILE, gson.toJson(messages));

            storedMessages.clear();
            storedMessages.addAll(messages);
            registerMessage();

            return true;

        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    // PART 3: GET REQUIRED ARRAYS
    public static List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public static List<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static List<Message> getStoredMessages() {
        loadStoredMessages();
        return new ArrayList<>(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    // PART 3: DISPLAY SENDER AND RECIPIENT
    public static String displaySenderAndRecipient(String sender) {
        loadStoredMessages();

        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        StringBuilder result = new StringBuilder();

        for (Message message : storedMessages) {
            result.append("Sender: ").append(sender)
                    .append("\nRecipient: ")
                    .append(message.recipient)
                    .append("\n\n");
        }

        return result.toString().trim();
    }

    // PART 3: DISPLAY LONGEST STORED MESSAGE
    public static String displayLongestMessage() {
        loadStoredMessages();

        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        Message longest = storedMessages.get(0);

        for (Message message : storedMessages) {
            if (message.messageText != null
                    && (longest.messageText == null
                    || message.messageText.length()
                    > longest.messageText.length())) {
                longest = message;
            }
        }

        return longest.messageText;
    }

    // PART 3: SEARCH BY MESSAGE ID
    public static String searchMessageID(String id) {
        loadStoredMessages();

        List<Message> all = new ArrayList<>(sentMessages);
        all.addAll(storedMessages);

        for (Message message : all) {
            if (message.messageID.equals(id)) {
                return "Recipient: " + message.recipient
                        + "\nMessage: " + message.messageText;
            }
        }

        return "Message ID not found.";
    }

    // PART 3: SEARCH BY RECIPIENT
    public static String searchRecipient(String phone) {
        loadStoredMessages();

        List<Message> all = new ArrayList<>(sentMessages);
        all.addAll(storedMessages);

        StringBuilder result = new StringBuilder();

        for (Message message : all) {
            if (message.recipient.equals(phone)) {
                result.append("Message: ")
                        .append(message.messageText)
                        .append("\n");
            }
        }

        return result.length() == 0
                ? "No messages found for this recipient."
                : result.toString().trim();
    }

    // PART 3: DELETE STORED MESSAGE USING HASH
    public static String deleteMessageByHash(String hash) {
        loadStoredMessages();

        Message target = null;

        for (Message message : storedMessages) {
            if (message.messageHash.equals(hash)) {
                target = message;
                break;
            }
        }

        if (target == null) {
            return "Message hash not found.";
        }

        List<Message> remaining = new ArrayList<>(storedMessages);
        remaining.remove(target);

        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting().create();

            Files.writeString(JSON_FILE, gson.toJson(remaining));

            storedMessages.clear();
            storedMessages.addAll(remaining);

            messageIDs.remove(target.messageID);
            messageHashes.remove(target.messageHash);

            return "Message: \"" + target.messageText
                    + "\" successfully deleted.";

        } catch (IOException e) {
            return "Message could not be deleted.";
        }
    }

    // PART 3: FULL REPORT OF STORED MESSAGES
    public static String displayStoredReport() {
        loadStoredMessages();

        return storedMessages.isEmpty()
                ? "No stored messages found."
                : formatMessages(storedMessages);
    }

    // PART 3: FULL REPORT OF SENT MESSAGES
    public static String displaySentReport() {
        return sentMessages.isEmpty()
                ? "No messages have been sent yet."
                : formatMessages(sentMessages);
    }

    private static String formatMessages(List<Message> messages) {
        StringBuilder result = new StringBuilder();

        for (Message message : messages) {
            result.append("Message ID: ")
                    .append(message.messageID)
                    .append("\nMessage Hash: ")
                    .append(message.messageHash)
                    .append("\nRecipient: ")
                    .append(message.recipient)
                    .append("\nMessage: ")
                    .append(message.messageText)
                    .append("\n\n");
        }

        return result.toString().trim();
    }

    // KEEP PART 2 METHODS WORKING
    public String printMessages() {
        return displaySentReport();
    }

    public int returnTotalMessagess() {
        return totalMessages;
    }

    public int returnTotalMessages() {
        return totalMessages;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }
}
