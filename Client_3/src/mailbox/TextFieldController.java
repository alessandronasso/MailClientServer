/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class TextFieldController {
    
    @FXML
    private TextField id;
    @FXML
    private TextField mitt;
    @FXML
    private TextField dest;
    @FXML
    private TextField data;
    @FXML
    private TextField oggetto;

    private DataModel model ;

    public void initModel(DataModel model) throws IOException, ClassNotFoundException, ParseException {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
        model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if (oldEmail != null) {
                id.setText(Integer.toString(oldEmail.IDProperty().getValue().intValue()));
                mitt.setText(oldEmail.MittenteProperty().getValue());
                dest.setText(oldEmail.DestinatarioProperty().getValue());
                oggetto.setText(oldEmail.OggettoProperty().getValue()); 
                data.setText(new SimpleDateFormat("dd-MM-yyyy").format(oldEmail.getData()));
            }
            if (newEmail == null) {
                id.setText("");
                mitt.setText("");
                dest.setText("");
                data.setText("");
                oggetto.setText("");
            } else {
                id.setText(Integer.toString(newEmail.IDProperty().getValue().intValue()));
                mitt.setText(newEmail.MittenteProperty().getValue());
                dest.setText(newEmail.DestinatarioProperty().getValue());
                data.setText(new SimpleDateFormat("dd-MM-yyyy").format(newEmail.getData()));
                oggetto.setText(newEmail.OggettoProperty().getValue());  
            }
        });
    }
}
