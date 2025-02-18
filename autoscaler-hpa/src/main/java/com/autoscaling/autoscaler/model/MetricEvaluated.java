package com.autoscaling.autoscaler.model;

import java.sql.Timestamp;

public class MetricEvaluated {

    private Double percentileResponse;
    private Double avgResponse;
    private Double avgCPU;
    private Double throughput;
    private Integer podCount;
    

    public MetricEvaluated(Double percentileResponse, Double avgResponse, Double avgCPU,  Double throughput, Integer  podCount) {
        this.percentileResponse = percentileResponse;
        this.avgResponse = avgResponse;
        this.avgCPU = avgCPU;
        this.podCount = podCount;
        this.throughput = throughput;
    }

    public Double getPercentileResponse() {
        return percentileResponse;
    }

    public void setPercentileResponse(Double percentileResponse) {
        this.percentileResponse = percentileResponse;
    }

    public Double getAVGResponse() {
        return avgResponse;
    }

    public void setAvgResponse(Double AVGResponse) {
        this.avgResponse = avgResponse;
    }

    public Double getAvgCPU() {
        return avgCPU;
    }

    public void setAvgCPU(Double AvgCPU) {
        this.avgCPU = avgCPU;
    }

    public Double getThroughput() {
        return throughput;
    }

    public void setThroughput (Double throughput) {
        this.throughput = throughput;
    }

    public Integer getPodCount() {
        return podCount;
    }

    public void setPodCount(Integer podCount) {
        this.podCount = podCount;
    }

    
}
