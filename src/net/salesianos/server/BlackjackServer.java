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
        System.out.println("Servidor Blackjack iniciado en el puerto " + Constants.SEVER_PORT);

        try (ServerSocket serverSocket = new ServerSocket(Constants.SEVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler player = new ClientHandler(clientSocket);
                players.add(player);
                sendGameRules(player);
                player.start();

                if (players.size() >= 2) {
                    startGame();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendGameRules(ClientHandler player) {
        player.sendMessage(
                """

                        ¡Bienvenido a Blackjack!
                        1. El objetivo es acercarse lo máximo posible a 21 sin pasarse.
                        2. Las cartas numéricas valen su valor nominal, las figuras (J, Q, K) valen 10 y los ases pueden valer 1 u 11.
                        3. Puedes pedir carta ('pedir') para tomar otra carta o 'plantarte' para mantener tu mano actual.
                        4. Si tu mano supera 21, pierdes (te pasas).
                        5. El crupier debe pedir carta hasta llegar al menos a 17.
                        6. El que más se acerque a 21 sin pasarse gana la ronda.
                        ¡Buena suerte!
                        """);
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
        broadcast("Primera carta del crupier: " + dealerHand.get(0));

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
        broadcast("Mano completa del crupier: " + dealerHand);
        determineWinners();
        endGame();
    }

    private static void determineWinners() {
        int dealerScore = calculateHandValue(dealerHand);
        List<ClientHandler> playersToRemove = new ArrayList<>();
        for (ClientHandler player : players) {
            int playerScore = calculateHandValue(player.getHand());
            if (playerScore > 21 || (dealerScore <= 21 && dealerScore >= playerScore)) {
                player.sendMessage("¡Has perdido! El crupier gana.");
            } else {
                player.sendMessage("¡Has ganado!");
            }
            player.closeConnection();
            playersToRemove.add(player);
        }
        players.removeAll(playersToRemove);
        playersFinished = 0;
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
        broadcast("¡Juego terminado!");
    }
}