package com.codepunisher.limstask;

import com.codepunisher.limstask.cache.ResultsCache;
import com.codepunisher.limstask.cache.ResultsCacheTextGenerator;
import com.codepunisher.limstask.parser.IParser;
import com.codepunisher.limstask.parser.MassesParser;
import com.codepunisher.limstask.parser.ReportsParser;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class LIMSTaskMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // Getting reports folder path
        logger.info("*** Please enter the reports folder path! ***\n");
        String reportsFolderPath = scanner.nextLine();

        // Getting masses csv file path
        logger.info("\n** Please enter the masses.csv file path! ***\n");
        String massesCsvFilePath = scanner.nextLine();

        // Getting desired output file name
        logger.info("\n** Enter your desired \"results\" file name **\n");
        String desiredName = scanner.nextLine();

        File reportsFolder = new File(reportsFolderPath);
        File massesFile = new File(massesCsvFilePath);

        // Cache + Parsers
        ResultsCache resultsCache = new ResultsCache();
        IParser[] parsers = new IParser[] {
                new MassesParser(resultsCache, massesFile, logger),
                new ReportsParser(resultsCache, reportsFolder, logger),
        };

        // Validating/caching/loading all parsers
        for (IParser parser : parsers) {
            if (parser.isFileValid()) {
                parser.parseAndLoadIntoCache();
            }
        }

        // Generating the cached data into a readable text file
        ResultsCacheTextGenerator textGenerator = new ResultsCacheTextGenerator(desiredName, logger, resultsCache);
        textGenerator.generate();
    }
}
