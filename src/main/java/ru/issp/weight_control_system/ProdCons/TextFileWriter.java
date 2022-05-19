package ru.issp.weight_control_system.ProdCons;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class TextFileWriter {
public static FileWriter writer;

    static {
        try {
            String fileName = new SimpleDateFormat("yyyy_MM_dd'.txt'").format(new Date());
            Files.deleteIfExists(Path.of(fileName));

            writer = new FileWriter(fileName, StandardCharsets.UTF_8, true);
            writer.write("time realMass modelMass modelMassFirstDerivative massDeviation deviationOfMassDeviation " +
                    "massFirstDerivativeDeviation deviationOfMassFirstDerivativeDeviation massSecondDerivativeDeviation " +
                    "deviationOfMassSecondDerivativeDeviation integralPartOfThePower proportionalPartOfThePower " +
                    "differentialPartOfThePower powerDeviation power V_upper length meltLevelHeight radiusFromModel" +
                    "radiusFromReal radius"+'\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToTxtFile(String time,LinkedList<Double> linkedList) throws FileNotFoundException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(time);
            for (Double aDouble : linkedList) {
                sb.append(' ');
                sb.append(aDouble);
            }
            sb.append('\n');
            writer.append(sb);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
