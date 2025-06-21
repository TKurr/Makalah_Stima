package com.datastruct;

public class Grid {
  private int rows, cols;
  private char[][] grid;

  public Grid(int rows, int cols, char[][] grid) {
      this.rows = rows;
      this.cols = cols;
      this.grid = grid;
  }

  public char[][] getGrid() {
      return grid;
  }

  public int getRows() {
      return rows;
  }

  public int getCols() {
      return cols;
  }

  public void setGrid(char[][] grid) {
      this.grid = grid;
  }

  public void setRows(int rows) {
      this.rows = rows;
  }

  public void setCols(int cols) {
      this.cols = cols;
  }

  public void print() {
      for (char[] row : grid) {
          System.out.println(new String(row));
      }
  }
}