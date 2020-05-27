package com.lwr.password.utils.generator;


//    char _0 = 48;
//    char _9 = 57;
//
//    char _A = 65;
//    char _Z = 90;
//
//    char _a = 97;
//    char _z = 122;
//    char _大括号 = 123;
//    char _约等号 = 126;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * 密码生成器
 */
public class PwdGenerator {

    //可读性
    boolean isVeryStrong = false;
    //10
    private static char[] number = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    //26
    private static char[] lowCase = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};//26
    //26
    private static char[] upCase = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};//26
    //32
    private static char[] symbols = new char[]{'~', '`', '!', '@', '#', '$', '(', ')', '*', '^', '&', '%', '?', '.', ',', '<', '>', '\'', '"', ';', ':', '/', '\\', '|', '[', ']', '{', '}', '=', '+', '_', '-'};
    //16
    private static char[] simple = new char[]{'*', '/', '+', '.', '~', '!', '@', '#', '$', '%', '^', '&', '*', '!', '\\', '='};

    /**
     * 生成密码
     *
     * @return
     */
    public static String generatePassword(int len, boolean isNumber, boolean isLow, boolean isUp, boolean isSymbols, char[] selfLetter) {
        ArrayList<char[]> arrayList = new ArrayList<char[]>();
        if (isNumber) {
            arrayList.add(number);
        }
        if (isLow) {
            arrayList.add(lowCase);
        }
        if (isUp) {
            arrayList.add(upCase);
        }
        if (isSymbols) {
            if (len > 16) {
                arrayList.add(symbols);
            } else {
                arrayList.add(simple);
            }
        }
        if (selfLetter != null && selfLetter.length > 0) {
            arrayList.add(selfLetter);
        }
        if (arrayList.size() < 1) {
            return "";
        }
        /**
         * 转换
         */
        char[][] cc = new char[arrayList.size()][];
        Iterator<char[]> iterator = arrayList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            cc[i++] = iterator.next();
        }
        return getPassword(len, cc);
    }

    private static String getPassword(int len, char[]... chars) {
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        int typeLength = chars.length;
        int type = random.nextInt(typeLength);
        ;
        for (int i = 0; i < len; i++) {
            //type取typeLength中不等于自己的一个整数
            int k = typeLength == 1 ? 1 : (random.nextInt(typeLength - 1) + 1);
            type = (k + type) % typeLength;
            char[] aTypeChars = chars[type];
            char a = aTypeChars[random.nextInt(aTypeChars.length)];
            sb.append(a);
        }
        random = null;
        return sb.toString();
    }

}
