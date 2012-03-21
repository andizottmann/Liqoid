/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.quadrillenschule.liquidroid.tools;

/**
 *
 * @author andi
 */
public class Utils {

    public static String lessThanDays(long deltainms) {
        String retval = ">9w";
        long mins = 1000 * 60;
        long hour = mins * 60;
        long day = hour * 24;
        long week=day*7;

        for (int i = 1; i < 9; i++) {
            if (deltainms < i * mins) {
                return "<" + i + "m";
            }
        }
        for (int i = 1; i < 9; i++) {
            if (deltainms < i * hour) {
                return "<" + i + "h";
            }
        }
        for (int i = 1; i < 9; i++) {
            if (deltainms < i * day) {
                return "<" + i + "d";
            }
        }
           for (int i = 1; i < 9; i++) {
            if (deltainms < i * week) {
                return "<" + i + "w";
            }
        }
        return retval;
    }
}
