package com.control;

import com.datastruct.*;
import com.logic.*;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;     

import java.io.File;

public class MainController {
    @FXML
    private GridPane gridPane;
    @FXML
    private Button solveButton;
    @FXML
    private ComboBox<String> algorithmChoice;

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
            switch (selectedAlgorithm) {
                case "DFS" -> solver.solveWith("dfs", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
                case "UCS" -> solver.solveWith("ucs", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
                case "GBFS" -> solver.solveWith("gbfs", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
                case "A*" -> solver.solveWith("astar", updatedGrid -> Platform.runLater(() -> renderGrid(updatedGrid)));
            }
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
