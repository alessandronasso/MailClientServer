/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.paint.Color;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class MenuBarController {

    private DataModel model;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label account;

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;

        account.setText(model.getAccountName());
        account.setTextFill(Color.web("#0076a3"));
    }

    @FXML
    public void elimina() throws IOException, InterruptedException {
        Email da_elim = model.currentEmailProperty().get();
        model.deleteMail(da_elim);
    }

    @FXML
    public void exit() throws IOException, InterruptedException {
        model.closeConnection();
        menuBar.getScene().getWindow().hide();
    }
}
