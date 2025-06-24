package com.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

import com.datastruct.Grid;
import com.datastruct.Point;

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

    private boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < grid.getRows() && c < grid.getCols();
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

    private List<int[]> prioritizeDirs(int r, int c, Point end) {
        List<int[]> dirs = new ArrayList<>(Arrays.asList(DIRS));
        dirs.sort(Comparator.comparingInt(d ->
                Point.manhattan(r + d[0], c + d[1], end.getRow(), end.getCol())));
        return dirs;
    }

    // private void findAllPaths(int r, int c, Point end, char ch, boolean[][] visited,
    //                           List<Point> path, List<List<Point>> allPaths) {
    //     if (r == end.getRow() && c == end.getCol()) {
    //         allPaths.add(new ArrayList<>(path));
    //         return;
    //     }

    //     visited[r][c] = true;

    //     List<int[]> dirs = prioritizeDirs(r, c, end);

    //     for (int[] d : dirs) {
    //         int nr = r + d[0], nc = c + d[1];

    //         if (inBounds(nr, nc) && !visited[nr][nc] &&
    //                 (grid.getGrid()[nr][nc] == '.' || (nr == end.getRow() && nc == end.getCol()))) {
    //             path.add(new Point(nr, nc));
    //             findAllPaths(nr, nc, end, ch, visited, path, allPaths);
    //             path.remove(path.size() - 1);
    //         }
    //     }

    //     visited[r][c] = false;
    // }

    private String pathToString(List<Point> path) {
        StringBuilder sb = new StringBuilder();
        for (Point p : path) {
            sb.append(p.getRow())
            .append(',')
            .append(p.getCol())
            .append(';');
        }
        return sb.toString();
    }

    private List<Point> findDFSPath(Point start, Point end, Set<String> tried) {
        Stack<List<Point>> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
    
        stack.push(List.of(start));
    
        while (!stack.isEmpty()) {
            List<Point> path = stack.pop();
            Point curr = path.get(path.size() - 1);
    
            if (curr.equals(end)) {
                String sig = pathToString(path);
                if (!tried.contains(sig)) return path;
                else continue;
            }
    
            for (int[] d : DIRS) {
                int nr = curr.getRow() + d[0], nc = curr.getCol() + d[1];
                Point next = new Point(nr, nc);
    
                if (inBounds(nr, nc) &&
                   (grid.getGrid()[nr][nc] == '.' || next.equals(end)) &&
                   !path.contains(next)) {
    
                    List<Point> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    String sig = pathToString(newPath);
    
                    if (!visited.contains(sig)) {
                        stack.push(newPath);
                        visited.add(sig);
                    }
                }
            }
        }
    
        return null;
    }

    private List<Point> findUCSPath(Point start, Point end, Set<String> tried) {
        PriorityQueue<List<Point>> pq = new PriorityQueue<>(Comparator.comparingInt(List::size));
        Set<String> visited = new HashSet<>();
    
        pq.add(List.of(start));
    
        while (!pq.isEmpty()) {
            List<Point> path = pq.poll();
            Point curr = path.get(path.size() - 1);
    
            if (curr.equals(end)) {
                String sig = pathToString(path);
                if (!tried.contains(sig)) return path;
                else continue;
            }
    
            for (int[] d : DIRS) { 
                int nr = curr.getRow() + d[0], nc = curr.getCol() + d[1];
                Point next = new Point(nr, nc);
                if (inBounds(nr, nc) &&
                   (grid.getGrid()[nr][nc] == '.' || next.equals(end)) &&
                   !path.contains(next)) {
                    List<Point> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    String sig = pathToString(newPath);
                    if (!visited.contains(sig)) {
                        pq.add(newPath);
                        visited.add(sig);
                    }
                }
            }
        }
    
        return null;
    }

    private List<Point> findGreedyPath(Point start, Point end, Set<String> tried) {
        PriorityQueue<List<Point>> pq = new PriorityQueue<>(Comparator.comparingInt(
            path -> Point.manhattan(path.get(path.size() - 1), end)
        ));
        Set<String> visited = new HashSet<>();
        
        pq.add(List.of(start));
        
        while (!pq.isEmpty()) {
            List<Point> path = pq.poll();
            Point curr = path.get(path.size() - 1);
            
            if (curr.equals(end)) {
                String sig = pathToString(path);
                if (!tried.contains(sig)) return path;
                else continue;
            }
            
            for (int[] d : prioritizeDirs(curr.getRow(), curr.getCol(), end)) {
                int nr = curr.getRow() + d[0], nc = curr.getCol() + d[1];
                Point next = new Point(nr, nc);
                if (inBounds(nr, nc) &&
                   (grid.getGrid()[nr][nc] == '.' || next.equals(end)) &&
                   !path.contains(next)) {
                    List<Point> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    String sig = pathToString(newPath);
                    if (!visited.contains(sig)) {
                        pq.add(newPath);
                        visited.add(sig);
                    }
                }
            }
        }
        
        return null;
    }

    private List<Point> findAStarPath(Point start, Point end, Set<String> tried) {
        PriorityQueue<List<Point>> pq = new PriorityQueue<>(Comparator.comparingInt(
            path -> path.size() + Point.manhattan(path.get(path.size() - 1), end)
        ));
        Set<String> visited = new HashSet<>();
    
        pq.add(List.of(start));
    
        while (!pq.isEmpty()) {
            List<Point> path = pq.poll();
            Point curr = path.get(path.size() - 1);
    
            if (curr.equals(end)) {
                String sig = pathToString(path);
                if (!tried.contains(sig)) return path;
                else continue;
            }
    
            for (int[] d : DIRS) {
                int nr = curr.getRow() + d[0], nc = curr.getCol() + d[1];
                Point next = new Point(nr, nc);
    
                if (inBounds(nr, nc) &&
                    (grid.getGrid()[nr][nc] == '.' || next.equals(end)) &&
                    !path.contains(next)) {
                    
                    List<Point> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    String sig = pathToString(newPath);
    
                    if (!visited.contains(sig)) {
                        pq.add(newPath);
                        visited.add(sig);
                    }
                }
            }
        }
    
        return null;
    }

    public boolean solveWith(String method, 
                             Consumer<char[][]> onStep) {
        List<Map.Entry<Character, List<Point>>> sortedPairs = 
                    new ArrayList<>(endpoints.entrySet());
        List<Character> keys = new ArrayList<>();
        for (Map.Entry<Character, List<Point>> entry : sortedPairs) {
            keys.add(entry.getKey());
        }
    
        return backtrack(0, keys, onStep, method);
    }
    
    private boolean backtrack(int idx, 
                              List<Character> keys, 
                              Consumer<char[][]> onStep, 
                              String method) {
        if (idx == keys.size()) return true;
    
        char ch = keys.get(idx);
        List<Point> points = endpoints.get(ch);
        Point start = points.get(0);
        Point end = points.get(1);
    
        Set<String> triedPaths = new HashSet<>();
        char[][] originalGrid = copyGrid();
    
        while (true) {
            List<Point> path = switch (method.toLowerCase()) {
                case "dfs" -> findDFSPath(start, end, triedPaths);
                case "ucs" -> findUCSPath(start, end, triedPaths);
                case "gbfs" -> findGreedyPath(start, end, triedPaths);
                case "astar" -> findAStarPath(start, end, triedPaths);
                default -> null;
            };
    
            if (path == null) break;
    
            triedPaths.add(pathToString(path));
            for (Point p : path) {
                grid.getGrid()[p.getRow()][p.getCol()] = ch;
                onStep.accept(copyGrid());
                sleep(10);
            }
    
            if (backtrack(idx + 1, keys, onStep, method)) return true;
    
            grid.setGrid(copyGrid(originalGrid));
            onStep.accept(copyGrid());
            sleep(10);
        }
    
        return false;
    }
}