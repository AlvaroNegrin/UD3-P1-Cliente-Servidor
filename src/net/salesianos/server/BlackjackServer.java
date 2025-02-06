package net.salesianos.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.salesianos.decks.Deck;
import net.salesianos.server.threads.ClientHandler;
import net.salesianos.utils.Constants;

public class BlackjackServer {
    private static List<ClientHandler> players = new ArrayList<>();
    private static Deck deck = new Deck();
    private static List<String> dealerHand = new ArrayList<>();
    private static int playersFinished = 0;

    public static void main(String[] args) {
        System.out.println("Blackjack Server started on port " + Constants.SEVER_PORT);
        System.out.println(
                """
                        \n
                            Welcome to Blackjack!
                            1. The goal is to get as close to 21 as possible without exceeding it.
                            2. Number cards are worth their face value, face cards (J, Q, K) are worth 10, and Aces can be 1 or 11.
                            3. You can 'hit' to take another card or 'stand' to keep your current hand.
                            4. If your hand exceeds 21, you lose (bust).
                            5. The dealer must hit until reaching at least 17.
                            6. The closest to 21 without going over wins the round.
                            Good luck!
                            """);

        try (ServerSocket serverSocket = new ServerSocket(Constants.SEVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler player = new ClientHandler(clientSocket);
                players.add(player);
                player.start();

                if (players.size() >= 2) {
                    startGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message) {
        players.removeIf(player -> !player.isConnected());
        for (ClientHandler player : players) {
            player.sendMessage(message);
        }
    }

    public static void startGame() {
        deck.shuffle();
        dealerHand.clear();
        dealerHand.add(deck.drawCard());
        dealerHand.add(deck.drawCard());
        broadcast("Dealer's first card: " + dealerHand.get(0));

        for (ClientHandler player : players) {
            player.giveCard(deck.drawCard());
            player.giveCard(deck.drawCard());
            player.showHand();
        }
    }

    public static String drawCardFromDeck() {
        return deck.drawCard();
    }

    public static synchronized void playerFinishedTurn() {
        playersFinished++;
        if (playersFinished == players.size()) {
            dealerTurn();
        }
    }

    public static void dealerTurn() {
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(deck.drawCard());
        }
        broadcast("Dealer's full hand: " + dealerHand);
        determineWinners();
        endGame();
    }

    private static void determineWinners() {
        int dealerScore = calculateHandValue(dealerHand);
        for (ClientHandler player : players) {
            int playerScore = calculateHandValue(player.getHand());
            if (playerScore > 21 || (dealerScore <= 21 && dealerScore >= playerScore)) {
                player.sendMessage("You lose! Dealer wins.");
            } else {
                player.sendMessage("You win!");
            }
        }
    }

    public static int calculateHandValue(List<String> hand) {
        int value = 0;
        int aceCount = 0;
        for (String card : hand) {
            if (card.equals("A")) {
                aceCount++;
                value += 11;
            } else if (card.equals("J") || card.equals("Q") || card.equals("K")) {
                value += 10;
            } else {
                value += Integer.parseInt(card);
            }
        }
        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }
        return value;
    }

    private static void endGame() {
        broadcast("Game over! Closing connections...");
        for (ClientHandler player : players) {
            player.closeConnection();
        }
        players.clear();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
