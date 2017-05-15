package com.example.spiros.savetheteacher.Weather.Helpers;

/**
 * Created by Spiros on 17/4/2017.
 */

public class TemperatureFormatter {

    public static String format(float temperature) {
        return String.valueOf(Math.round(temperature)) + "°";
    }
}