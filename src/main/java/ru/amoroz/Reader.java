package ru.amoroz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {
    public static void startSearching(File root) {
        //search for files recursive
        System.out.println("Searching files...");
        List<File> allFiles = getFiles(root);
        System.out.println("TOTAL FILES COUNT at " + root.getAbsolutePath() + " = " + allFiles.size());

        List<String> routeList = new ArrayList<>();

        //read files
        for (int i = 0; i < allFiles.size(); i++) {
            String s = readFile(allFiles.get(i));
            if (s.contains("<from uri")) {
                //search routes
                System.out.println("Searching routes... " + (100 * i / allFiles.size()) + "%");
                routeList.addAll(searchRoutes(s));
            }
        }

        //print routes
        StringBuffer sb = new StringBuffer();
        routeList.stream().distinct().sorted().forEach(s -> sb.append(s + "\n"));
        System.out.println(sb);
        Saver.saveFile(root, sb.toString());
    }

    public static List<File> getFiles(File rootDir) {
        if (!rootDir.isDirectory()) return null;

        List<File> files = new ArrayList<>(Arrays.asList(rootDir.listFiles(File::isFile)));

        File[] directories = rootDir.listFiles(File::isDirectory);
        for (File directory : directories) {
            files.addAll(getFiles(directory));
        }
        return files;
    }

    public static String readFile(File file) {
        if (!file.isFile()) return null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> searchRoutes(String text) {
        //String reg = "\\<(from|to) uri=\".*?\".?\\/\\>";
        return searchRoutes(text, "<(from|to) uri=\".*?\".*?>");
    }

    public static List<String> searchRoutes(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(0));
        }

        return list;
    }
}