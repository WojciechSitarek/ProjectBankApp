package echoserver;

import java.net.*;
import java.io.*;

public class Client2  {

    public static void main(String args[]) {
        String host = "localhost";
        int port = 0;
        try {
            port = new Integer("6666").intValue();
        } catch (NumberFormatException e) {
            System.out.println("Nieprawidłowy argument: port");
            System.exit(-1);
        }

        try {

            Socket clientSocket = new Socket(host, port);
            System.out.println("Połączono z " + clientSocket);

            Thread serverThread = new Thread(new ServerReader(clientSocket));
            serverThread.start();

            Thread userInputThread = new Thread(new UserInputHandler(clientSocket));
            userInputThread.start();

        } catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            System.exit(-1);
        }
    }

    static class ServerReader implements Runnable {
        private Socket socket;

        public ServerReader(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader brSockInp = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = brSockInp.readLine()) != null) {
//                    System.out.println("Otrzymano: " + line);
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Błąd wejścia-wyjścia: " + e);
                System.exit(-1);
            }
        }
    }

    // Klasa obsługująca wprowadzanie danych przez użytkownika
    static class UserInputHandler implements Runnable {
        private Socket socket;

        public UserInputHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader brLocalInp = new BufferedReader(
                        new InputStreamReader(System.in));
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String line;
                while ((line = brLocalInp.readLine()) != null && !"quit".equals(line)) {
                    System.out.println("Wysyłam: " + line);
                    out.writeBytes(line + '\n');
                    out.flush();
                }
                // Zamykanie połączenia
                System.out.println("Kończenie pracy...");
                socket.close();
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Błąd wejścia-wyjścia: " + e);
                System.exit(-1);
            }
        }
    }
}
