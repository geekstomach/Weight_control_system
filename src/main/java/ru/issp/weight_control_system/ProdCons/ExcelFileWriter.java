package ru.issp.weight_control_system.ProdCons;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
//Немного примеров
//https://poi.apache.org/components/spreadsheet/quick-guide.html#NewWorkbook

public class ExcelFileWriter{
    static Workbook workbook = new XSSFWorkbook();
    //static CreationHelper createHelper = workbook.getCreationHelper();
    static Sheet sheet = workbook.createSheet("Test 1");
       static AtomicInteger count = new AtomicInteger(1);
       static{
           Row dataRow = sheet.createRow(0);
           dataRow.createCell(0).setCellValue("time");
           dataRow.createCell(1).setCellValue("realMass");
           dataRow.createCell(2).setCellValue("modelMass");
           dataRow.createCell(3).setCellValue("modelMassFirstDerivative");
           dataRow.createCell(4).setCellValue("massDeviation");
           dataRow.createCell(5).setCellValue("Deviation of massDeviation");
           dataRow.createCell(6).setCellValue("massFirstDerivativeDeviation");
           dataRow.createCell(7).setCellValue("Deviation of massFirstDerivativeDeviation");
           dataRow.createCell(8).setCellValue("massSecondDerivativeDeviation");
           dataRow.createCell(9).setCellValue("Deviation of massSecondDerivativeDeviation");
           dataRow.createCell(10).setCellValue("integralPartOfThePower");
           dataRow.createCell(11).setCellValue("proportionalPartOfThePower");
           dataRow.createCell(12).setCellValue("differentialPartOfThePower");
           dataRow.createCell(13).setCellValue("powerDeviation");
           dataRow.createCell(14).setCellValue("Power");
           dataRow.createCell(15).setCellValue("V_upper");
           dataRow.createCell(16).setCellValue("length");
           dataRow.createCell(17).setCellValue("meltLevelHeight");
           dataRow.createCell(18).setCellValue("Radius from Model");
           dataRow.createCell(19).setCellValue("Radius from Real");
           dataRow.createCell(20).setCellValue("Radius");

       }


    public static void writeToExcelFile(String time,LinkedList<Double> linkedList) throws FileNotFoundException {


    Row dataRow = sheet.createRow(count.getAndIncrement());

    dataRow.createCell(0).setCellValue(time);

        for (int i = 0; i < linkedList.size(); i++) {

            dataRow.createCell(i+1).setCellValue(linkedList.get(i));
            }
        try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}



