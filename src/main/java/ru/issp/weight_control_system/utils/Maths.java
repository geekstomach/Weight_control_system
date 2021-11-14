package ru.issp.weight_control_system.utils;

import java.util.ArrayList;

public class Maths {
    //TODO добавить в расчеты скользящее среднее
    public static double SMA (ArrayList<Double> fullList,int buffSize){
       // double [] smaArray = new double[buffSize];
        double sum = 0;
        for (int i = fullList.size()-buffSize,j = 0; i < fullList.size(); i++,j++) {
         //  smaArray[j]=fullList.get(i);
            sum=sum+fullList.get(i);
        }
        return sum/buffSize;
    }
}
