package com.lwr.password;

import org.junit.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String s = "*/+.~!@#$%^&*";
        char[] symbols = new char[13];//{'~','`','!','@','#','$','(',')','*','^','&','%','?','.',',','<','>','\'','"',';',':','/','\\','|','[',']','{','}','=','+','_','-'};
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
        char[] always = new char[]{'*', '/', '+', '.', '~', '!', '@', '#', '$', '%', '^', '&', '*', '!', '\\', '='};
//        s.getChars(0,s.length(),symbols,0);
//        s = "";
//        for (int i = 0; i < symbols.length; i++) {
//            s+="'"+symbols[i]+"'"+",";
//        }
//            System.out.println(always.length);
//        nd(always,symbols);
//        int typeLength = 5;
//        int type = 0;
//        while (true) {
//
//        System.out.println((type++ % typeLength));

        SecureRandom random = new SecureRandom();
        int typeLength = 5;
        int type = random.nextInt(typeLength);
        int flag;
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        int e = 0;
        for (int i = 0; i < 500000; i++) {
            flag = type;
            //type取typeLength中不等于自己的一个整数
            int k = random.nextInt(typeLength - 1) + 1;
            type = (k + type) % typeLength;
            if (type == 0) {
                a++;
            }
            if (type == 1) {
                b++;
            }
            if (type == 2) {
                c++;
            }
            if (type == 3) {
                d++;
            }
            if (type == 4) {
                e++;
            }
            if (type == flag) {
                System.out.println(type);
                System.out.println("问题很大" + type + flag);
                break;
            }
        }
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);



    }

    void nd(char[]... s) {
        for (char[] sd : s
        ) {
            System.out.println(sd.length);
        }
        System.out.println(s.getClass().getName());
    }
}