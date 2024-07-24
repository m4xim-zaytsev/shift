package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static boolean appendMode = false;
    private static boolean shortStats = false;
    private static boolean fullStats = false;
    private static String outputPath = "";
    private static String stringName = "strings.txt";
    private static String integerName = "integers.txt";
    private static String floatName = "floats.txt";
    private static String prefix = "";

    private static List<Integer> integers = new ArrayList<>();
    private static List<Double> floats = new ArrayList<>();
    private static List<String> strings = new ArrayList<>();

    public static void main(String[] args) {
        List<String> inputFiles = new ArrayList<>();

        parseArguments(args, inputFiles);

        for (String inputFile : inputFiles) {
            processFile(inputFile);
        }

        writeResults();
        printStatistics();
    }

    private static void parseArguments(String[] args, List<String> inputFiles) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                    appendMode = true;
                    break;
                case "-s":
                    shortStats = true;
                    fullStats = false;
                    break;
                case "-f":
                    fullStats = true;
                    shortStats = false;
                    break;
                case "-o":
                    if (i + 1 < args.length) {
                        outputPath = args[++i];
                    } else {
                        System.err.println("Missing value for -o option");
                        System.exit(1);
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        prefix = args[++i];
                    } else {
                        System.err.println("Missing value for -p option");
                        System.exit(1);
                    }
                    break;
                default:
                    inputFiles.add(args[i]);
                    break;
            }
        }
    }


    private static void processFile(String inputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                classifyAndStore(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + inputFile + " - " + e.getMessage());
        }
    }

    private static void classifyAndStore(String line) {
        try {
            int intValue = Integer.parseInt(line);
            integers.add(intValue);
        } catch (NumberFormatException e1) {
            try {
                double doubleValue = Double.parseDouble(line);
                floats.add(doubleValue);
            } catch (NumberFormatException e2) {
                strings.add(line);
            }
        }
    }

    private static void writeResults() {
        writeFile(integerName, integers.stream().map(Object::toString).collect(Collectors.toList()));
        writeFile(floatName, floats.stream().map(Object::toString).collect(Collectors.toList()));
        writeFile(stringName, strings);
    }

    private static void writeFile(String fileName, List<String> data) {
        if (data.isEmpty()) return;

        Path outputPathDir = Paths.get(outputPath);
        if (outputPath.isEmpty() || !Files.exists(outputPathDir)) {
            outputPathDir = Paths.get(".");
        }

        Path filePath = outputPathDir.resolve(prefix + fileName);
        System.out.println("Writing to file: " + filePath.toAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), appendMode))) {
            for (String item : data) {
                writer.write(item);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath + " - " + e.getMessage());
        }
    }

    private static void printStatistics() {
        if (shortStats) {
            if (!integers.isEmpty())
                System.out.println("Integers: count=" + integers.size());
            if (!floats.isEmpty())
                System.out.println("Floats: count=" + floats.size());
            if (!strings.isEmpty())
                System.out.println("Strings: count=" + strings.size());
        }

        if (fullStats) {
            int min = Collections.min(integers);
            int max = Collections.max(integers);
            int sum = integers.stream().mapToInt(Integer::intValue).sum();
            double avg = integers.stream().mapToInt(Integer::intValue).average().orElse(0);
            System.out.println("Min=" + min + ", Max=" + max + ", Sum=" + sum + ", Avg=" + avg);

            double floatsMin = Collections.min(floats);
            double floatsMax = Collections.max(floats);
            double floatsSum = floats.stream().mapToDouble(Double::doubleValue).sum();
            double floatsAvg = floats.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            System.out.println("Min=" + floatsMin + ", Max=" + floatsMax + ", Sum=" + floatsSum + ", Avg=" + floatsAvg);

            if (!strings.isEmpty())
                System.out.println("Strings: count=" + strings.size());
            int minLen = strings.stream().mapToInt(String::length).min().orElse(0);
            int maxLen = strings.stream().mapToInt(String::length).max().orElse(0);
            System.out.println("Min Length=" + minLen + ", Max Length=" + maxLen);
        }
    }
}
