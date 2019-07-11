package sudokuhex;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private BorderPane pai;

    @FXML
    private VBox vboxOpcoes, loginDados;

    @FXML
    private Button login, registo, estatisticas;

    boolean erroLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        login.setOnAction((event) -> {
            loginUser();
            estatisticas.setVisible(false);
        });
        registo.setOnAction((event) -> {
            registaUser();
            estatisticas.setVisible(false);
        });

        estatisticas.setOnAction((event) -> {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("VerEstatisticas.fxml"));
                Scene parentScene = new Scene(parent);
                parentScene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(parentScene);
                window.show();

            } catch (IOException ex) {
                Logger.getLogger(VerEstatisticasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    // Função responsável por verificar a validade de dados introduzidos com os de BD
    public void loginUser() {
        erroLogin = false;
        Button retroceder = new Button("Retroceder");
        retroceder.setMaxWidth(Double.MAX_VALUE);
        retroceder.setOnAction((eventoRetroceder) -> {
            login.setVisible(true);
            registo.setVisible(true);
            loginDados.getChildren().clear();
            estatisticas.setVisible(true);
        });
        Label erro = new Label();
        erro.setId("aviso");
        Label lNome = new Label("Nome");
        lNome.setId("categoria");
        Label lPassword = new Label("Password");
        lPassword.setId("categoria");
        TextField tNome = new TextField();
        PasswordField tPassword = new PasswordField();
        loginDados.getChildren().add(0, lNome);
        loginDados.getChildren().add(1, tNome);
        loginDados.getChildren().add(2, lPassword);
        loginDados.getChildren().add(3, tPassword);

        Button entrar = new Button("Entrar");
        entrar.setMaxWidth(Double.MAX_VALUE);
        entrar.setOnAction((eventoEntrar) -> {

            if (!(tNome.getText().trim().isEmpty())
                    && !(tPassword.getText().trim().isEmpty())) {
                try {
                    GestorBD gBD = new GestorBD();
                    boolean res = gBD.verificaPass(tNome.getText(), tPassword.getText());
                    if (res) {
                        try {

                            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("OpcoesJogo.fxml"));
                            Parent parent = (Parent) fxmlloader.load();
                            OpcoesJogoController mycont = (OpcoesJogoController) fxmlloader.getController();
                            Scene parentScene = new Scene(parent);
                            parentScene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
                            Stage window = (Stage) ((Node) eventoEntrar.getSource()).getScene().getWindow();
                            window.setScene(parentScene);
                            window.show();
                            Platform.runLater(() -> {
                                mycont.atualizaNome(tNome.getText());
                            });

                        } catch (IOException ex) {
                            Logger.getLogger(OpcoesJogoController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        if (erroLogin) {
                            loginDados.getChildren().remove(4);
                        }
                        erro.setText("Dados inválidos");
                        loginDados.getChildren().add(4, erro);
                        erroLogin = true;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (erroLogin) {
                    loginDados.getChildren().remove(4);
                }
                erro.setText("Campos vazios");
                loginDados.getChildren().add(4, erro);
                erroLogin = true;

            }
        });

        loginDados.getChildren().add(4, entrar);
        loginDados.getChildren().add(5, retroceder);
        Node nEntrar = loginDados.getChildren().get(4);
        loginDados.setMargin(nEntrar, new Insets(40, 0, 0, 0));
        Node nRetroceder = loginDados.getChildren().get(5);
        loginDados.setMargin(nRetroceder, new Insets(10, 0, 0, 0));
        login.setVisible(false);
        registo.setVisible(false);

    }

    // Função responsável por registar user na BD
    public void registaUser() {
        erroLogin = false;
        Button retroceder = new Button("Retroceder");
        retroceder.setMaxWidth(Double.MAX_VALUE);
        retroceder.setOnAction((eventoRetroceder) -> {
            login.setVisible(true);
            registo.setVisible(true);
            loginDados.getChildren().clear();
            estatisticas.setVisible(true);
        });
        Label erro = new Label();
        erro.setId("aviso");
        erro.setWrapText(true);
        erro.setTextAlignment(TextAlignment.CENTER);

        Label lNome = new Label("Nome");
        lNome.setId("categoria");
        Label lPassword = new Label("Password");
        lPassword.setId("categoria");
        Label lConfirmaPassword = new Label("Confirmar password");
        lConfirmaPassword.setId("categoria");
        TextField tNome = new TextField();
        PasswordField tPassword = new PasswordField();
        PasswordField tConfirmaPassword = new PasswordField();
        loginDados.getChildren().add(0, lNome);
        loginDados.getChildren().add(1, tNome);
        loginDados.getChildren().add(2, lPassword);
        loginDados.getChildren().add(3, tPassword);
        loginDados.getChildren().add(4, lConfirmaPassword);
        loginDados.getChildren().add(5, tConfirmaPassword);

        Button entrar = new Button("Registar");
        entrar.setMaxWidth(Double.MAX_VALUE);
        entrar.setOnAction((eventoEntrar) -> {

            // Todos os campos foram preenchidos
            if (!(tNome.getText().trim().isEmpty())
                    && !(tPassword.getText().trim().isEmpty())
                    && !(tConfirmaPassword.getText().trim().isEmpty())) {

                // Password introduzida é a mesma nos dois campos
                if (tConfirmaPassword.getText().equals(tPassword.getText())) {
                    try {
                        GestorBD gBD = new GestorBD();
                        boolean res = gBD.insereUser(tNome.getText(), tPassword.getText());
                        if (res) {
                            try {

                                FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("OpcoesJogo.fxml"));
                                Parent parent = (Parent) fxmlloader.load();
                                OpcoesJogoController mycont = (OpcoesJogoController) fxmlloader.getController();
                                Scene parentScene = new Scene(parent);
                                parentScene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
                                Stage window = (Stage) ((Node) eventoEntrar.getSource()).getScene().getWindow();
                                window.setScene(parentScene);
                                window.show();
                                Platform.runLater(() -> {
                                    mycont.atualizaNome(tNome.getText());
                                });

                            } catch (IOException ex) {
                                Logger.getLogger(OpcoesJogoController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            if (erroLogin) {
                                loginDados.getChildren().remove(6);
                            }
                            erro.setText("Utilizador já em uso!");
                            loginDados.getChildren().add(6, erro);
                            erroLogin = true;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } // Não tem a mesma password em ambos os campos
                else {
                    if (erroLogin) {
                        loginDados.getChildren().remove(6);
                    }
                    erro.setText("Passwords diferem");

                    loginDados.getChildren().add(6, erro);
                    erroLogin = true;
                }

                // Existem campos não preenchidos
            } else {
                if (erroLogin) {
                    loginDados.getChildren().remove(6);
                }
                erro.setText("Campos vazios");
                loginDados.getChildren().add(6, erro);
                erroLogin = true;

            }
        });

        loginDados.getChildren().add(6, entrar);
        loginDados.getChildren().add(7, retroceder);
        Node nEntrar = loginDados.getChildren().get(6);
        loginDados.setMargin(nEntrar, new Insets(40, 0, 0, 0));
        Node nRetroceder = loginDados.getChildren().get(7);
        loginDados.setMargin(nRetroceder, new Insets(10, 0, 0, 0));
        login.setVisible(false);
        registo.setVisible(false);
    }
}
