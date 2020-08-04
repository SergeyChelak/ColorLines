package org.chelak.colorlines.board;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

/**
 * Created by Sergey on 23.02.2016.
 */
public class ColorFilter {

    private float red;
    private float green;
    private float blue;
    private float alpha;

    public ColorFilter(float red, float green, float blue, float alpha) {
        this.alpha = alpha;
        this.blue = blue;
        this.green = green;
        this.red = red;
    }

    public ColorFilter(ColorFilter filter) {
        this.alpha = filter.alpha;
        this.blue = filter.blue;
        this.green = filter.green;
        this.red = filter.red;
    }

    public ColorFilter(float red, float green, float blue) {
        this(red, green, blue, 1.0f);
    }

    public ColorMatrixColorFilter createFilter() {
        float[] matrix = new float[]{
                red, 0, 0, 0, 0,
                0, green, 0, 0, 0,
                0, 0, blue, 0, 0,
                0, 0, 0, alpha, 0};
        ColorMatrix colorMatrix = new ColorMatrix(matrix);
        return new ColorMatrixColorFilter(colorMatrix);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }
}
