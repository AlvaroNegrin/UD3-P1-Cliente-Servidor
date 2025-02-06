package net.salesianos.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import net.salesianos.client.theards.ServerListener;
import net.salesianos.utils.Constants;

public class PlayerClient {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter your name:");
            String name = scanner.nextLine();
            Socket socket = new Socket("localhost", Constants.SEVER_PORT);
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            outputStream.writeUTF(name);
            outputStream.flush();

            ServerListener serverListener = new ServerListener(inputStream);
            serverListener.start();

            while (serverListener.isRunning()) {
                System.out.print("-> ");
                String message = scanner.nextLine();

                if (!serverListener.isRunning()) {
                    break;
                }

                outputStream.writeUTF(message);
                outputStream.flush();
            }

            System.out.println("Connection closed. Exiting game.");
            socket.close();
        } catch (IOException e) {
            System.out.println("Error connecting to the server.");
        }
    }
}