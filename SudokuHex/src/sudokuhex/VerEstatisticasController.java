package sudokuhex;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VerEstatisticasController implements Initializable {

    @FXML
    public BorderPane estatisticas;

    @FXML
    private VBox conteudo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            verEstatistica();
        } catch (SQLException ex) {
            Logger.getLogger(VerEstatisticasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verEstatistica() throws SQLException {

        Label titulo = new Label("Estatística");
        titulo.setId("subtitulo");
        Label categoriaFacil = new Label("Categoria: Fácil");
        categoriaFacil.setId("categoria");
        Label categoriaMedio = new Label("Categoria: Médio");
        categoriaMedio.setId("categoria");
        Label categoriaDificil = new Label("Categoria: Difícil");
        categoriaDificil.setId("categoria");
        Button retorna = new Button("Retornar");
        retorna.setOnAction((event) -> {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Scene parentScene = new Scene(parent);
                parentScene.getStylesheets().add(getClass().getResource("HexSudokuCSS.css").toString());
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(parentScene);
                window.show();

            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        conteudo.getChildren().addAll(titulo,
                categoriaFacil, carregaTabela("Fácil"),
                categoriaMedio, carregaTabela("Médio"),
                categoriaDificil, carregaTabela("Difícil"), retorna);

    }

    public TableView carregaTabela(String dif) throws SQLException {
        GestorBD gera = new GestorBD();
        ArrayList<Estatistica> alRegisto = new ArrayList<>();
        alRegisto = gera.devolveRegisto(dif);

        ObservableList<Estatistica> listaRegistos
                = FXCollections.observableArrayList(alRegisto);

        TableView table = new TableView();

        TableColumn nomeCol = new TableColumn("Nome");
        nomeCol.setPrefWidth(100);
        nomeCol.setStyle("-fx-alignment: CENTER;");
        nomeCol.prefWidthProperty().bind(table.widthProperty().divide(2).subtract(2));
        nomeCol.setCellValueFactory(
                new PropertyValueFactory<>("nome"));

        TableColumn tempoCol = new TableColumn("Tempo");
        tempoCol.setPrefWidth(100);
        tempoCol.setStyle("-fx-alignment: CENTER;");
        tempoCol.prefWidthProperty().bind(table.widthProperty().divide(2).subtract(2));
        tempoCol.setCellValueFactory(
                new PropertyValueFactory<>("tempo"));

        table.setItems(listaRegistos);
        table.getColumns().addAll(nomeCol, tempoCol);
        table.setPrefHeight(150);
        table.setPrefWidth(200);

        return table;
    }

}
