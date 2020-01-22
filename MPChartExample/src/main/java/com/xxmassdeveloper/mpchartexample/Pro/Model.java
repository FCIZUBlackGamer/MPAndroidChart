package com.xxmassdeveloper.mpchartexample.Pro;

import java.util.List;

public class Model {
    List<providerStat> providerStats;
    String newSlotsAverage;
    String followupSlotsAverage;

    public List<providerStat> getProviderStats() {
        return providerStats;
    }

    public void setProviderStats(List<providerStat> providerStats) {
        this.providerStats = providerStats;
    }

    public String getNewSlotsAverage() {
        return newSlotsAverage;
    }

    public void setNewSlotsAverage(String newSlotsAverage) {
        this.newSlotsAverage = newSlotsAverage;
    }

    public String getFollowupSlotsAverage() {
        return followupSlotsAverage;
    }

    public void setFollowupSlotsAverage(String followupSlotsAverage) {
        this.followupSlotsAverage = followupSlotsAverage;
    }
}
