package net.salesianos.client.theards;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class ServerListener extends Thread {
    private DataInputStream inputStream;
    private boolean running = true;

    public ServerListener(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            while (running) {
                String serverMessage = inputStream.readUTF();
                System.out.println(serverMessage);

                if (serverMessage.contains("Game over")) {
                    running = false;
                }
            }
        } catch (EOFException e) {
            System.out.println("Server closed the connection.");
        } catch (IOException e) {
            System.out.println("Error receiving message.");
        } finally {
            running = false;
        }
    }
}
