package echoserver;


import database.BankDatabase;

import java.net.*;
import java.io.*;

public class EchoServerThread implements Runnable {
    protected Socket socket;

    public EchoServerThread(Socket clientSocket) {
        this.socket = clientSocket;
    }


    public void run() {
        //Deklaracje zmiennych
        BufferedReader brinp = null;
        DataOutputStream out = null;
        String threadName = Thread.currentThread().getName();

        //inicjalizacja strumieni
        try {
            brinp = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
            );
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(threadName + "| Błąd przy tworzeniu strumieni " + e);
            return;
        }
        String line = null;
        boolean loggedIn = false;
        //pętla główna
        while (true) {
            try {
                line = brinp.readLine();
                // badanie warunku zakończenia pracy
                if ((line == null) || "quit".equals(line)) {
                    out.flush(); // Dodaj wywołanie flush() przed zakończeniem pracy
                    System.out.println(threadName + "| Zakończenie pracy z klientem: " + socket);
                    socket.close();
                    return;
                }
                switch (line) {
                    case "register":
                        try {
                            handleRegistration(brinp, out);
                            out.flush();
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "login":
                        try {
                            boolean loginSuccessful = handleUserLogin(brinp, out);
                            if (loginSuccessful) {
                                loggedIn = true;
                                out.writeBytes("Logowanie powiodlo sie\n");
                                out.writeBytes("""
                                        === Lista Operacji ===
                                        1. Wplata
                                        2. Wyplata
                                        3. Sprawdz srodki
                                        4. Przelew
                                        5. Informacji
                                        6. Wyloguj
                                        """);
                            } else {
                                out.writeBytes("Logowanie nie powiodlo sie\n");
                            }
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "wyloguj":
                        loggedIn = false;
                        out.writeBytes("Wylogowano pomyślnie\n");
                        out.flush();
                        break;

                    default:
                        if (!loggedIn) {
                            out.writeBytes("Musisz byc zalogowany!\n");
                            out.flush();
                            break;
                        } else {
                            switch (line) {
                                case "deposit":
                                    // implementacja wpłaty
                                    break;
                                case "withdraw":
                                    // implementacja wypłaty
                                    break;
                                case "balance":
                                        handleCheckBalance(out);
                                    break;
                                case "info":
                                    // implementacja info
                                    break;
                                case "transfer":
                                    // implementacja przelewu
                                    break;
                                default:
                                    out.writeBytes("Nieprawidlowe polecenie\n");
                                    out.flush();
                            }
                        }
                }
            } catch (IOException e) {
                System.out.println(threadName + "| Błąd wejścia-wyjścia." + e);
                return;
            }
        }
    }

    private void handleRegistration(BufferedReader brinp, DataOutputStream out) throws IOException {
        out.writeBytes("Podaj imie: \n");
        out.flush();
        String name = brinp.readLine();
        out.writeBytes("Podaj nazwisko: \n");
        out.flush();
        String lastName = brinp.readLine();
        out.writeBytes("Podaj login: \n");
        out.flush();
        String login = brinp.readLine();
        out.writeBytes("Podaj haslo: \n");
        out.flush();
        String password = brinp.readLine();
        out.writeBytes("Podaj adres: \n");
        out.flush();
        String address = brinp.readLine();
        out.writeBytes("Podaj numer telefonu: \n");
        out.flush();
        int phoneNumber = Integer.parseInt(brinp.readLine());
        BankDatabase.registerCustomer(name, lastName, login, password, address, phoneNumber);
        out.writeBytes("Utworzono uzytkownika!\n\r");
        out.flush();
    }

    private static void createAccount(String customerId) {
        BankDatabase.createCustomerAccount(customerId);
    }

    private boolean handleUserLogin(BufferedReader brinp, DataOutputStream out) throws IOException {
        out.writeBytes("Podaj login: \n");
        out.flush();
        String login = brinp.readLine();
        out.writeBytes("Podaj haslo: \n");
        out.flush();
        String password = brinp.readLine();
        out.flush();
        boolean loginSuccess = BankDatabase.checkCredentials(login, password);
        out.flush();

        return loginSuccess;
    }

    private void handleCheckBalance(DataOutputStream out) throws IOException {


    }
}
