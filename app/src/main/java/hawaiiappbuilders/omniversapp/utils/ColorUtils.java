package hawaiiappbuilders.omniversapp.utils;

import android.graphics.Color;

import java.util.Random;

public class ColorUtils {
    public static int randomColor() {

        return Color.argb(255, getRandomNumber(30, 200), getRandomNumber(30, 200), getRandomNumber(30, 200));
    }

    public static int getRandomNumber(int min, int max) {
        return (new Random()).nextInt((max - min) + 1) + min;
    }
}
