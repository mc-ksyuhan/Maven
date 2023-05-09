package app;

import java.util.Objects;

public class Line {
    Point pointA;
    Point pointB;

    public Line(Point pointA, Point pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
        System.out.println(pointA.pos.x + " " + pointA.pos.y);
        System.out.println(pointB.pos.x + " " + pointB.pos.y);
    }

    public double getDistance(Point pointC){
        double xA = this.pointA.pos.x;
        double xB = this.pointB.pos.x;
        double yA = this.pointA.pos.y;
        double yB = this.pointB.pos.y;
        double xC = pointC.pos.x;
        double yC = pointC.pos.y;
        double a = yB-yA;
        if (a==0) a=0.0000000000001;
        double b = xA-xB;
        if (b==0) b=0.0000000000001;
        double c = yA*xB-yB*xA;
        double d = Math.abs(a*xC+b*yC+c)/Math.sqrt(a*a+b*b);
        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(pointA, line.pointA) && Objects.equals(pointB, line.pointB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointA, pointB);
    }
}