//package com.autoscaling.autoscaler;
//
//import com.autoscaling.autoscaler.model.Metric;
//import com.autoscaling.autoscaler.model.MetricSLA;
//import com.autoscaling.autoscaler.ScalingServiceBench;
//import java.sql.Timestamp;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import com.autoscaling.autoscaler.model.SLA;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class ThresholdSearchTest {
//
//    private Timestamp now = DateUtils.now();
//    private SLA  slaNow = new SLA(now, 98.5);
//    private double thresholdNow = 50.0;
//    @Test
//    public void shouldFindThreshold() {
//
//        final double RESPONSE_TIME_THRESHOLD = 1500;
//        final Double SLA = 95.0;
//
//
//        List<Metric> metrics = new ArrayList<Metric>();
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 1200.0,
//                            10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 700.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 1550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 1550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 1490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 11450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 5.0, 150.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 1560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 1570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 1590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 1630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 800.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 1480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 1250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 5.0, 50.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 5.0, 50.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 5.0, 50.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 49.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 200.0,
//                10));
//
//
//
//        //violation
//        metrics.add(new Metric(1000.1, 1600.0, 90.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 49.3, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 70.5, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 69.4, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 70.3, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 59.3, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 90.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 49.3, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 70.5, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 69.4, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 70.3, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 59.3, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 90.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 49.3, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 70.5, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 69.4, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 70.3, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 59.3, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 60.3, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 59.3, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 60.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 59.0, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 70.1, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 49.1, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 50.1, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 39.1, 980.0,
//                10));
//        metrics.add(new Metric(1000.1, 1600.0, 40.1, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 1660.0, 59.1, 980.0,
//                10));
//
//        //bad values
//        metrics.add(new Metric(700.0, 660.0, 56.0, 1050.0,
//                10));
//
//        metrics.add(new Metric(10000.0, 16000.0, 95.0, 0.0,
//                10));
//        metrics.add(new Metric(Double.NaN, Double.NaN, 95.0, 0.0,
//                10));
//
//        //assertEquals(66.0, ScalingServiceBench.searchForThreshold(metrics,980.0, SLA, RESPONSE_TIME_THRESHOLD));
//        //assertEquals(36.0, ScalingServiceBench.returnCPUThreshold(metrics,  slaNow));
//
///*
//        CPUSettings cpuSettings = new CPUSettings(CPUValues.defaultUpscalingValues(), CPUValues.defaultDownscalingValues());
//        CPUAdjustor cpuAdjustor = new CustomCPUAdjustor();
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//
//        assertEquals(0.80, cpuSettings.getUpscalingValues().getUpper());
//        assertEquals(0.70, cpuSettings.getUpscalingValues().getMid());
//        assertEquals( 0.60, cpuSettings.getUpscalingValues().getLower());
//
//        assertEquals( CPUAdjustor.DOWNSCALING_UPPER_MAX, cpuSettings.getDownscalingValues().getUpper());
//        assertEquals(CPUAdjustor.DOWNSCALING_MID_MAX, cpuSettings.getDownscalingValues().getMid());
//        assertEquals(CPUAdjustor.DOWNSCALING_LOWER_MAX, cpuSettings.getDownscalingValues().getLower());
//
// */
//    }
//    @Test
//    public void shouldOverprovisionFindThreshold() {
//
//        final double RESPONSE_TIME_THRESHOLD = 1500;
//        final Double sla = 95.0;
//
//
//        List<Metric> metrics = new ArrayList<Metric>();
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//        metrics.add(new Metric(1980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(1980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(1700.0, 660.0, 85.0, 350.0,
//                10));
//        metrics.add(new Metric(1980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.1, 600.0, 50.0, 50.0,
//                10));
//        metrics.add(new Metric(1980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(1980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(1980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(1980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 65.0, 250.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 69.0, 290.0,
//                10));
//        metrics.add(new Metric(1980.0, 660.0, 50.0, 200.0,
//                10));
//
//
//
//        //anomalies
//        metrics.add(new Metric(980.1, 500.0, 99.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 1980.0,
//                10));
//
//        //bad values
//        metrics.add(new Metric(700.0, 660.0, 56.0, 1050.0,
//                10));
//
//        metrics.add(new Metric(1000.0, 1600.0, 95.0, 0.0,
//                10));
//        metrics.add(new Metric(Double.NaN, Double.NaN, 95.0, 0.0,
//                10));
//
//       // assertEquals(66.0, ScalingServiceBench.searchForThreshold(metrics,980.0, SLA, RESPONSE_TIME_THRESHOLD));
//        //assertEquals(26.0, ScalingServiceBench.returnCPUThreshold(metrics,slaNow));
//
///*
//        CPUSettings cpuSettings = new CPUSettings(CPUValues.defaultUpscalingValues(), CPUValues.defaultDownscalingValues());
//        CPUAdjustor cpuAdjustor = new CustomCPUAdjustor();
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//
//        assertEquals(0.80, cpuSettings.getUpscalingValues().getUpper());
//        assertEquals(0.70, cpuSettings.getUpscalingValues().getMid());
//        assertEquals( 0.60, cpuSettings.getUpscalingValues().getLower());
//
//        assertEquals( CPUAdjustor.DOWNSCALING_UPPER_MAX, cpuSettings.getDownscalingValues().getUpper());
//        assertEquals(CPUAdjustor.DOWNSCALING_MID_MAX, cpuSettings.getDownscalingValues().getMid());
//        assertEquals(CPUAdjustor.DOWNSCALING_LOWER_MAX, cpuSettings.getDownscalingValues().getLower());
//
// */
//    }
//
//    @Test
//    public void shouldUnderprovisionFindThreshold() {
//
//        final double RESPONSE_TIME_THRESHOLD = 1500;
//        final Double SLA = 95.0;
//
//
//        List<Metric> metrics = new ArrayList<Metric>();
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 1600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 1660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 1350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 1550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 1550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 1490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 1160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 1560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 1570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 1590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 1630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 1400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 1480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 1250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 1290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 1200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 1600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 1660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 1350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 1550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 1550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 1490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 1450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 1160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 24.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 39.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 200.0,
//                10));
//        metrics.add(new Metric(980.1, 1000.0, 60.0, 600.0,
//                10));
//        metrics.add(new Metric(980.0, 1100.0, 66.0, 660.0,
//                10));
//        metrics.add(new Metric(700.0, 660.0, 35.0, 350.0,
//                10));
//        metrics.add(new Metric(980.0, 860.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 1160.0, 55.0, 550.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 49.0, 490.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 45.0, 450.0,
//                10));
//        metrics.add(new Metric(980.1, 600.0, 16.0, 160.0,
//                10));
//        metrics.add(new Metric(980.0, 900.0, 56.0, 560.0,
//                10));
//        metrics.add(new Metric(980.0, 1260.0, 57.0, 570.0,
//                10));
//        metrics.add(new Metric(980.0, 990.0, 59.0, 590.0,
//                10));
//        metrics.add(new Metric(980.0, 1420.0, 63.0, 630.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 40.0, 400.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 48.0, 480.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 25.0, 250.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 29.0, 290.0,
//                10));
//        metrics.add(new Metric(980.0, 660.0, 20.0, 200.0,
//                10));
//
//
//        //anomalies
//        metrics.add(new Metric(1500.1, 2500.0, 90.0, 450.0,
//                10));
//        metrics.add(new Metric(1980.0, 2260.0, 49.0, 980.0,
//                10));
//        metrics.add(new Metric(1500.1, 2500.0, 15.0, 1500.0,
//                10));
//        metrics.add(new Metric(980.0, 2260.0, 29.0, 5290.0,
//                10));
//
//        //bad values
//        metrics.add(new Metric(700.0, 660.0, 56.0, 1050.0,
//                10));
//
//        metrics.add(new Metric(1000.0, 1600.0, 95.0, 0.0,
//                10));
//        metrics.add(new Metric(Double.NaN, Double.NaN, 95.0, 0.0,
//                10));
//
//        // assertEquals(66.0, ScalingServiceBench.searchForThreshold(metrics,980.0, SLA, RESPONSE_TIME_THRESHOLD));
//        //assertEquals(26.0, ScalingServiceBench.returnCPUThreshold(metrics, slaNow));
//    }
//
//    /*
//        CPUSettings cpuSettings = new CPUSettings(CPUValues.defaultUpscalingValues(), CPUValues.defaultDownscalingValues());
//        CPUAdjustor cpuAdjustor = new CustomCPUAdjustor();
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
//
//        assertEquals(0.80, cpuSettings.getUpscalingValues().getUpper());
//        assertEquals(0.70, cpuSettings.getUpscalingValues().getMid());
//        assertEquals( 0.60, cpuSettings.getUpscalingValues().getLower());
//
//        assertEquals( CPUAdjustor.DOWNSCALING_UPPER_MAX, cpuSettings.getDownscalingValues().getUpper());
//        assertEquals(CPUAdjustor.DOWNSCALING_MID_MAX, cpuSettings.getDownscalingValues().getMid());
//        assertEquals(CPUAdjustor.DOWNSCALING_LOWER_MAX, cpuSettings.getDownscalingValues().getLower());
//
// *//*
//    }
//
//    *//*
//    @Test
//    public void shouldReachMinValues() {
//
//        CPUSettings cpuSettings = new CPUSettings(CPUValues.defaultUpscalingValues(), CPUValues.defaultDownscalingValues());
//        CPUAdjustor cpuAdjustor = new CustomCPUAdjustor();
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
//
//        assertEquals(0.30, cpuSettings.getUpscalingValues().getUpper());
//        assertEquals(0.20, cpuSettings.getUpscalingValues().getMid());
//        assertEquals( 0.10, cpuSettings.getUpscalingValues().getLower());
//
//        assertEquals( CPUAdjustor.DOWNSCALING_UPPER_MIN, cpuSettings.getDownscalingValues().getUpper());
//        assertEquals( CPUAdjustor.DOWNSCALING_MID_MIN, cpuSettings.getDownscalingValues().getMid());
//        assertEquals(CPUAdjustor.DOWNSCALING_LOWER_MIN, cpuSettings.getDownscalingValues().getLower());
//    }
//
//
//    class CustomCPUAdjustor extends CPUAdjustor {
//        @Override
//        protected boolean isTimeToAdjust() {
//            return true;
//        }
//    }
//*/
//
//}