package com.japanquickchat.japanquickchat;

import java.util.Scanner;

public class JapanQuickChat {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("================================");
        System.out.println("      Welcome to QuickChat");
        System.out.println("================================");

        // STEP 1: REGISTER USER
        System.out.println("\n--- USER REGISTRATION ---");

        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        System.out.print("Create a username (must contain _ and be no longer than 5 characters): ");
        String username = input.nextLine();

        System.out.print("Create a password (8+ characters, capital, number and special character): ");
        String password = input.nextLine();

        System.out.print("Enter cellphone number (example +27838968976): ");
        String cellphone = input.nextLine();

        Login login = new Login(
                firstName,
                lastName,
                username,
                password,
                cellphone
        );

        String registrationResult = login.registerUser();

        System.out.println("\n" + registrationResult);

        if (!registrationResult.startsWith(
                "Username successfully captured.")) {

            System.out.println(
                    "Registration failed. Please run the program again and try valid details."
            );

            input.close();
            return;
        }

        // STEP 2: LOGIN
        System.out.println("\n--- USER LOGIN ---");

        boolean loggedIn = false;

        for (int attempts = 1; attempts <= 3; attempts++) {

            System.out.print("Enter username: ");
            String enteredUsername = input.nextLine();

            System.out.print("Enter password: ");
            String enteredPassword = input.nextLine();

            if (login.loginUser(
                    enteredUsername,
                    enteredPassword)) {

                loggedIn = true;

                System.out.println("\n" + login.returnLoginStatus());
                break;

            } else {

                System.out.println(
                        "Username or password incorrect, please try again."
                );
            }
        }

        if (!loggedIn) {

            System.out.println(
                    "Too many failed login attempts. Goodbye!"
            );

            input.close();
            return;
        }

        // DEBUG CHECK
        System.out.println(
                "\nDEBUG: Login successful. Starting QuickChat menu."
        );

        // STEP 3: ASK HOW MANY MESSAGES
        int messageLimit = readNonNegativeInt(
                input,
                "\nHow many messages would you like to send? "
        );

        int messagesProcessed = 0;
        int messageNumber = 0;

        boolean running = true;

        // STEP 4: MAIN MENU
        while (running) {

            System.out.println("\n================================");
            System.out.println("          QUICKCHAT MENU");
            System.out.println("================================");
            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            String option = input.nextLine().trim();

            switch (option) {

                case "1":

                    if (messagesProcessed >= messageLimit) {
                        System.out.println(
                                "You have reached your message limit."
                        );
                        break;
                    }

                    System.out.print(
                            "Enter recipient cellphone number (+27 followed by 9 digits): "
                    );

                    String recipient = input.nextLine().trim();

                    System.out.print(
                            "Enter your message (maximum 250 characters): "
                    );

                    String messageText = input.nextLine();

                    if (messageText.length() > 250) {
                        System.out.println(
                                "Please enter a message of less than 250 characters."
                        );
                        break;
                    }

                    Message message = new Message(
                            recipient,
                            messageText,
                            messageNumber
                    );

                    String recipientResult =
                            message.checkRecipientCell();

                    if (!recipientResult.startsWith(
                            "Cell phone number successfully captured.")) {

                        System.out.println(recipientResult);
                        break;
                    }

                    if (!message.checkMessageID()) {
                        System.out.println("Invalid message ID.");
                        break;
                    }

                    System.out.println("\nMessage ready to send.");
                    System.out.println(
                            "Message ID: " + message.getMessageID()
                    );
                    System.out.println(
                            "Message Hash: " + message.getMessageHash()
                    );
                    System.out.println(
                            "Recipient: " + message.getRecipient()
                    );
                    System.out.println(
                            "Message: " + message.getMessageText()
                    );

                    System.out.println("\nWhat would you like to do?");
                    System.out.println("1. Send Message");
                    System.out.println("2. Disregard Message");
                    System.out.println("3. Store Message");
                    System.out.print("Choose an option: ");

                    String messageChoice = input.nextLine().trim();

                    String result = message.SentMessage(messageChoice);

                    System.out.println(result);

                    if (messageChoice.equals("1")
                            || messageChoice.equalsIgnoreCase("send")) {

                        messagesProcessed++;
                        messageNumber++;

                    } else if (messageChoice.equals("2")
                            || messageChoice.equalsIgnoreCase("disregard")
                            || messageChoice.equalsIgnoreCase("discard")) {

                        messagesProcessed++;
                        messageNumber++;

                    } else if (messageChoice.equals("3")
                            || messageChoice.equalsIgnoreCase("store")) {

                        if (result.equals(
                                "Message successfully stored.")) {

                            messagesProcessed++;
                            messageNumber++;
                        }
                    }

                    break;

                case "2":

                    System.out.println("\nComing Soon.");
                    break;

                case "3":

                    running = false;
                    System.out.println(
                            "\nThank you for using QuickChat!"
                    );
                    break;

                default:

                    System.out.println(
                            "Invalid option. Please choose 1, 2, or 3."
                    );
            }
        }

        System.out.println(
                "\nTotal messages successfully sent: "
                + new Message("", "", 0).returnTotalMessagess()
        );

        input.close();
    }

    private static int readNonNegativeInt(
            Scanner input,
            String prompt) {

        while (true) {

            System.out.print(prompt);

            String value = input.nextLine().trim();

            try {

                int number = Integer.parseInt(value);

                if (number >= 0) {
                    return number;
                }

                System.out.println(
                        "Please enter zero or a positive number."
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid whole number."
                );
            }
        }
    }
}