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

    public static void main(String[] args) {
        ArrayList<Double> rawWeight = new ArrayList<>();
        rawWeight.add(0d);rawWeight.add(1d);rawWeight.add(2d);rawWeight.add(3d);rawWeight.add(4d);rawWeight.add(5d);rawWeight.add(6d);
        rawWeight.add(7d);
        System.out.println(SMA(rawWeight,8));
    }
}
