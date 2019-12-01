package com.leyou.common.enums;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class ddd {



    public static void main(){
        student s=new student();




    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class student{
        private String name;
        private int age;
    }
}


