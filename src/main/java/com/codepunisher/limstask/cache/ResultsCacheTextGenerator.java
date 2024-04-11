package com.codepunisher.limstask.cache;

import com.codepunisher.limstask.models.Report;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class ResultsCacheTextGenerator {
    private final String name;
    private final Logger logger;
    private final ResultsCache resultsCache;

    public void generate() {
        File file = new File(Paths.get("").toAbsolutePath() + "/" + name + ".txt");
        if (!createFileIfValid(file)) {
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Report report : resultsCache.getReportsList()) {
                writer.write("=========( " + report.getFileName() + " )=========\n");

                writer.write("Name: " + report.getName() + "\n");
                writer.write("Type: " + report.getType() + "\n");
                writer.write("Instrument: " + report.getInstrument() + "\n");
                writer.write("Injection Date: " + report.getInjectionDate().toString() + "\n");
                writer.write("Dilution: " + report.getDilution() + "\n");

                String massResult = resultsCache.getMass(report.getName());
                if (massResult != null) {
                    writer.write("Mass: " + massResult + "\n");
                }

                writer.write(String.format("%-20s%-10s%n", "Analyte", "Result"));
                for (Map.Entry<String, BigDecimal> entry : report.getAnalyteResultsMap().entrySet()) {
                    String analyte = entry.getKey();
                    BigDecimal result = entry.getValue();
                    String formattedRow = String.format("%-20s%-10s%n", analyte, result);
                    writer.write(formattedRow);
                }

                writer.write("==================================\n");
                writer.write("\n");
                writer.write("\n");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "ERROR: Could not write to file!", e.getMessage());
        }
    }

    private boolean createFileIfValid(File file) {
        try {
            if (file.exists()) {
                logger.info(String.format("The file name %s already exists!", name));
                return false;
            }

            return file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.WARNING, "ERROR: Could not create new file!");
            return false;
        }
    }
}
