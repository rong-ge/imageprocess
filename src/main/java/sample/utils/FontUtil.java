package sample.utils;


import sun.font.FontDesignMetrics;

import java.awt.*;

public class FontUtil {

    //得到默认字体
    public static Font getDefaultFont() {
        return new Font(null);
    }

    private static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    //得到字符串长度
    public static int getStringLength(Font font, String str) {
        if (isEmpty(str)) {
            return 0;
        }
        if (null == font) {
            font = getDefaultFont();
        }
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
//        char[] strcha = str.toCharArray();
//        int strWidth = metrics.charsWidth(strcha, 0, str.length());
        return metrics.stringWidth(str);
    }

    //得到应该换行前的最大字符串
    public static String strSplitMaxLenthStr(Font font, String str, int maxLength) {
        if (isEmpty(str) || maxLength < 1) {
            return str;
        }
        if (null == font) {
            font = getDefaultFont();
        }
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        String max_Str = str;

        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            width += metrics.charWidth(str.charAt(i));
            if (width > maxLength) {
                //上一个长度
                max_Str = str.substring(0, i);
                break;
            }
        }
        return max_Str;
    }

    //得到应该换行前的最大字符串
    public static int getSplitMaxLenth(Font font, String str, int maxLength) {
        if (isEmpty(str) || maxLength < 1) {
            return 0;
        }
        if (null == font) {
            font = getDefaultFont();
        }
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        String max_Str = str;

        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            width += metrics.charWidth(str.charAt(i));
            if (width > maxLength) {
                //上一个长度
                return i;
            }
        }
        return 0;
    }


    //是否是最长的字符串，如果是true，就不需要再次进行截断
    public static boolean isMaxStr(Font font, String str, int maxLength) {
        if (isEmpty(str) || maxLength < 1) {
            return true;
        }
        if (null == font) {
            font = getDefaultFont();
        }
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        int width = metrics.stringWidth(str);
//        int width = 0;
//        for (int i = 0; i < str.length(); i++) {
//            width += metrics.charWidth(str.charAt(i));
//            if (width > maxLength) {
//                return false;
//            }
//        }
        return width <= maxLength;

    }

    public static int getRightSize(String text, float canvasWidth) {
        Font font = new Font("宋体", Font.BOLD, 40);
        int stringLength = getStringLength(font, text);
        //根据最大值，计算出当前文本占用的宽度
        //如果文本占用的宽度比画布宽度小，说明不用伸缩，直接返回当前字号
        if (stringLength < canvasWidth) {
            return 40;
        }
        //已知当前文本字号、文本占用宽度、画布宽度，计算出合适的字号，并返回
        return (int) (40 * canvasWidth / stringLength);
    }

}