/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.IOException;
import java.text.ParseException;
import javafx.scene.control.TextArea;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class TextAreaController {
    
    private DataModel model;
    public TextArea textarea;
    
    public void initModel(DataModel model) throws IOException, ClassNotFoundException, ParseException {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model ;
        model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if (oldEmail != null) {
                textarea.setText("Testo: "+(oldEmail.TestoProperty().getValue())+"\n");
                
            }
            if (newEmail == null) {
                textarea.setText("");
            } else {
                textarea.setText(newEmail.TestoProperty().getValue());
            }
        });
    }
}
