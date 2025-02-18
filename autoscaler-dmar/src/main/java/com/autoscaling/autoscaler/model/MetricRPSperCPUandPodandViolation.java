package com.autoscaling.autoscaler.model;

public class MetricRPSperCPUandPodandViolation {

    private Metric metric;
    private Double violation;

    private Double rpsPerPod;

    private Double rpsPerCPU;


    public MetricRPSperCPUandPodandViolation(Metric metric, double rpsPerPod, double rpsPerCPU, double violation ) {
        this.metric = metric;
        this.rpsPerPod = rpsPerPod;
        this.rpsPerCPU = rpsPerCPU;
        this.violation = violation;
    }

    public Metric getMetric() {
        return metric;
    }
    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public Double getRpsPerCPU() {
        return rpsPerCPU;
    }
    public void setRpsPerCPU(Double rpsPerCPU) {
        this.rpsPerCPU = rpsPerCPU;
    }

    public Double getRpsPerPod() {
        return rpsPerPod;
    }
    public void setRpsPerPod(Double rpsPerPod) {
        this.rpsPerPod = rpsPerPod;
    }

    public Double getViolation() {
        return violation;
    }
    public void setViolation(Double violation) {
        this.violation = violation;
    }


}
