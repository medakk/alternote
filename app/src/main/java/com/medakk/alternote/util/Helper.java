package com.medakk.alternote.util;

/**
 * Helper
 *  -contains static utilities to use in other parts
 *  of the code
 */
public class Helper {

    /*
     *  takes a String and splits the first line
     *  and the rest of the String. Returns an array
     *  contain 2 Strings
     *  The second string may be an empty String if there is only
     *  one line in src
     */

    public static String[] splitTitleAndContent(String src) {
        if(src == null) {
            throw new NullPointerException("Helper: splitTitleAndContent(src) - src cannot be null");
        }

        final int srcLength = src.length();
        String[] out = new String[2];

        int firstNewLinePosition = src.indexOf('\n');

        //the given String contains only one line
        if(firstNewLinePosition == -1) {
            out[0] = src;
            out[1] = "";
        } else {
            //extract the title
            out[0] = src.substring(0, firstNewLinePosition);
            //extract the content
            out[1] = src.substring(firstNewLinePosition + 1, srcLength);
        }

        return out;
    }
}
