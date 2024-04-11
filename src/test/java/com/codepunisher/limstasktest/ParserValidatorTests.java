package com.codepunisher.limstasktest;

import com.codepunisher.limstask.cache.ResultsCache;
import com.codepunisher.limstask.parser.MassesParser;
import com.codepunisher.limstask.parser.ReportsParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParserValidatorTests {
    @Mock
    private Logger logger;
    @Mock
    private ResultsCache resultsCache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsFileValid_MassesParser_NonExistentFile() {
        File nonExistentFile = getMockedFile(file -> {
            when(file.exists()).thenReturn(false);
        });

        assertFalse(getMockedMassesParser(nonExistentFile).isFileValid());
    }

    @Test
    public void testIsFileValid_MassesParser_InvalidExtension() {
        File invalidExtensionFile = getMockedFile(file -> {
            when(file.exists()).thenReturn(true);
            when(file.getName()).thenReturn("test.txt");
        });

        assertFalse(getMockedMassesParser(invalidExtensionFile).isFileValid());
    }

    @Test
    public void testIsFileValid_MassesParser_NullFile() {
        assertFalse(getMockedMassesParser(null).isFileValid());
    }

    @Test
    public void testIsFileValid_ReportsParser_NonExistentDirectory() {
        File nonExistentDirectory = getMockedFile(file -> {
            when(file.exists()).thenReturn(false);
        });

        assertFalse(getMockedReportsParser(nonExistentDirectory).isFileValid());
    }

    @Test
    public void testIsFileValid_ReportsParser_FileInsteadOfDirectory() {
        File nonDirectoryFile = getMockedFile(file -> {
            when(file.exists()).thenReturn(true);
            when(file.isDirectory()).thenReturn(false);
        });

        assertFalse(getMockedReportsParser(nonDirectoryFile).isFileValid());
    }

    @Test
    public void testIsFileValid_ReportsParser_NullDirectory() {
        assertFalse(getMockedReportsParser(null).isFileValid());
    }

    @Test
    public void testIsFileValid_ReportsParser_EmptyDirectory() {
        File emptyDirectory = getMockedFile(file -> {
            when(file.exists()).thenReturn(true);
            when(file.isDirectory()).thenReturn(true);
            when(file.listFiles()).thenReturn(new File[0]);
        });

        assertFalse(getMockedReportsParser(emptyDirectory).isFileValid());
    }


    private File getMockedFile(Consumer<File> fileConsumer) {
        File mockFile = mock(File.class);
        fileConsumer.accept(mockFile);
        return mockFile;
    }

    private MassesParser getMockedMassesParser(File file) {
        return new MassesParser(resultsCache, file, logger);
    }

    private ReportsParser getMockedReportsParser(File file) {
        return new ReportsParser(resultsCache, file, logger);
    }
}
