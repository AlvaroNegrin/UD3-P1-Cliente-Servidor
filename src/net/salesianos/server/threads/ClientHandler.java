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
            BlackjackServer.broadcast(playerName + " se ha unido al juego!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            System.out.println("Error al enviar mensaje a " + playerName + ". Desconectando...");
            closeConnection();
        }
    }

    public void giveCard(String card) {
        hand.add(card);
        sendMessage("Has recibido: " + card);
    }

    public void showHand() {
        sendMessage("Tu mano: " + hand);
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
                if (action.equalsIgnoreCase("pedir")) {
                    String card = BlackjackServer.drawCardFromDeck();
                    giveCard(card);
                    showHand();
                    if (BlackjackServer.calculateHandValue(hand) > 21) {
                        sendMessage("¡Te has pasado!");
                        closeConnection();
                        BlackjackServer.playerFinishedTurn();
                        return;
                    }
                } else if (action.equalsIgnoreCase("plantarse")) {
                    sendMessage("Has elegido plantarte.");
                    BlackjackServer.playerFinishedTurn();
                    return;
                }
                BlackjackServer.broadcast(playerName + " elige: " + action);
            }
        } catch (IOException e) {
            if (running) {
                System.out.println(playerName + " ha abandonado el juego.");
            }
            closeConnection();
        }
    }
}