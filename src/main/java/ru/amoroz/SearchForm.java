package ru.amoroz;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SearchForm extends JFrame {

    public SearchForm() {
        super("Search form");
        setMinimumSize(new Dimension(640, 480));
        setBounds(0, 0, 800, 600);

        JPanel northJP = new JPanel(new GridLayout(1, 3));
        JButton choosePathButton = new JButton("Choose dir");
        JButton startSearchButton = new JButton("Start");
        JButton saveButton = new JButton("Save log");
        northJP.add(choosePathButton);
        northJP.add(startSearchButton);
        northJP.add(saveButton);
        add(northJP, BorderLayout.NORTH);

        JPanel southJP = new JPanel(new GridLayout(2, 1));
        JPanel southEastJP = new JPanel(new GridLayout(1, 2));
        JTextField jtfPath = new JTextField("");
        jtfPath.setEditable(false);
        JTextField jtfRegex = new JTextField("<(from|to) uri=\".*?\".*?>");
        southEastJP.add(jtfPath);
        southEastJP.add(jtfRegex);
        southJP.add(southEastJP);
        JProgressBar jpb = new JProgressBar(0, 100);
        southJP.add(jpb);
        add(southJP, BorderLayout.SOUTH);

        JTextArea jta = new JTextArea();
        jta.setEditable(false);
        JScrollPane jsp = new JScrollPane(jta);
        add(jsp, BorderLayout.CENTER);

        choosePathButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int tmp = fileChooser.showDialog(null, "Choose directory");
            if (tmp == JFileChooser.APPROVE_OPTION) {
                jtfPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
            } else {
                jta.append(new Date() + " WARNING: Bad directory. Try again\n");
            }
        });

        startSearchButton.addActionListener(e -> {
            try {
                Pattern.compile(jtfRegex.getText()).matcher("");
                if (jtfPath.getText().isEmpty()) {
                    jta.append(new Date() + " WARNING: Choose directory\n");
                } else {
                    new Thread(() -> {
                        choosePathButton.setEnabled(false);
                        startSearchButton.setEnabled(false);
                        saveButton.setEnabled(false);
                        jtfRegex.setEditable(false);
                        jta.setText("Searching routes...");
                        jpb.setValue(0);
                        //search for files recursive
                        File f = new File(jtfPath.getText());
                        java.util.List<File> allFiles = Reader.getFiles(f);
                        jta.setText("FOUND: " + allFiles.size() + " files\n");
                        jta.append("parsing...");

                        List<String> routeList = new ArrayList<>();

                        //read files
                        for (int i = 0; i < allFiles.size(); i++) {
                            String s = Reader.readFile(allFiles.get(i));
                            if (s.contains("<from uri")) {
                                //search routes
                                jpb.setValue(100 * i / allFiles.size() + 1);
                                routeList.addAll(Reader.searchRoutes(s, jtfRegex.getText()));
                            }
                        }

                        //print routes
                        StringBuffer sb = new StringBuffer();
                        routeList.stream().distinct().sorted().forEach(s -> sb.append(s + "\n"));
                        jta.setText(sb.toString());
                        choosePathButton.setEnabled(true);
                        startSearchButton.setEnabled(true);
                        saveButton.setEnabled(true);
                        jtfRegex.setEditable(true);
                    }).start();
                }
            } catch (PatternSyntaxException ex) {
                jta.append(new Date() + " WARNING: bad regex: " + ex.getMessage() + "\n");
            }
        });

        saveButton.addActionListener(e -> {
            if (jta.getText().isEmpty() || jtfPath.getText().isEmpty()) {
                jta.append(new Date() + " WARN: Save directory unknown or nothing to save\n");
            } else {
                Saver.saveFile(new File(jtfPath.getText()), jta.getText());
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}