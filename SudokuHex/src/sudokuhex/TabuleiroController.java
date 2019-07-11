package sudokuhex;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class TabuleiroController implements Initializable {

    @FXML
    public BorderPane bpParent;

    @FXML
    public GridPane GridPai;

    @FXML
    private TextField numNumeros;

    @FXML
    private Button geraPuzzle, pausaTempo, retroceder;

    @FXML
    private CheckBox ajudaMenu;

    @FXML
    private Label horas, minutos, segundos, nomeUser;

    @FXML
    private ComboBox nivelDificuldade;

    @FXML
    private VBox vBoxOpcoes;

    private int[][] matrizOriginal, matrizComEspacos, matrizRespUser;

    private int nGlobal;

    private Popup pop;

    private String labelID;

    private GeraPuzzle gera;

    private Timer timer;

    private int totSegundos, hh, mm, ss;

    private boolean bPausa, bPersonalizado;

    private String[] converteValores;

    private Label lext;

    private String dificuldade, nome;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        nomeUser.setId("categoria");
        horas.setId("temporizador");
        minutos.setId("temporizador");
        segundos.setId("temporizador");

        dificuldade = "Fácil";

        converteValores = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

        labelID = "";

        ObservableList<String> options
                = FXCollections.observableArrayList(
                        "Fácil",
                        "Médio",
                        "Difícil",
                        "Personalizado"
                );

        nivelDificuldade.getItems().addAll(options);
        nivelDificuldade.setValue("Fácil");

        nivelDificuldade.setOnMouseClicked((event) -> {
            atualizaJanela();
        });

        nivelDificuldade.valueProperty().addListener((observable) -> {
            if (nivelDificuldade.getValue().equals("Personalizado")) {
                bPersonalizado = true;
                numNumeros.setVisible(true);
                numNumeros.setPromptText("Células visíveis");
                numNumeros.setTooltip(new Tooltip("Insira o número de células que serão visíveis no puzzle gerado"));
            } else {
                bPersonalizado = false;
                numNumeros.setVisible(false);
            }
        });

        totSegundos = 0;
        bPausa = false;
        bPersonalizado = false;
        numNumeros.setId("personalizado");
        numNumeros.setVisible(false);
        numNumeros.setOnMouseClicked((event) -> {
            atualizaJanela();
        });
        comecaTimer();

        pausaTempo.setOnAction((event) -> {
            atualizaJanela();
            lext.setStyle("-fx-background-color: #dbdbdb; -fx-border-color: grey; -fx-border-width: 4px");
            lext.setDisable(false);
            vBoxOpcoes.setDisable(true);
            timer.cancel();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setGraphic(new ImageView(this.getClass().getResource("pause.png").toString()));
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResource("pause.png").toString()));
            alert.setOnCloseRequest((eventTime) -> {
                lext.setStyle("-fx-border-color: grey; -fx-border-width: 4px");
                lext.setDisable(true);
                vBoxOpcoes.setDisable(false);
                comecaTimer();
            });
            alert.setTitle("Pausa");
            alert.setHeaderText("Jogo interrompido.");
            alert.setContentText("Clique OK para retomar o jogo.");

            alert.showAndWait();

        });

        retroceder.setOnAction((event) -> {
            atualizaJanela();
            timer.cancel();
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("OpcoesJogo.fxml"));
                Scene parentScene = new Scene(parent);
                parentScene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(parentScene);
                window.show();

            } catch (IOException ex) {
                Logger.getLogger(OpcoesJogoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        matrizRespUser = new int[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                matrizRespUser[i][j] = -1;
            }
        }

        ajudaMenu.setSelected(false);
        ajudaMenu.setTooltip(new Tooltip("Serão apresentados apenas os números admissíveis a cada célula"));
        ajudaMenu.setOnAction((event) -> {

            atualizaJanela();
            if (!ajudaMenu.isSelected()) {
                enableBotoes();
            }
        });

        geraPuzzle.setOnAction((v) -> {
            Platform.runLater(() -> {
                atualizaJanela();
                labelID = "";
                totSegundos = 0;
                dificuldade = nivelDificuldade.getValue().toString();
                selecionaDif(dificuldade);

            });
        });

        GridPai.heightProperty().addListener((observable) -> {
            Platform.runLater(() -> {
                GridPai.setPrefWidth(GridPai.getHeight());
            });
        });

        pop = new Popup();
        GridPane opcoes = new GridPane();
        opcoes.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #bcbcbc; -fx-border-width: 2px");
        opcoes.setPadding(new Insets(6, 6, 6, 6));
        opcoes.setVgap(5);
        opcoes.setHgap(5);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Button opBut = new Button(String.valueOf(converteValores[4 * i + j]));
                opBut.setMinWidth(35);
                opBut.setMinHeight(35);
                opBut.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                opBut.setAlignment(Pos.CENTER);
                opBut.setOnAction((event) -> {
                    ((Label) GridPai.lookup("#" + labelID)).setText(opBut.getText());
                    String[] res = labelID.split("_");
                    try {
                        matrizRespUser[Integer.parseInt(res[0])][Integer.parseInt(res[1])] = Integer.parseInt(opBut.getText());
                    } catch (Exception e) {
                        matrizRespUser[Integer.parseInt(res[0])][Integer.parseInt(res[1])] = convertHex(opBut.getText());

                    } finally {

                        if (avaliaMatriz()) {
                            timer.cancel();
                            atualizaJanela();
                            bpParent.setDisable(true);
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setGraphic(new ImageView(this.getClass().getResource("trophy.png").toString()));
                            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage.getIcons().add(new Image(this.getClass().getResource("trophy.png").toString()));
                            alert.setOnCloseRequest((eventTime) -> {
                                bpParent.setDisable(false);
                            });
                            alert.setTitle("PARABÉNS!!!!");
                            alert.setHeaderText(null);
                            String tempo = horas.getText() + ":"
                                    + minutos.getText() + ":"
                                    + segundos.getText();
                            alert.setContentText(nome + ", o seu tempo foi "
                                    + tempo + ".");
                            alert.showAndWait();
                            GestorBD bd = new GestorBD();
                            try {
                                // para exemplificar o registo em base de dados
                                if (dificuldade.equals("Personalizado")) {
                                    dificuldade = "Difícil";
                                }
                                bd.insereRegisto(nome, tempo, dificuldade);
                            } catch (SQLException ex) {
                                Logger.getLogger(TabuleiroController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
                opcoes.add(opBut, j, i);
            }
        }

        pop.getContent().add(opcoes);

        bpParent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!GridPai.isHover()) {
                    atualizaJanela();
                }
            }
        });

    }

    // Função responsável por iniciar ou reiniciar o temporizador
    public void comecaTimer() {
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    totSegundos++;
                    hh = totSegundos / 3600;
                    mm = (totSegundos - hh * 3600) / 60;
                    ss = totSegundos - mm * 60 - hh * 3600;

                    if (hh < 10) {
                        horas.setText("0" + String.valueOf(hh));
                    } else {
                        horas.setText(String.valueOf(hh));
                    }

                    if (mm < 10) {
                        minutos.setText("0" + String.valueOf(mm));
                    } else {
                        minutos.setText(String.valueOf(mm));

                    }

                    if (ss < 10) {
                        segundos.setText("0" + String.valueOf(ss));
                    } else {
                        segundos.setText(String.valueOf(ss));
                    }
                });
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(tarefa, 0, 1000);
    }

    public void preencheGrelha() {

        String s = "";
        String style = "";

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                s = "";
                style = "-fx-border-color: #aaaaaa;";
                if (bPersonalizado) {
                    if (mostraNumero()) {
                        s = String.valueOf(converteValores[matrizOriginal[i][j]]);
                        matrizRespUser[i][j] = matrizOriginal[i][j];
                        style = "-fx-background-color: #E0F76A; -fx-border-color: #aaaaaa; -fx-font-weight: bold;";
                    }
                } else {
                    if (matrizComEspacos[i][j] != -1) {
                        s = String.valueOf(converteValores[matrizComEspacos[i][j]]);
                        matrizRespUser[i][j] = matrizComEspacos[i][j];
                        style = "-fx-background-color: #E0F76A; -fx-border-color: #aaaaaa; -fx-font-weight: bold;";
                    }
                }
                Label label = new Label(s);
                label.setId(i + "_" + j);
                label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);
                label.setStyle(style);
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            label.getBackground().isEmpty();
                            atualizaJanela();
                        } catch (Exception e) {

                            atualizaJanela();

                            String style = "-fx-background-color: #FFD2D2; -fx-border-color: #cacebb;";
                            label.setStyle(style);
                            labelID = label.getId();
                            String[] res = labelID.split("_");
                            if (ajudaMenu.isSelected()) {
                                avaliaNumeros(Integer.parseInt(res[0]), Integer.parseInt(res[1]));
                            }
                            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

                            pop.show(window);
                            Point2D point = label.localToScene(0.0, 0.0);
                            pop.setX(window.getX() + point.getX() + label.getWidth());
                            pop.setY(window.getY() + point.getY() + 2 * label.getHeight());
                        }
                    }
                });
                GridPai.add(label, j, i);
            }
        }
        preencheLinhas();
    }

    public void preencheLinhas() {

        for (int i = 0; i < 16; i += 4) {
            for (int j = 0; j < 16; j += 4) {
                Label lint = new Label();
                lint.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                lint.setStyle("-fx-border-color: grey; -fx-border-width: 2px");
                lint.setDisable(true);
                GridPai.add(lint, i, j, 4, 4);
            }
        }

        lext = new Label();
        lext.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        lext.setStyle("-fx-border-color: grey; -fx-border-width: 2px");
        lext.setDisable(true);
        GridPai.add(lext, 0, 0, 16, 16);

    }

    private boolean mostraNumero() {

        Random numero = new Random();
        double n = numero.nextDouble();
        if (n <= nGlobal / 256.0) {
            return true;
        }
        return false;
    }

    private void clearPuzzle() {

        GridPai.getChildren().clear();
        matrizRespUser = new int[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                matrizRespUser[i][j] = -1;
            }
        }
        enableBotoes();

    }

    // Função invocada ao clique de X da janela
    public void closeStage() {
        // Terminar threads
        timer.cancel();
        Platform.exit();
        System.exit(0);

    }

    public void avaliaNumeros(int iLinha, int iColuna) {
        boolean bFlag;
        int k = -1;
        GridPane opcoes = (GridPane) pop.getContent().get(0);
        for (Node but : opcoes.getChildren()) {
            Button b = (Button) but;
            b.setDisable(false);
            try {
                k = Integer.parseInt(b.getText());
            } catch (Exception e) {
                k = convertHex(b.getText());
            } finally {
                bFlag = gera.valorValido(matrizRespUser, iLinha, iColuna, k);
                if (!bFlag) {
                    b.setDisable(true);
                }
            }
        }
    }

    public void enableBotoes() {
        GridPane opcoes = (GridPane) pop.getContent().get(0);
        for (Node but : opcoes.getChildren()) {
            Button b = (Button) but;
            b.setDisable(false);
        }
    }

    public void atualizaJanela() {
        if (!labelID.equals("")) {
            pop.hide();
            ((Label) GridPai.lookup("#" + labelID)).setStyle("-fx-border-color: #aaaaaa;");
        }
    }

    public int convertHex(String s) {
        int id = -1;
        switch (s) {
            case "A":
                id = 10;
                break;
            case "B":
                id = 11;
                break;
            case "C":
                id = 12;
                break;
            case "D":
                id = 13;
                break;
            case "E":
                id = 14;
                break;
            case "F":
                id = 15;
                break;
        }
        return id;
    }

    public void selecionaDif(String d) {

        // Criar um puzzle personalizado, apagando tantas células quantas o user escolheu, aleatoriamente
        if (d.equals("Personalizado")) {
            try {
                nGlobal = Integer.parseInt(numNumeros.getText());
                gera.criaHexadoku();
                matrizOriginal = gera.getIaArray();
                bPersonalizado = true;
                clearPuzzle();
                preencheGrelha();
            } catch (Exception e) {
                Tooltip tooltip = new Tooltip("Número inválido");
                numNumeros.clear();
                numNumeros.setPromptText("Número inválido");
            }
        } // Atualizar tipo de dificuldade em GeraPuzzle e obter os puzzles deste
        else {
            gera.setsTipoPuzzle(d);
            gera.criaHexadoku();
            matrizOriginal = gera.getIaArray();
            matrizComEspacos = gera.puzzleGeradoDificuldade();
            clearPuzzle();
            preencheGrelha();
        }
    }

    public void atualizaNome(String nome) {
        this.nome = nome;
        nomeUser.setText(this.nome);
    }

    public void atualizaDificuldade(String dif) {
        this.dificuldade = dif;
        gera = new GeraPuzzle(dificuldade);
        gera.criaHexadoku();
        matrizOriginal = gera.getIaArray();
        matrizComEspacos = gera.puzzleGeradoDificuldade();
        preencheGrelha();
        nivelDificuldade.setValue(this.dificuldade);
    }

    public boolean avaliaMatriz() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (matrizOriginal[i][j] != matrizRespUser[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

}
