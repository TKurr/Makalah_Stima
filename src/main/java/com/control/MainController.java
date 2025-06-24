package com.control;

import java.io.File;

import com.datastruct.Grid;
import com.logic.ReadFile;
import com.logic.Solver;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private GridPane gridPane;
    @FXML
    private Button solveButton;
    @FXML
    private ComboBox<String> algorithmChoice;
    @FXML
    private Label timeLabel;

    private Grid loadedGrid;

    private static final int CELL_SIZE = 40;

    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE,
            Color.DARKCYAN, Color.DARKORANGE, Color.BROWN, Color.MAGENTA, Color.DARKGREEN,
            Color.PINK, Color.LIMEGREEN, Color.DARKRED, Color.INDIGO, Color.DARKMAGENTA,
            Color.DARKBLUE, Color.CORAL, Color.TEAL, Color.DEEPPINK, Color.SADDLEBROWN,
            Color.LIGHTSEAGREEN, Color.OLIVE, Color.STEELBLUE, Color.DARKVIOLET, Color.GOLD
    };

    @FXML
    public void initialize() {
        algorithmChoice.getItems().addAll("DFS", "UCS", "GBFS", "A*");
        algorithmChoice.setValue("A*"); 
    }

    public void onChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File Puzzle");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) gridPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                ReadFile reader = new ReadFile();
                reader.readFromFile(file.getAbsolutePath());
    
                loadedGrid = reader.getGrid();
                renderGrid(loadedGrid.getGrid());
                solveButton.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
public void onSolve() {
    if (loadedGrid == null) return;

    String selectedAlgorithm = algorithmChoice.getValue();
    if (selectedAlgorithm == null) return;

    Solver solver = new Solver(loadedGrid);

    new Thread(() -> {
        long startTime = System.currentTimeMillis(); 

        boolean result = switch (selectedAlgorithm) {
            case "DFS" -> solver.solveWith("dfs", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
            case "UCS" -> solver.solveWith("ucs", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
            case "GBFS" -> solver.solveWith("gbfs", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
            case "A*" -> solver.solveWith("astar", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
            default -> false;
        };

        long endTime = System.currentTimeMillis(); 
        long duration = endTime - startTime;

        System.out.println("Solving time: " + duration + " ms");

        Platform.runLater(() -> {
            timeLabel.setText(duration + " ms");
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Informasi");
            alert.setHeaderText("Solusi ditemukan");
            alert.setContentText("Waktu penyelesaian: " + duration + " ms");
            alert.show();
        });

    }).start();
}
    

    private void renderGrid(char[][] data) {
        gridPane.getChildren().clear();
        gridPane.setHgap(2);
        gridPane.setVgap(2);

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                char ch = data[row][col];

                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setStroke(Color.BLACK);

                if (ch == '.' || ch == ' ') {
                    cell.setFill(Color.WHITE);
                } else if (Character.isLetter(ch)) {
                    int index = Character.toUpperCase(ch) - 'A';
                    if (index >= 0 && index < COLORS.length) {
                        cell.setFill(COLORS[index]);
                    } else {
                        cell.setFill(Color.GRAY);
                    }
                } else {
                    cell.setFill(Color.GRAY);
                }

                gridPane.add(cell, col, row);
            }
        }
    }
}
