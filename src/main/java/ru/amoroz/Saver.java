package ru.amoroz;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Saver {
    public static void saveFile(File directory, String content) {
        String filename = "report_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()).toString() + ".txt";

        File file = new File(directory.getAbsolutePath() + File.separator + filename);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "File been saved to: " + file.getAbsolutePath());
    }
}