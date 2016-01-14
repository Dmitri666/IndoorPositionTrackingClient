package com.lps.lpsapp.positions;

/**
 * Created by dle on 30.10.2015.
 */
public class Vector2D {
    public Double x;
    public Double y;

    public Vector2D(double x,double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getLength()
    {
        return Math.sqrt(Math.pow(this.x,2.0) + Math.pow(this.y,2.0));
    }

    public Vector2D withLength(double length) {
        double oldLength = this.getLength();
        return new Vector2D(this.x * length / oldLength, this.y * length/ oldLength);
    }

    public Vector2D plus(Vector2D other)
    {
        return new Vector2D(this.x + other.x,this.y + other.y);
    }

    public Vector2D minus(Vector2D other)
    {
        return new Vector2D(this.x - other.x,this.y - other.y);
    }

    public Vector2D ortogonal() {
        return new Vector2D(- this.y, this.x);
    }
}
