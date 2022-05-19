package ru.issp.weight_control_system.utils;

import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Filters {
    //TODO добавить в расчеты скользящее среднее
    public static double SMA (ObservableList<Double> fullList, int buffSize){
       // double [] smaArray = new double[buffSize];
        if (fullList.size()<buffSize)return 0d;
        double sum = 0;
        for (int i = fullList.size()-buffSize,j = 0; i < fullList.size(); i++,j++) {
         //  smaArray[j]=fullList.get(i);
            sum=sum+fullList.get(i);
        }
        return sum/buffSize;
    }
    //https://engineering.stackexchange.com/questions/13516/pt1-filter-without-derivative
    //инерционный апериодический фильтр
    public static double lowPassFilter(ObservableList<Double> realList,ObservableList<Double> filteredList, double period, int tick){
        if ((realList.size()>=2)&&(filteredList.size()>=2)){
            double xCurrent = realList.get(realList.size()-1)-filteredList.get(filteredList.size()-1);
           // System.out.println("xCurrent = "+xCurrent+" = "+ realList.get(realList.size()-1)+" - "+ filteredList.get(filteredList.size()-1));
            double xPrevious = realList.get(realList.size()-2)-filteredList.get(filteredList.size()-2);
           // System.out.println("xPrevious = "+xPrevious+" = "+ realList.get(realList.size()-2)+" - "+ filteredList.get(filteredList.size()-2));

            return filteredList.get(filteredList.size()-1)+((double) tick /(2*period))*(xCurrent+xPrevious);}

        return 0d;}
    }

