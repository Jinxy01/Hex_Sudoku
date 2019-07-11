
package sudokuhex;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SudokuHex extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("HexSudoku");
        scene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
        stage.setResizable(false);
        Image applicationIcon = new Image(getClass().getResourceAsStream("/sudokuhex/icon.jpg"));
        stage.getIcons().add(applicationIcon);
        
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

}
