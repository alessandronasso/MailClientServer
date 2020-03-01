/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class ServerController {

    @FXML
    private TextArea textarea;

    private StringBuilder contenutoTextArea = new StringBuilder("");

    private ArrayList<String> userConnected = new ArrayList<String>();

    private ServerSocket s;

    /**
     * In questo metodo il server si mette in attesa e crea un thread per ogni
     * client che vuole instaurare una connessione. Mette a disposizione la
     * porta 5000 per fare cio'.
     */
    public void initModel() throws IOException {
        contenutoTextArea.append("Waiting for connections\n");
        textarea.setText(contenutoTextArea.toString());
        s = new ServerSocket(5000);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        new ThreadedEchoHandler(s);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
    }

    class ThreadedEchoHandler implements Runnable {

        private Socket incoming;

        private String nomeAccount = "";

        ThreadedEchoHandler(ServerSocket serv) throws IOException {
            incoming = serv.accept();
            new Thread(this).start();
        }

        /**
         * Metodo che viene eseguito nel momento in cui un client si connette.
         * Verifica innanzitutto chi ha stabilito la connessione per verificare
         * se si e' gia' connesso in passato oppure no, ed in base a quello
         * decide se deve fare il caricamento iniziale delle email o se deve
         * mettersi in attesa di sapere quale tipo di operazione vuole eseguire
         * il client.
         */
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
                try {
                    nomeAccount = in.readLine();
                } catch (IOException ex) {
                    System.out.println("Unable to read from client");
                }
            } catch (IOException ex) {
                System.out.println("Unable to read from client");
            }

            if (!alreadyConnected()) {
                contenutoTextArea.append(nomeAccount + " has connected! Address: " + incoming.getLocalAddress() + "\n");
                textarea.setText(contenutoTextArea.toString());
                try {
                    firstLoad();
                } catch (IOException ex) {
                    System.out.println("Cannot load data");
                }
                userConnected.add(nomeAccount);
            } else {
                String op;
                BufferedReader in;
                try {
                    in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
                    op = in.readLine();
                    try {
                        clientChoise(in, op);
                    } catch (NullPointerException e) {
                        System.out.println("");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("Unable to read messages");
                }
            }
        }

        /**
         * Primo caricamento dei dati. Qui il server esegue una lettura dei file
         * all'interno della cartella riferita all'utente che ha stabilito la
         * connessione ed estrapola, sotto forma di stringhe, tutti i contenuti
         * che inviera' successivamente al client.
         */
        public void firstLoad() throws IOException {
            File dir = new File("src/server/" + nomeAccount);
            String[] tmp = new String[100];
            int i = 0;
            for (File file : dir.listFiles()) {
                if (file.isFile() && !(file.getName().equals(".DS_Store"))) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            tmp[i++] = line;
                        }
                        br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.out.println("Cannot read from file");
                    }
                }
            }

            PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
            for (int j = 0; j < i; j++) {
                out.println(tmp[j]);
            }
            out.println("Fine");
        }

        /**
         * In questo metodo controllo se l'utente che ha stabilito la
         * connessione lo aveva gia' fatto in passato durante il ciclo di vita
         * del server.
         */
        public boolean alreadyConnected() {
            for (int i = 0; userConnected != null && i < userConnected.size(); i++) {
                if (userConnected.get(i).equals(nomeAccount)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * In questo metodo, al momento del logout da parte del client, rimuovo
         * l'utente dalla lista degli utenti connessi durante il ciclo di vita
         * del client.
         */
        public void removeUser() {
            for (int i = 0; userConnected != null && i < userConnected.size(); i++) {
                if (userConnected.get(i).equals(nomeAccount)) {
                    userConnected.remove(i);
                }
            }
        }

        /**
         * In questo metodo invio la mail al client. Dato che posso averne piu'
         * di uno li distinguo spezzettando la stringa che contiene l'elenco dei
         * destinatari.
         * @param mail array di stringhe contenente tutte le mail
         */
        public void sendMail(String[] mail) throws FileNotFoundException, UnsupportedEncodingException {
            mail[0] = mail[0].replaceAll("\\s+", "");
            String[] parts = mail[0].split(";");
            for (int i = 0; i < parts.length; i++) {
                File f = new File("src/server/" + parts[i]);
                if (f.exists() && f.isDirectory()) {
                    int id = sceltaID("src/server/" + parts[i]);
                    try (PrintWriter writer = new PrintWriter("src/server/" + parts[i] + "/" + id + ".txt", "UTF-8")) {
                        writer.println(Integer.toString(id));
                        writer.println(mail[1]);
                        writer.println(mail[2]);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date();
                        writer.println(dateFormat.format(date));
                        writer.println(mail[3]);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        /**
         * In questo metodo controllo il primo ID piu' basso disponibile nella
         * cartella dell'utente.
         *
         * @param dir nome della cartella dell'utente
         * @return l'ID estratto
         */
        public int sceltaID(String dir) {
            boolean check = true;
            int i = 0;
            while (check) {
                check = new File(dir, (Integer.toString(++i) + ".txt")).exists();
            }
            return i;
        }

        /**
         * In questo metodo effettuo l'operazione correlata alla scelta ce ha
         * effettuato il client.
         *
         * @param in il bufferedReader passatogli dal server stesso
         * @param op operazione scelta dal client
         */
        public void clientChoise(BufferedReader in, String op) throws IOException {
            if (op.equals("Elimina")) {
                String tmp = in.readLine();
                contenutoTextArea.append(nomeAccount + " has deleted the mail number ").append(tmp).append(". \n");
                textarea.setText(contenutoTextArea.toString());
                File file = new File("src/server/" + nomeAccount + "/" + tmp + ".txt");
                file.delete();
            } else if (op.equals("Scrivi")) {
                String[] mail = new String[4];
                for (int j = 0; j < 4; j++) {
                    mail[j] = in.readLine();
                }
                contenutoTextArea.append(nomeAccount + " has send an email.").append(". \n");
                textarea.setText(contenutoTextArea.toString());
                sendMail(mail);
            } else if (op.equals("Numb")) {
                int n = new File("src/server/" + nomeAccount).list().length - 1;
                PrintWriter scrivi = new PrintWriter(incoming.getOutputStream(), true);
                scrivi.println(n);
            } else if (op.equals("Reload")) {
                File dir = new File("src/server/" + nomeAccount);
                String[] tmp = new String[100];
                int i = 0;
                for (File file : dir.listFiles()) {
                    if (file.isFile() && !(file.getName().equals(".DS_Store"))) {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                tmp[i++] = line;
                            }
                            br.close();
                        } catch (IOException ex) {
                            System.out.println("Cannot read from file");
                        }
                    }
                }

                PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
                for (int j = 0; j < i; j++) {
                    out.println(tmp[j]);
                }
                out.println("Fine");
            } else if (op.equals("Fine")) {
                contenutoTextArea.append(nomeAccount + " has disconnected.\n");
                textarea.setText(contenutoTextArea.toString());
                removeUser();
            }
        }
    }
}
