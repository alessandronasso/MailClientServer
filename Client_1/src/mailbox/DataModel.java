/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class DataModel {

    private final String account = "alessandro@gmail.com";

    private ObservableList<Email> emailList = FXCollections.observableArrayList(email
            -> new Observable[]{email.IDProperty(), email.MittenteProperty()});

    private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);

    public ObjectProperty<Email> currentEmailProperty() {
        return currentEmail;
    }

    public final Email getCurrentEmail() {
        return currentEmailProperty().get();
    }

    public final String getAccountName() {
        return account;
    }

    public final void setCurrentEmail(Email email) {
        currentEmailProperty().set(email);
    }

    public ObservableList<Email> getEmailList() {
        return emailList;
    }

    private Socket s;

    private PrintWriter out;

    private BufferedReader in;

    /**
     * In questo metodo il client effettua la connessione al server sulla porta
     * 5000. In caso non dovesse trovarlo fa apparire un messaggio di errore.
     */
    public void connect() throws IOException, InterruptedException {
        try {
            s = new Socket("127.0.0.1", 5000);
            out = new PrintWriter(s.getOutputStream(), true);
            out.println(account + "\n");
            Thread.sleep(150);
        } catch (IOException io) {
            new Alert(Alert.AlertType.INFORMATION, "Non trovo il server!").showAndWait();
            System.out.println("Unable to connect");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * In questo metodo eseguo la chiusura della socket.
     */
    public void closeSocket() throws IOException {
        s.close();
    }

    private int numberOfEmail = 0;

    public int getNumberOfEmail() {
        return numberOfEmail;
    }

    /**
     * In questo metodo il client effettua il primo caricamento dei dati.
     * Innanzitutto si identifica, in modo tale che il server possa andare a
     * recuperare le email nella cartella corretta, e successivamente si mette
     * in attesa di ricevere i contenuti, che una volta arrivati verrano
     * ordinati ed inseriti a tutti gli effetti.
     */
    public void loadData() throws IOException, ClassNotFoundException, ParseException, InterruptedException {
        try {
            connect();
            ArrayList<Email> email = new ArrayList<Email>();
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy", new Locale("it", "IT"));
            Date data;

            out.println(account + "\n");

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            String message[] = new String[5];
            for (int j = 0; ((line = in.readLine()) != null) && (!line.equals("Fine"));) {
                message[j++] = line;
                if (j == 5) {
                    data = format.parse(message[3]);
                    email.add(new Email((Integer.parseInt(message[0])), message[1], account, message[2], message[4], data));
                    numberOfEmail++;
                    j = 0;
                }
            }

            emailList = FXCollections.observableArrayList(email);

            Collections.sort(emailList, (Email o1, Email o2) -> {
                if (o1.getData() == null || o2.getData() == null) {
                    return 0;
                }
                return o1.getData().compareTo(o2.getData());
            });
        } catch (SocketException se) {
            emailList.setAll(null, null);
        }
        s.close();
    }

    /**
     * In questo metodo il client effettua nuovamente il caricamento dei dati.
     * Esegue cio' quando vi sono nuove mail.
     */
    public void reLoadData() throws ParseException, IOException, InterruptedException {
        connect();
        out.println("Reload");
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String line;
        String message[] = new String[5];
        ArrayList<Email> email = new ArrayList<Email>();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", new Locale("it", "IT"));
        Date data;
        for (int j = 0; ((line = in.readLine()) != null) && (!line.equals("Fine"));) {
            message[j++] = line;
            if (j == 5) {
                data = format.parse(message[3]);
                email.add(new Email((Integer.parseInt(message[0])), message[1], account, message[2], message[4], data));
                j = 0;
            }
        }

        emailList = FXCollections.observableArrayList(email);

        Collections.sort(emailList, (Email o1, Email o2) -> {
            if (o1.getData() == null || o2.getData() == null) {
                return 0;
            }
            return o1.getData().compareTo(o2.getData());
        });
        s.close();
    }

    /**
     * In questo metodo il client elimina la mail desiderata dalla propria lista
     * ed invia successivamente al server l'ID in modo tale che ench'esso possa
     * eliminarla a tutti gli effetti.
     */
    public void deleteMail(Email da_elim) throws IOException, InterruptedException {
        connect();
        int id_del = da_elim.getID();
        emailList.remove(da_elim);
        out.println("Elimina");
        Thread.sleep(100);
        out.println(Integer.toString(id_del));
        s.close();
    }

    /**
     * In questo metodo il client comunica che vuole eseguire una disconnesione.
     */
    public void closeConnection() throws IOException, InterruptedException {
        connect();
        out.println("Fine");
        s.close();
    }

    /**
     * In questo metodo il client comunica al server che e' pronto ad inviare
     * una mail e successivamente si prepara a mandargli una serie di stringhe
     * che corrispondono ai contenuti di essa.
     *
     * @param mail l'array con i campi della mail
     */
    public void writeMail(String[] mail) throws IOException, InterruptedException {
        connect();
        out.println("Scrivi");
        Thread.sleep(100);
        for (int j = 0; j < mail.length; j++) {
            out.println(mail[j]);
        }
        s.close();
    }

    /**
     * In questo metodo il client chiede al server quante email vi sono nella
     * cartella correlata all'utente.
     *
     * @return il numero di email sul server
     */
    public int askNumbOfEmail() throws IOException, InterruptedException {
        connect();
        out.println("Numb");
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        int NoE = Integer.parseInt(in.readLine());
        s.close();
        return NoE;
    }
}
