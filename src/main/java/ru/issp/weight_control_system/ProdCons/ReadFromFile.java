package ru.issp.weight_control_system.ProdCons;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadFromFile implements Runnable {

    String file = "src/main/resources/Nika/data20210924022410.txt";//Сработает?

    private final DataInputStream reader;
    private final BlockingQueue<byte[]> queueFormFile;

    public ReadFromFile(BlockingQueue<byte[]> q) throws FileNotFoundException {
        queueFormFile = q;
        reader = new DataInputStream(new FileInputStream(file));
    }


    @Override
    public void run() {
        try {
            while (true) {
                queueFormFile.put(produce());
                Thread.sleep(100);
            }
        } catch (InterruptedException | IOException ex) {
            //обрабатываем исключение
            System.err.println("Исключение " + ex);
            Logger.getLogger(ReadFromCom.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    private byte[] produce() throws IOException, InterruptedException {
        byte[] rawData = new byte[12];

            byte temp = reader.readByte();

            if (temp == '<') {
                for (int i = 0; i < 12; i++) {
                    rawData[i] = reader.readByte();
                }
            }
        //System.out.println();
            if (reader.readByte() == '>') {
                //System.out.println("Данные успешно получены");
//TODO после каждого значений в файле есть возврат каретки и переход на новую строку
                reader.readByte();
                reader.readByte();
//TODO в случае когда мы пропускаем байт Put не должен срабатывать
            } else {
                System.out.println("Пропускаем байт " + temp);
            }

        return rawData;

    }
}