package com.csc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheeseAnalyzer {
    public static void main(String[] args) {
        String inputFilePath = "cheese_data.csv"; // Update with actual file path
        String outputFilePath = "output.txt";
        
        int pasteurizedCount = 0;
        int rawCount = 0;
        int organicHighMoistureCount = 0;
        Map<String, Integer> milkTypeCount = new HashMap<>();
        double totalMoisture = 0.0;
        int moistureCount = 0;
        int lacticCheeseCount = 0;

        Set<Integer> cheeseIds = new HashSet<>();
        Set<Integer> missingIds = new TreeSet<>();
        
        Pattern lacticPattern = Pattern.compile("\\b(lactic)\\b", Pattern.CASE_INSENSITIVE);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line = reader.readLine(); // Read header line and ignore
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) continue; // Skip incomplete rows

                try {
                    int cheeseId = Integer.parseInt(data[0].trim());
                    cheeseIds.add(cheeseId);
                } catch (NumberFormatException ignored) {}

                String milkTreatment = data[1].trim();
                if (milkTreatment.equalsIgnoreCase("Pasteurized")) {
                    pasteurizedCount++;
                } else if (milkTreatment.equalsIgnoreCase("Raw")) {
                    rawCount++;
                }

                try {
                    int organic = Integer.parseInt(data[2].trim());
                    double moisturePercent = Double.parseDouble(data[3].trim());
                    if (organic == 1 && moisturePercent > 41.0) {
                        organicHighMoistureCount++;
                    }
                    totalMoisture += moisturePercent;
                    moistureCount++;
                } catch (NumberFormatException ignored) {}

                String milkType = data[4].trim();
                milkTypeCount.put(milkType, milkTypeCount.getOrDefault(milkType, 0) + 1);

                String flavor = data[5].trim();
                Matcher matcher = lacticPattern.matcher(flavor);
                if (matcher.find()) {
                    lacticCheeseCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxCount = Collections.max(milkTypeCount.values());
        String mostCommonMilkType = milkTypeCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown");

        double averageMoisture = (moistureCount > 0) ? totalMoisture / moistureCount : 0.0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("Number of cheeses using pasteurized milk: " + pasteurizedCount + "\n");
            writer.write("Number of cheeses using raw milk: " + rawCount + "\n");
            writer.write("Number of organic cheeses with moisture > 41.0%: " + organicHighMoistureCount + "\n");
            writer.write("Most common milk type: " + mostCommonMilkType + "\n");
            writer.write("Average moisture percentage: " + String.format("%.2f", averageMoisture) + "\n");
            writer.write("Number of lactic cheeses: " + lacticCheeseCount + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Identify missing IDs
        for (int i = 200; i <= Collections.max(cheeseIds); i++) {
            if (!cheeseIds.contains(i)) {
                missingIds.add(i);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("missing_ids.txt"))) {
            for (int id : missingIds) {
                writer.write(id + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
