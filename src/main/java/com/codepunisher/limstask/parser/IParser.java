package com.codepunisher.limstask.parser;

public interface IParser {
    boolean isFileValid();

    /**
     * This is designed to parse/load
     * values into cache dependent on
     * which parser is implementing it
     * <p>
     * This can throw an error if not validated first
     */
    void parseAndLoadIntoCache();
}
