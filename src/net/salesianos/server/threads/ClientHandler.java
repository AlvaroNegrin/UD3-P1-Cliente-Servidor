package net.salesianos.server.threads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.salesianos.server.BlackjackServer;

public class ClientHandler extends Thread {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String playerName;
    private List<String> hand = new ArrayList<>();
    private boolean running = true;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            playerName = input.readUTF();
            BlackjackServer.broadcast(playerName + " has joined the game!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to " + playerName + ". Disconnecting...");
            closeConnection();
        }
    }

    public void giveCard(String card) {
        hand.add(card);
        sendMessage("You received: " + card);
    }

    public void showHand() {
        sendMessage("Your hand: " + hand);
    }

    public List<String> getHand() {
        return hand;
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }

    public void closeConnection() {
        try {
            this.running = false;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                String action = input.readUTF();
                if (action.equalsIgnoreCase("hit")) {
                    String card = BlackjackServer.drawCardFromDeck();
                    giveCard(card);
                    showHand();
                    if (BlackjackServer.calculateHandValue(hand) > 21) {
                        sendMessage("You busted!");
                        closeConnection();
                        BlackjackServer.playerFinishedTurn();
                        return;
                    }
                } else if (action.equalsIgnoreCase("stand")) {
                    sendMessage("You chose to stand.");
                    BlackjackServer.playerFinishedTurn();
                    return;
                }
                BlackjackServer.broadcast(playerName + " chooses: " + action);
            }
        } catch (IOException e) {
            if (running) {
                System.out.println(playerName + " has left the game.");
            }
        }
    }
}
