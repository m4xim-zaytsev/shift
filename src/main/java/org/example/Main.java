package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
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

        if (!parseArguments(args, inputFiles)) {
            System.err.println("Invalid arguments. Exiting program.");
            System.exit(1);
        }

        if (inputFiles.isEmpty()) {
            System.err.println("No input files specified. Exiting program.");
            System.exit(1);
        }

        for (String inputFile : inputFiles) {
            processFile(inputFile);
        }

        writeResults();
        printStatistics();
    }

    private static boolean parseArguments(String[] args, List<String> inputFiles) {
        try {
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
                            if (!Files.isDirectory(Paths.get(outputPath))) {
                                System.err.println("Output path does not exist: " + outputPath);
                                return false;
                            }
                        } else {
                            System.err.println("Missing value for -o option");
                            return false;
                        }
                        break;
                    case "-p":
                        if (i + 1 < args.length) {
                            prefix = args[++i];
                        } else {
                            System.err.println("Missing value for -p option");
                            return false;
                        }
                        break;
                    default:
                        if (!Files.isReadable(Paths.get(args[i]))) {
                            System.err.println("Input file is not readable or does not exist: " + args[i]);
                        } else {
                            inputFiles.add(args[i]);
                        }
                        break;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            return false;
        }
    }

    private static void processFile(String inputFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                classifyAndStore(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + inputFile + " - " + e.getMessage());
        }
    }

    private static void classifyAndStore(String line) {
        if (line.isEmpty()) {
            return;
        }
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
            printShortStatistics();
        } else if (fullStats) {
            printFullStatistics();
        }
    }

    private static void printShortStatistics() {
        if (!integers.isEmpty()) {
            System.out.println("Integers: count=" + integers.size());
        }
        if (!floats.isEmpty()) {
            System.out.println("Floats: count=" + floats.size());
        }
        if (!strings.isEmpty()) {
            System.out.println("Strings: count=" + strings.size());
        }
    }

    private static void printFullStatistics() {
        if (!integers.isEmpty()) {
            int min = Collections.min(integers);
            int max = Collections.max(integers);
            long sum = integers.stream().mapToLong(Integer::longValue).sum();
            double avg = integers.stream().mapToInt(Integer::intValue).average().orElse(0);
            System.out.printf("Integers: count=%d, Min=%d, Max=%d, Sum=%d, Avg=%.2f\n",
                    integers.size(), min, max, sum, avg);
        }

        if (!floats.isEmpty()) {
            double floatsMin = Collections.min(floats);
            double floatsMax = Collections.max(floats);
            double floatsSum = floats.stream().mapToDouble(Double::doubleValue).sum();
            double floatsAvg = floats.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            System.out.printf("Floats: count=%d, Min=%.2f, Max=%.2f, Sum=%.2f, Avg=%.2f\n",
                    floats.size(), floatsMin, floatsMax, floatsSum, floatsAvg);
        }

        if (!strings.isEmpty()) {
            System.out.println("Strings: count=" + strings.size());
            int minLen = strings.stream().mapToInt(String::length).min().orElse(0);
            int maxLen = strings.stream().mapToInt(String::length).max().orElse(0);
            System.out.printf("Strings: Min Length=%d, Max Length=%d\n", minLen, maxLen);
        }
    }
}

