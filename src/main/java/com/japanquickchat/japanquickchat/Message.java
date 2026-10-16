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

    private static final List<Message> SENT_MESSAGES =
            new ArrayList<>();

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+27\\d{9}$");

    private static final Path JSON_FILE =
            Path.of("messages.json");

    public Message(String recipient, String messageText,
                   int messageNumber) {

        this.messageID = generateMessageID();
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    public Message(String messageID, int messageNumber,
                   String recipient, String messageText) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    private String generateMessageID() {
        long randomID = ThreadLocalRandom.current().nextLong(
                1_000_000_000L, 10_000_000_000L);

        return String.valueOf(randomID);
    }

    public boolean checkMessageID() {
        return messageID != null
                && messageID.length() <= 10;
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
        return messageText != null
                && messageText.length() <= 250;
    }

    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2
                || messageText == null
                || messageText.trim().isEmpty()) {
            return "";
        }

        String[] words = messageText.trim().split("\\s+");

        String firstWord = words[0].replaceAll(
                "[^a-zA-Z0-9]", "");

        String lastWord = words[words.length - 1].replaceAll(
                "[^a-zA-Z0-9]", "");

        return messageID.substring(0, 2)
                + ":" + messageNumber
                + ":" + (firstWord + lastWord).toUpperCase();
    }

    public String SentMessage(String choice) {
        if (!checkMessageLength()) {
            return "Please enter a message of less than 250 characters.";
        }

        if (!checkMessageID()) {
            return "Invalid message ID.";
        }

        if (!PHONE_PATTERN.matcher(
                recipient == null ? "" : recipient).matches()) {
            return checkRecipientCell();
        }

        if (choice == null) {
            return "Invalid option.";
        }

        switch (choice.trim().toLowerCase()) {
            case "1":
            case "send":
                if (!SENT_MESSAGES.contains(this)) {
                    SENT_MESSAGES.add(this);
                    totalMessages++;
                }
                return "Message successfully sent.";

            case "2":
            case "disregard":
            case "discard":
                return "Press O to delete the message.";

            case "3":
            case "store":
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";

            default:
                return "Invalid option.";
        }
    }

    public boolean storeMessage() {
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            List<Message> messages = new ArrayList<>();

            if (Files.exists(JSON_FILE)) {
                String existingJson =
                        Files.readString(JSON_FILE);

                if (!existingJson.trim().isEmpty()) {
                    Type listType =
                            new TypeToken<ArrayList<Message>>() {
                            }.getType();

                    List<Message> existingMessages =
                            gson.fromJson(existingJson, listType);

                    if (existingMessages != null) {
                        messages.addAll(existingMessages);
                    }
                }
            }

            messages.add(this);

            Files.writeString(
                    JSON_FILE,
                    gson.toJson(messages));

            return true;

        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    public String printMessages() {
        StringBuilder output = new StringBuilder();

        for (Message message : SENT_MESSAGES) {
            output.append("Message ID: ")
                    .append(message.messageID)
                    .append("\nMessage Hash: ")
                    .append(message.messageHash)
                    .append("\nRecipient: ")
                    .append(message.recipient)
                    .append("\nMessage: ")
                    .append(message.messageText)
                    .append("\n\n");
        }

        if (output.length() == 0) {
            return "No messages have been sent yet.";
        }

        return output.toString().trim();
    }

    public int returnTotalMessagess() {
        return totalMessages;
    }

    // Correctly spelled alternative for convenience.
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