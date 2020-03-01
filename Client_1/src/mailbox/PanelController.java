/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class PanelController {

    @FXML
    private TextField dest;
    @FXML
    private TextField obj;
    @FXML
    private TextArea testo;
    @FXML
    private Button invia;

    private DataModel model;

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
    }

    public void initRispondi(DataModel model) {
        dest.setText(model.currentEmailProperty().getValue().getMittente());
        dest.setDisable(true);
    }

    public void initReplyAll(DataModel model) {
        final ObservableList<Email> elenco = model.getEmailList();
        String s = "";
        for (Email mail : elenco) {
            s+=""+mail.MittenteProperty().getValue()+"; ";
        }
        dest.setText(s);
        dest.setDisable(true);
    }
    
    public void initForward(DataModel model) {
        obj.setText(model.currentEmailProperty().getValue().getOggetto());
        testo.setText(model.currentEmailProperty().getValue().getTesto());
    }
    
    @FXML
    public void scrivi() throws IOException, InterruptedException {
        String[] da_inv = new String[4];
        da_inv[0] = dest.getText();
        da_inv[1] = model.getAccountName();
        da_inv[2] = obj.getText();
        da_inv[3] = testo.getText();
        boolean ok = true;
        for (int i=0; i<4 && ok; i++) {
            if (da_inv[i].length()<1)
                ok = false;
        }
        if (!ok)
            new Alert(Alert.AlertType.ERROR, "Compila tutti i campi!").showAndWait();
        else {
            model.writeMail(da_inv);
            Window stage = dest.getScene().getWindow();
            stage.hide();
        }
    }
}
