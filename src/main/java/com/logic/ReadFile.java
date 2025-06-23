package com.logic;

import com.datastruct.Grid;
import com.datastruct.Point;
import com.datastruct.PathEndpoints;

import java.io.*;
import java.util.*;

public class ReadFile {
    private Grid grid;
    private final Map<Character, PathEndpoints> endpoints = new HashMap<>();

    public void readFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String[] dims = reader.readLine().trim().split("\\s+");
        int rows = Integer.parseInt(dims[0]);
        int cols = Integer.parseInt(dims[1]);

        char[][] gridData = new char[rows][cols];
        Map<Character, List<Point>> tempMap = new HashMap<>();

        for (int r = 0; r < rows; r++) {
            String line = reader.readLine().trim();
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                gridData[r][c] = ch;
                if (Character.isLetter(ch)) {
                    tempMap.putIfAbsent(ch, new ArrayList<>());
                    tempMap.get(ch).add(new Point(r, c));
                }
            }
        }
        reader.close();

        for (Map.Entry<Character, List<Point>> entry : tempMap.entrySet()) {
            List<Point> pts = entry.getValue();
            if (pts.size() == 2) {
                endpoints.put(entry.getKey(), new PathEndpoints(pts.get(0), pts.get(1)));
            } else {
                throw new IllegalArgumentException("Color " + entry.getKey() + " must have exactly 2 endpoints.");
            }
        }

        this.grid = new Grid(rows, cols, gridData);
    }

    public Grid getGrid() {
        return grid;
    }

    public Map<Character, PathEndpoints> getEndpoints() {
        return endpoints;
    }
}
