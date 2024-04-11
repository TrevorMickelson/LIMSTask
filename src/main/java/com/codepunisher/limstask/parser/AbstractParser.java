package com.codepunisher.limstask.parser;

import com.codepunisher.limstask.cache.ResultsCache;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

@AllArgsConstructor
public abstract class AbstractParser implements IParser {
    protected final ResultsCache resultsCache;
    protected final File file;
    protected final Logger logger;

    public BufferedReader createBufferedReader() throws IOException {
        return new BufferedReader(new FileReader(file));
    }
}
