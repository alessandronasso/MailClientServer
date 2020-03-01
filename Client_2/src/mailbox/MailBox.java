/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailbox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Alessandro Nasso, Giancarlo Stefania
 */
public class MailBox extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("lista.fxml"));
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("menubar.fxml"));
        FXMLLoader textareaLoader = new FXMLLoader(getClass().getResource("textarea.fxml"));
        FXMLLoader fieldLoader = new FXMLLoader(getClass().getResource("textfield.fxml"));
        FXMLLoader buttonLoader = new FXMLLoader(getClass().getResource("button.fxml"));
        
        AnchorPane root = new AnchorPane(listLoader.load(), textareaLoader.load(), fieldLoader.load(), menuLoader.load(), buttonLoader.load());
        
        ListController listController = listLoader.getController();
        MenuBarController menuController = menuLoader.getController();
        TextAreaController textareaController = textareaLoader.getController();
        TextFieldController fieldController = fieldLoader.getController();
        ButtonController buttonController = buttonLoader.getController();

        DataModel model = new DataModel();
        listController.initModel(model);
        menuController.initModel(model);
        textareaController.initModel(model);
        fieldController.initModel(model);
        buttonController.initModel(model);

        Scene scene = new Scene(root, 603, 403);
        stage.setScene(scene);
        stage.setTitle("Client 2");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
