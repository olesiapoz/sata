package com.autoscaling.autoscaler.model;

public class MetricAggregateByCPU {

    private Double cpuRangePercentileLow;
    private  Double cpuRangePercentileHigh;
    private Double cpuRangeFrom;
    private Double cpuRangeTo;
    private Double CpuRangeViolations;
    private Double cpuRangeEvents;
    private Double cpuRangeRPS;
    private Double cpuRangeSLA;



    public MetricAggregateByCPU(Double cpuRangeFrom, Double cpuRangeTo, double CpuRangeViolations, double cpuRangeEvents, double cpuRangeRPS,double cpuRangeSLA, double cpuRangePercentileLow, double cpuRangePercentileHigh ) {
        this.cpuRangeFrom = cpuRangeFrom;
        this.cpuRangeTo = cpuRangeTo;
        this.CpuRangeViolations = CpuRangeViolations;
        this.cpuRangeEvents = cpuRangeEvents;
        this.cpuRangeRPS = cpuRangeRPS;
        this.cpuRangeSLA = cpuRangeSLA;
        this.cpuRangePercentileLow = cpuRangePercentileLow;
        this.cpuRangePercentileHigh = cpuRangePercentileHigh;
    }

    public Double getCpuRangeFrom() {
        return cpuRangeFrom;
    }
    public void setCpuRangeFrom(Double cpuRangeFrom) {
        this.cpuRangeFrom = cpuRangeFrom;
    }

    public Double getCpuRangeTo() {
        return cpuRangeTo;
    }
    public void setCpuRangeTo(Double cpuRangeTo) {
        this.cpuRangeTo = cpuRangeTo;
    }

    public Double getCpuRangeEvents() {
        return cpuRangeEvents;
    }
    public void setCpuRangeEvents(Double cpuRangeEvents) {
        this.cpuRangeEvents = cpuRangeEvents;
    }

    public Double getCpuRangeViolations() {
        return CpuRangeViolations;
    }
    public void setCpuRangeViolations(Double CpuRangeViolations) {
        this.CpuRangeViolations = CpuRangeViolations;
    }

    public Double getCpuRangeRPS() {
        return cpuRangeRPS;
    }
    public void setCpuRangeRPS(Double cpuRangeRPS) {
        this.cpuRangeRPS = cpuRangeRPS;
    }
    public Double getCpuRangeSLA() {
        return cpuRangeSLA;
    }
    public void setCpuRangeSLA(Double cpuRangeSLA) {
        this.cpuRangeSLA = cpuRangeSLA;
    }

    public Double getCpuRangePercentileLow() {
        return cpuRangePercentileLow;
    }
    public void setCpuRangePercentileLow(Double cpuRangePercentileLow) {
        this.cpuRangePercentileLow = cpuRangePercentileLow;
    }

    public Double getCpuRangePercentileHigh() {
        return cpuRangePercentileHigh;
    }
    public void setCpuRangePercentileHigh(Double cpuRangePercentileHigh) {
        this.cpuRangePercentileHigh = cpuRangePercentileHigh;
    }


}
