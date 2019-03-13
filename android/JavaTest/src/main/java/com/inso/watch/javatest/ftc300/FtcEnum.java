package com.inso.watch.javatest.ftc300;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/27
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class FtcEnum {
    static abstract class Weekday {
        private Weekday() {
        }

        ;
        public static final Weekday MON = new Weekday() {
            @Override
            public Weekday nextDay() {
                return SUN;
            }
        };
        public static final Weekday SUN = new Weekday() {
            @Override
            public Weekday nextDay() {
                return MON;
            }
        };
//        public Weekday nextDay(){
//            if(this==MON){
//                return SUN;
//            }else{
//                return MON;
//            }
//        }

        public abstract Weekday nextDay(); //

        @Override
        public String toString() {
            return this == MON ? "MON" : "SUN";
        }
    }

    public static void main(String[] args) {
        System.out.printf(Weekday.MON.nextDay().toString());
    }
}
