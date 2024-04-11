package com.codepunisher.limstask.parser;

import com.codepunisher.limstask.cache.ResultsCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MassesParser extends AbstractParser {
    public MassesParser(ResultsCache resultsCache, File file, Logger logger) {
        super(resultsCache, file, logger);
    }

    @Override
    public boolean isFileValid() {
        if (file == null || !file.exists()) {
            logger.log(Level.WARNING, "ERROR: Masses file does not exist!");
            return false;
        }

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            logger.log(Level.WARNING, "ERROR: Masses file is not a .csv extension!");
            return false;
        }

        return true;
    }

    @Override
    public void parseAndLoadIntoCache() {
        try (BufferedReader csvBuffer = createBufferedReader()) {
            String csvLine;
            while ((csvLine = csvBuffer.readLine()) != null) {
                String[] csvSplit = csvLine.split(",");

                // Caching name as key, and mass as value
                resultsCache.addSample(csvSplit[1], csvSplit[2]);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error when attempting to parse csv file!", e.getMessage());
        }
    }
}
