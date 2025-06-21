package com.datastruct;

public class Point {
  private int row;
  private int col;

  public Point(int row, int col) {
      this.row = row;
      this.col = col;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Point)) return false;
      Point other = (Point) o;
      return this.row == other.row && this.col == other.col;
  }

  public int getRow() {
      return row;
  }

  public int getCol() {
      return col;
  }

  @Override
  public int hashCode() {
      return 31 * row + col;
  }

  @Override
  public String toString() {
      return "(" + row + "," + col + ")";
  }
}
