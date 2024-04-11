package com.codepunisher.limstask.cache;

import com.codepunisher.limstask.models.Report;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ResultsCache {
    // Key -> sample name, Value -> mass
    private final Map<String, String> massesCsvMap = new HashMap<>();
    private final List<Report> reportsList = new ArrayList<>();

    public void addSample(String name, String mass) {
        massesCsvMap.put(name, mass);
    }

    @Nullable
    public String getMass(String sample) {
        return massesCsvMap.get(sample);
    }

    public void addReport(Report report) {
        reportsList.add(report);
    }

    public List<Report> getReportsList() {
        return Collections.unmodifiableList(reportsList);
    }
}
