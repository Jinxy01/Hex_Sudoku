package sudokuhex;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class OpcoesJogoController implements Initializable {

    @FXML
    private BorderPane opcoes;

    @FXML
    private Button facil, medio, dificil, retroceder;

    @FXML
    private Label titulo;

    private String nome;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        nome = "";
        
        facil.setText("Fácil");
        medio.setText("Médio");
        dificil.setText("Difícil");

        facil.setOnAction((eventFacil) -> {
            iniciaJogo(nome, "Fácil", eventFacil);
        });

        medio.setOnAction((eventMedio) -> {
            iniciaJogo(nome, "Médio", eventMedio);
        });

        dificil.setOnAction((eventDificil) -> {
            iniciaJogo(nome, "Difícil", eventDificil);
        });

        retroceder.setOnAction((eventRetroceder) -> {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Scene parentScene = new Scene(parent);
                parentScene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
                Stage window = (Stage) ((Node) eventRetroceder.getSource()).getScene().getWindow();
                window.setScene(parentScene);
                window.show();

            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void iniciaJogo(String nome, String dif, Event event) {
        try {

            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("Tabuleiro.fxml"));
            Parent parent = (Parent) fxmlloader.load();
            TabuleiroController mycont = (TabuleiroController) fxmlloader.getController();

            Scene parentScene = new Scene(parent);
            parentScene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(parentScene);
            window.show();
            Platform.runLater(() -> {
                mycont.atualizaDificuldade(dif);
                mycont.atualizaNome(nome);
            });
            // Enviar para controller stage
            window.setOnCloseRequest((WindowEvent t) -> {
                Platform.runLater(() -> {
                    mycont.closeStage();
                });
            });

        } catch (IOException ex) {
            Logger.getLogger(TabuleiroController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void atualizaNome(String nome) {
        this.nome = nome;
    }
}
