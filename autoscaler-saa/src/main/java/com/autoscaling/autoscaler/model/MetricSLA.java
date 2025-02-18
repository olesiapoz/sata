package com.autoscaling.autoscaler.model;

public class MetricSLA {

    private Metric metric;
    private Double SLA;


    public MetricSLA(Metric metric, Double SLA) {
        this.metric = metric;
        this.SLA = SLA;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public Double getSLA() {
        return SLA;
    }

    public void setSLA(Double SLA) {
        this.SLA = SLA;
    }

}
