package ru.issp.weight_control_system.ProdCons;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class TextThreeFileWriter {
    public static FileWriter writer1;
    public static FileWriter writer2;
    public static FileWriter writer3;

    static {
        try {
            String fileName1 = new SimpleDateFormat("yyyy_MM_dd'_1.txt'").format(new Date());
            String fileName2 = new SimpleDateFormat("yyyy_MM_dd'_2.txt'").format(new Date());
            String fileName3 = new SimpleDateFormat("yyyy_MM_dd'_3.txt'").format(new Date());
            Files.deleteIfExists(Path.of(fileName1));
            Files.deleteIfExists(Path.of(fileName2));
            Files.deleteIfExists(Path.of(fileName3));

            writer1 = new FileWriter(fileName1, StandardCharsets.UTF_8, true);
            writer2 = new FileWriter(fileName2, StandardCharsets.UTF_8, true);
            writer3 = new FileWriter(fileName3, StandardCharsets.UTF_8, true);


       /*     writer.write("time realMass modelMass modelMassFirstDerivative massDeviation deviationOfMassDeviation " +
                    "massFirstDerivativeDeviation deviationOfMassFirstDerivativeDeviation massSecondDerivativeDeviation " +
                    "deviationOfMassSecondDerivativeDeviation integralPartOfThePower proportionalPartOfThePower " +
                    "differentialPartOfThePower powerDeviation power V_upper length meltLevelHeight radiusFromModel" +
                    "radiusFromReal radius"+'\n');*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToTxtFile(String time,LinkedList<Double> linkedList) throws FileNotFoundException {
        try {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();

            sb1.append(time).append(' ').append(linkedList.get(0)).append(' ').append(linkedList.get(13)).append(' ').append(linkedList.get(3)).append('\n');
            sb2.append(time).append(' ').append(linkedList.get(15)).append(' ').append(linkedList.get(5)).append(' ').append(linkedList.get(6)).append('\n');
            sb3.append(time).append(' ').append(linkedList.get(19)).append(' ').append(linkedList.get(16)).append(' ').append(linkedList.get(13)).append('\n');

            writer1.append(sb1);
            writer2.append(sb2);
            writer3.append(sb3);
            writer1.flush();
            writer2.flush();
            writer3.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
