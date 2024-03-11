package echoserver;

import database.BankDatabase;

import java.net.*;
import java.io.*;

public class EchoServerThread implements Runnable {
    protected Socket socket;
    private String loggedInUser;
    private String loggedInAccountNumber;

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
                                loggedInAccountNumber = BankDatabase.getAccountNumberByLogin(loggedInUser);
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
                        loggedInUser = null;
                        loggedInAccountNumber = null;
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
                                    handleDeposit(brinp,out);
                                    break;
                                case "withdraw":
                                    handleWithdrawal(out,brinp);
                                    break;
                                case "balance":
                                    handleCheckBalance(out);
                                    break;
                                case "info":
                                    // implementacja info
                                    break;
                                case "transfer":
                                    handleTransfer(out,brinp);
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
        String ownerId = String.valueOf(BankDatabase.getCustomerIdByLogin(loggedInUser));
        BankDatabase.createCustomerAccount(ownerId,BankDatabase.getCustomerIdByLogin(login));
        out.writeBytes("Utworzono uzytkownika!\n\r");
        out.flush();
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
        if (loginSuccess) {
            loggedInUser = login;
            loggedInAccountNumber = BankDatabase.getAccountNumberByLogin(loggedInUser);
        }
        return loginSuccess;
    }

    private void handleCheckBalance(DataOutputStream out) throws IOException {
        if (loggedInUser != null) {
            double balance = BankDatabase.getBalanceByLogin(loggedInUser);
            out.writeBytes("Aktualny stan konta: " + balance + "\n");
            out.flush();
        } else {
            out.writeBytes("Nie jestes zalogowany!\n");
            out.flush();
        }
    }

    private void handleDeposit(BufferedReader brinp, DataOutputStream out) throws IOException {
        if (loggedInAccountNumber != null) {
            out.writeBytes("Podaj kwote: \n");
            out.flush();
            String depositAmountString = brinp.readLine();
            BankDatabase.makePayment(loggedInAccountNumber, Integer.parseInt(depositAmountString));

            out.writeBytes("Wplata pomyslnie zrealizowana.\n");
        } else {
            out.writeBytes("Nie jestes zalogowany lub numer konta jest nieprawidlowy!\n");
        }
        out.flush();
    }
    private void handleWithdrawal(DataOutputStream out,BufferedReader brinp ) throws IOException {
        if (loggedInAccountNumber != null) {
            out.writeBytes("Podaj kwote");
            out.flush();
            String withdrawalAmount = brinp.readLine();
            BankDatabase.PaymentProcessor.makePaycheck((loggedInAccountNumber), Double.parseDouble(withdrawalAmount));
            out.writeBytes("Wypłata pomyślnie zrealizowana.\n");
        } else {
            out.writeBytes("Nie jesteś zalogowany lub numer konta jest nieprawidłowy!\n");
        }
        out.flush();
    }

    private void handleTransfer(DataOutputStream out,BufferedReader brinp ) throws IOException {
        if (loggedInAccountNumber != null) {
            out.writeBytes("Podaj kwote: \n");
            out.flush();
            String transferAmount = brinp.readLine();
            out.writeBytes("Podaj docelowy numer konta: \n");
            out.flush();
            String destinationAccountNumber = brinp.readLine();
            BankDatabase.makeTransfer((loggedInAccountNumber), destinationAccountNumber, Double.parseDouble(transferAmount));
            out.writeBytes("Przelew pomyślnie zrealizowany.\n");
        } else {
            out.writeBytes("Nie jesteś zalogowany lub numer konta jest nieprawidłowy!\n");
        }
        out.flush();
    }
}
