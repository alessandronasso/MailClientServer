/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class ListController {

    @FXML
    private ListView<Email> listView;
    private DataModel model;
    public int emailForClient;
    public Stage owner;

    public void initModel(DataModel model) throws IOException, ClassNotFoundException, ParseException, InterruptedException {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;

        try {
            model.loadData();
        } catch (NullPointerException np) {
            System.out.println("");
        }
    
        listView.setItems(model.getEmailList());

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection)
                -> model.setCurrentEmail(newSelection));
        model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if (newEmail == null) {
                listView.getSelectionModel().clearSelection();
            } else {
                listView.getSelectionModel().select(newEmail);
            }
        });

        listView.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            public void updateItem(Email mail, boolean empty) {
                super.updateItem(mail, empty);
                if (empty) {
                    setText(null);
                } else if (mail != null) {
                    setText(mail.getMittente());
                }
            }
        });
        emailForClient = model.getNumberOfEmail();
        Thread updateUI = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ListController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        int currentOnServer = model.askNumbOfEmail();
                        if (emailForClient != currentOnServer) {
                            model.reLoadData();
                            int currentEmail = emailForClient;
                            Platform.runLater(() -> {
                                listView.setItems(model.getEmailList());
                                if (currentOnServer > currentEmail) {
                                    owner = (Stage) listView.getScene().getWindow();
                                    Alert alt = new Alert(Alert.AlertType.INFORMATION, "You just received an email!");
                                    alt.initModality(Modality.APPLICATION_MODAL);
                                    alt.initOwner(owner);
                                    alt.showAndWait();
                                }
                            });
                            emailForClient = currentOnServer;
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        Thread.currentThread().interrupt();
                        return;
                    } catch (ParseException ex) {
                        System.out.println("ParseException ERROR!");
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedException ERROR!");
                    }
                }
            }
        };
        updateUI.setDaemon(true);
        updateUI.start();
    }
}
