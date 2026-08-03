package com.github.mzehetmeyr.javabooleananalyzer;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;

import java.util.List;

public class MainController {

    @FXML
    private TextField expressionInput;

    @FXML
    protected void onAnalyzeButtonClick() {
        String expression = expressionInput.getText().trim();

        if (expression.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Por favor, digite uma expressão booleana.");
            return;
        }

        try {
            BooleanEvaluator evaluator = new BooleanEvaluator();
            BooleanEvaluator.TruthTableData data = evaluator.generateTableData(expression);

            showTableWindow(expression, data);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    private void showTableWindow(String expression, BooleanEvaluator.TruthTableData data) {
        TableView<List<String>> table = new TableView<>();

        for (int i = 0; i < data.headers.size(); i++) {
            final int colIndex = i;
            TableColumn<List<String>, String> column = new TableColumn<>(data.headers.get(i));
            column.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().get(colIndex)));

            column.setStyle("-fx-alignment: CENTER;");
            column.setMinWidth(80);
            table.getColumns().add(column);
        }

        table.getItems().addAll(data.rows);

        Label titleLabel = new Label("Expressão Analisada: " + expression);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setPadding(new Insets(10, 10, 10, 10));

        VBox root = new VBox();
        root.getChildren().addAll(titleLabel, table);

        VBox.setVgrow(table, Priority.ALWAYS);

        Stage stage = new Stage();
        stage.setTitle("Tabela Verdade");

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}