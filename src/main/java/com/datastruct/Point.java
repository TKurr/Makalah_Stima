package com.datastruct;

public class Point {
  private final int row;
  private final int col;

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

  public static int manhattan(Point a, Point b) {
    return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
  }

  public static int manhattan(int r1, int c1, int r2, int c2) {
    return Math.abs(r1 - r2) + Math.abs(c1 - c2);
  }

  public int getRow() {
      return row;
  }

  public int getCol() {
      return col;
  }

  @Override
  public String toString() {
      return "(" + row + "," + col + ")";
  }
}
