package com.codepunisher.limstask.parser;

import com.codepunisher.limstask.cache.ResultsCache;
import com.codepunisher.limstask.models.Report;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportsParser extends AbstractParser {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    public ReportsParser(ResultsCache resultsCache, File file, Logger logger) {
        super(resultsCache, file, logger);
    }

    @Override
    public boolean isFileValid() {
        if (file == null || !file.exists()) {
            logger.log(Level.WARNING, "ERROR: That folder does not exist!");
            return false;
        }

        if (!file.isDirectory()) {
            logger.log(Level.WARNING, String.format("ERROR: The folder %s does not exist!", file.getName()));
            return false;
        }

        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            logger.log(Level.WARNING, "ERROR: There are no files in the directory " + file.getName());
            return false;
        }

        return true;
    }

    @Override
    public void parseAndLoadIntoCache() {
        for (File nestedFile : Objects.requireNonNull(file.listFiles())) {
            if (!nestedFile.getName().toLowerCase().endsWith(".txt")) {
                logger.log(Level.WARNING, "ERROR: File in directory not a txt file! " + nestedFile);
                continue;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(nestedFile))) {
                boolean isTable = false;
                BigDecimal dilution = null;
                Report.ReportBuilder reportBuilder = Report.builder();
                reportBuilder.fileName(nestedFile.getName());

                String line;
                while ((line = br.readLine()) != null) {
                    loadNameAndTypeIfRelevant(line, reportBuilder);

                    loadInstrumentIfRelevant(line, reportBuilder);

                    loadInjectionDateIfRelevant(line, reportBuilder);

                    // Only setting if null (to avoid setting back to null)
                    if (dilution == null) {
                        dilution = loadDilutionIfRelevant(line, reportBuilder);
                    }

                    // Determining line is at the table
                    if (isAnalyzeResultsTable(line)) {
                        isTable = true;
                        continue;
                    }

                    // Table ends here
                    if (line.startsWith("Totals")) {
                        isTable = false;
                    }

                    if (isTable) {
                        loadTable(line, dilution, reportBuilder);
                    }
                }

                resultsCache.addReport(reportBuilder.build());
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadNameAndTypeIfRelevant(String line, Report.ReportBuilder builder) {
        if (line.startsWith("Sample Name")) {
            String[] split = line.split(":")[1].split(",");
            String type = split[0].trim();
            String name = split[1];

            builder.name(name.isEmpty() ? "N/A" : name);
            builder.type(type.isEmpty() ? "SR" : type);
        }
    }

    private void loadInstrumentIfRelevant(String line, Report.ReportBuilder builder) {
        if (line.startsWith("Acq. Instrument")) {
            String[] split = line.split(":");
            String update = split[1].trim().replace("Location", "");
            builder.instrument(update);
        }
    }

    private void loadInjectionDateIfRelevant(String line, Report.ReportBuilder builder) throws ParseException {
        if (line.startsWith("Injection Date")) {
            int amIndex = line.lastIndexOf("AM");
            int pmIndex = line.lastIndexOf("PM");
            int index = Math.max(amIndex, pmIndex) + 3;

            String update = line.substring(0, index)
                    .replace("Injection Date  : ", "");
            builder.injectionDate(dateFormat.parse(update));
        }
    }

    @Nullable
    private BigDecimal loadDilutionIfRelevant(String line, Report.ReportBuilder builder) {
        if (line.startsWith("Dilution")) {
            BigDecimal dilution = BigDecimal.valueOf(Double.parseDouble(line.split(":")[2].trim()));
            builder.dilution(dilution);
            return dilution;
        }

        return null;
    }

    // Assumes were already at the table location in the txt file
    private void loadTable(String line, BigDecimal dilution, Report.ReportBuilder builder) {
        // Splitting via empty spaces (regardless of space size)
        String[] split = line.split("\\s+");
        String analyteName = split[split.length - 1];

        String resultAmount = split[split.length - 2];
        if (resultAmount.isEmpty() || resultAmount.equals("-")) {
            builder.addAnalyteResult(analyteName, BigDecimal.valueOf(0));
            return;
        }

        if (dilution != null) {
            BigDecimal originalConcentration = BigDecimal.valueOf(Double.parseDouble(resultAmount));
            BigDecimal dilutedConcentration = originalConcentration.divide(dilution, 6, RoundingMode.HALF_UP);
            BigDecimal dilutedConcentrationInMgPerML = dilutedConcentration.divide(new BigDecimal("1000"), 6, RoundingMode.HALF_UP);
            builder.addAnalyteResult(analyteName, dilutedConcentrationInMgPerML);
        }
    }

    /**
     * Checking this by determining if the line starts with
     * "-------|" as this is static across all reports txt files
     */
    private boolean isAnalyzeResultsTable(String line) {
        return line.startsWith("-------|");
    }
}
