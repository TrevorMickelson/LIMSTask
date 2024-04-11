package com.codepunisher.limstask.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Report {
    @Getter
    private final String fileName;
    @Getter
    private final String name;
    @Getter
    private final String type;
    @Getter
    private final String instrument;
    @Getter
    private final Date injectionDate;
    @Getter
    private final BigDecimal dilution;
    private final Map<String, BigDecimal> analyteResultsMap;

    public Map<String, BigDecimal> getAnalyteResultsMap() {
        return Collections.unmodifiableMap(analyteResultsMap);
    }

    public static ReportBuilder builder() {
        return new ReportBuilder();
    }

    public static class ReportBuilder {
        private String fileName;
        private String name;
        private String type;
        private String instrument;
        private Date injectionDate;
        private BigDecimal dilution;
        private final Map<String, BigDecimal> analyteResultsMap = new HashMap<>();

        public ReportBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ReportBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ReportBuilder type(String type) {
            this.type = type;
            return this;
        }

        public ReportBuilder instrument(String instrument) {
            this.instrument = instrument;
            return this;
        }

        public ReportBuilder injectionDate(Date injectionDate) {
            this.injectionDate = injectionDate;
            return this;
        }

        public ReportBuilder dilution(BigDecimal dilution) {
            this.dilution = dilution;
            return this;
        }

        public ReportBuilder addAnalyteResult(String analyte, BigDecimal result) {
            analyteResultsMap.put(analyte, result);
            return this;
        }

        public Report build() {
            return new Report(fileName, name, type, instrument, injectionDate, dilution, analyteResultsMap);
        }
    }
}
