/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class ButtonController {

    @FXML
    private Button scrivi;

    @FXML
    private Button reply;

    @FXML
    private Button replyall;

    @FXML
    private Button forward;

    private DataModel model;
    
    private PanelController pc;

    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
        model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if (oldEmail != null) {
                reply.setDisable(false);
                forward.setDisable(false);
            }
            if (newEmail == null) {
                reply.setDisable(true);
                forward.setDisable(true);
                replyall.setDisable(true);
            } else {
                reply.setDisable(false);
                forward.setDisable(false);
                replyall.setDisable(false);
            }
        });
    }

    @FXML
    public void creaFinestra() throws IOException {
        FXMLLoader pagina = new FXMLLoader(getClass().getResource("panel.fxml"));
        BorderPane root = new BorderPane(pagina.load());
        pc = pagina.getController();
        pc.initModel(model);
        Scene scene = new Scene(root, 315, 383);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    public void finestraRispondi () throws IOException {
        creaFinestra();
        pc.initRispondi(model);
    }
    
    @FXML
    public void finestraReplyAll () throws IOException {
        creaFinestra();
        pc.initReplyAll(model);
    }
    
    @FXML
    public void finestraForward () throws IOException {
        creaFinestra();
        pc.initForward(model);
    }
}
