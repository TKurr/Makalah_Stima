package com.logic;

import com.datastruct.*;

import java.util.*;
import java.util.function.Consumer;

public class Solver {

    private final Grid grid;
    private final Map<Character, List<Point>> endpoints;    

    private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public Solver(Grid grid) {
        this.grid = grid;
        this.endpoints = findEndpoints(grid);
    }

    private Map<Character, List<Point>> findEndpoints(Grid grid) {
        Map<Character, List<Point>> map = new HashMap<>();
        char[][] data = grid.getGrid();

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                char ch = data[row][col];
                if (Character.isLetter(ch)) {
                    map.computeIfAbsent(ch, k -> new ArrayList<>()).add(new Point(row, col));
                }
            }
        }

        return map;
    }

    private int manhattan(Point a, Point b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private void findAllPaths(int r, int c, Point end, char ch, boolean[][] visited,
                              List<Point> path, List<List<Point>> allPaths) {
        if (r == end.getRow() && c == end.getCol()) {
            allPaths.add(new ArrayList<>(path));
            return;
        }

        visited[r][c] = true;

        List<int[]> dirs = prioritizeDirs(r, c, end);

        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];

            if (inBounds(nr, nc) && !visited[nr][nc] &&
                    (grid.getGrid()[nr][nc] == '.' || (nr == end.getRow() && nc == end.getCol()))) {
                path.add(new Point(nr, nc));
                findAllPaths(nr, nc, end, ch, visited, path, allPaths);
                path.remove(path.size() - 1);
            }
        }

        visited[r][c] = false;
    }

    private String pathToString(List<Point> path) {
        StringBuilder sb = new StringBuilder();
        for (Point p : path) {
            sb.append(p.getRow()).append(',').append(p.getCol()).append(';');
        }
        return sb.toString();
    }

    public boolean solve(Consumer<char[][]> onStep) {
        List<Map.Entry<Character, List<Point>>> sortedPairs = new ArrayList<>(endpoints.entrySet());
        sortedPairs.sort(Comparator.comparingInt(e ->
                manhattan(e.getValue().get(0), e.getValue().get(1))));

        List<Character> keys = new ArrayList<>();
        for (Map.Entry<Character, List<Point>> entry : sortedPairs) {
            keys.add(entry.getKey());
        }
        return backtrack(0, keys, onStep);
    }

    private boolean backtrack(int idx, List<Character> keys, Consumer<char[][]> onStep) {
        if (idx == keys.size()) return true;
    
        char ch = keys.get(idx);
        List<Point> points = endpoints.get(ch);
        Point start = points.get(0);
        Point end = points.get(1);
    
        Set<String> triedPaths = new HashSet<>();
        char[][] originalGrid = copyGrid();
    
        while (true) {
            boolean[][] visited = new boolean[grid.getRows()][grid.getCols()];
            List<List<Point>> allPaths = new ArrayList<>();
            findAllPaths(start.getRow(), start.getCol(), end, ch, visited,
                         new ArrayList<>(List.of(start)), allPaths);
    
            allPaths.removeIf(path -> triedPaths.contains(pathToString(path)));
    
            allPaths.sort(Comparator.comparingInt(List::size));
    
            if (allPaths.isEmpty()) break;
    
            List<Point> path = allPaths.get(0);
            triedPaths.add(pathToString(path));
    
            for (Point p : path) {
                grid.getGrid()[p.getRow()][p.getCol()] = ch;
                onStep.accept(copyGrid());
                sleep(30);
            }
    
            if (backtrack(idx + 1, keys, onStep)) return true;
    
            grid.setGrid(copyGrid(originalGrid));
            onStep.accept(copyGrid());
            sleep(30);
        }
    
        return false;
    }

    private List<int[]> prioritizeDirs(int r, int c, Point end) {
        List<int[]> dirs = new ArrayList<>(Arrays.asList(DIRS));
        dirs.sort(Comparator.comparingInt(d ->
                manhattan(r + d[0], c + d[1], end.getRow(), end.getCol())));
        return dirs;
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < grid.getRows() && c < grid.getCols();
    }

    private int manhattan(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private char[][] copyGrid() {
        return copyGrid(grid.getGrid());
    }

    private char[][] copyGrid(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }
}