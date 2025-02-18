package com.autoscaling.autoscaler.model;

import java.sql.Timestamp;

public class TouchstonePodCount {

    private Integer value;

    public TouchstonePodCount(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TouchstonePodCount{" +
                "value=" + value +
                '}';
    }

    public String toCSVLine() {
        return Integer.toString(value);

    }
}
