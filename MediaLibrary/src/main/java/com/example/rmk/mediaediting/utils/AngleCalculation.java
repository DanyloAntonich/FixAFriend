package com.example.rmk.mediaediting.utils;

/**
 * Created by RMK on 10/23/2015.
 */
public class AngleCalculation {
    public Vector2D v1, v2;
    public float angleTwo;

    public  AngleCalculation(Vector2D v1, Vector2D v2){
        this.v1 = v1;
        this.v2 = v2;
        angleBetweenVectors(v1, v2);
    }


    public float angleBetweenVectors(Vector2D v1, Vector2D v2){
        float result = 0;

        float width = Math.abs(v1.x - v2.x);
        float height = Math.abs(v1.y - v2.y);

        result = (float) Math.atan((v1.x - v2.x) / (v1.y - v2.y));
        angleTwo = result;
        return result;
    }

    public Vector2D rotateVector(float alpha, Vector2D center, Vector2D vector2D) {
        float length = (float) Math.sqrt(Math.pow((center.x - vector2D.x), 2) + Math.pow((center.y - vector2D.y), 2));
        float deltaY = (float) (length * Math.sin(alpha));
        float deltaX = (float) (deltaY * Math.tan(alpha));

        Vector2D result = new Vector2D(deltaX + vector2D.x, deltaY + vector2D.y);

        return result;
    }
}
