package com.datastruct;

public class PathEndpoints {
    public Point start;
    public Point end;
  
    public PathEndpoints(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Start: " + start + ", End: " + end;
    }
    }
