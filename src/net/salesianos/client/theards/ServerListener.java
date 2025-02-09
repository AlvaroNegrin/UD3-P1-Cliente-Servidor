package net.salesianos.client.theards;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class ServerListener extends Thread {
    private DataInputStream inputStream;
    private boolean running = true;
    private String playerName;

    public ServerListener(DataInputStream inputStream, String playerName) {
        this.inputStream = inputStream;
        this.playerName = playerName;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            while (running) {
                String serverMessage = inputStream.readUTF();
                System.out.println("Servidor dice: " + serverMessage);

                if (serverMessage.contains("¡Juego terminado!") ||
                        serverMessage.contains("¡Has ganado!") ||
                        serverMessage.contains("¡Has perdido!")) {
                    running = false;
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("El servidor ha cerrado la conexión.");
            running = false;
        } catch (IOException e) {
            System.out.println("Error al recibir mensaje del servidor.");
            running = false;
        } finally {
            if (!running) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}