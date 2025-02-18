//package com.autoscaling.autoscaler;
//
//import com.autoscaling.autoscaler.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.lang.Math;
//import java.sql.Timestamp;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import javax.annotation.PostConstruct;
//import java.util.stream.DoubleStream;
//
//import io.fabric8.kubernetes.client.*;
//import io.fabric8.kubernetes.client.dsl.base.PatchContext;
//import io.fabric8.kubernetes.client.dsl.base.PatchType;
//
//@Service
//public class ScalingServiceBench {
//    public static final Integer MAX_REPLICAS = 28;
//    public static final Integer MIN_REPLICAS = 1;
//
//    private static double SLANow = 101.00;
//    private static double SLABefore = 101.00;
//
//    private static double SLABeforeBefore = 101.00;
//    private double SLADiff = 0;
//
//
//    private static final Integer sufficientPeriodsNumber = 100;
//
//    private static final double RESPONSE_TIME_THRESHOLD = 1500;
//    private static final Integer performanceEvaluationPeriods = 100;
//    private Integer performanceEvaluationPeriodsleft = 150;
//    private static double init_CPU = 50.0;
//
//    private static double last_cpu = 50.0;
//    private static int n = 0;
//
//    private static boolean notDefaultThreshold = false;
//
//    private Timestamp lastTimeUpdated = null;
//
//    private static boolean sufficientNumberOfEventsCollectedFlag = false;
//    private static int scalePeriod = 90;
//    private static int  stabilizationWindowLength = 180;
//    private static int monitoringSamplePeriod = 6;
//    private static int monitoringSamplesCount = 0;
//    private static int underProvisioningPeriodsCount =0;
//    private static int thresholdUpdatePeriods = 5;//5-10
//    private int thresholdUpdatePeriodLength = scalePeriod*thresholdUpdatePeriods;
//    private static int thresholdEvaluationPeriods = 3*thresholdUpdatePeriods;//3-5
//    private static final Integer sufficientEventNumber =  scalePeriod*thresholdEvaluationPeriods/monitoringSamplePeriod >= 300 ? scalePeriod*thresholdEvaluationPeriods/monitoringSamplePeriod : 300;//or min 300 events
//    private static int storedMetricsSizeForThresholdEvaluation = 0;
//    private Timestamp lastTimeStored;
//    private static int sla_drop = 0;
//    private static Double candidate;
//
//    private int noLoadPeriodsCount=0;
//
//    private static boolean noLoadFlag = false;
//
//    private static boolean slaDropFlag = false;
//    private static boolean underProvisioningFlag = false;
//
//    @Autowired
//    private ValuesService valuesService;
//
//
//    @Autowired
//    private MetricsService metricsService;
//
//    // private HashMap<Timestamp, ArrayList> avgRTtoValues = new HashMap<Timestamp,
//    // ArrayList>();
//    // private HashMap<Timestamp, ArrayList> RTtoValues = new HashMap<Timestamp,
//    // ArrayList>();
//
//    private List<List<Double>> avgRTtoValues = new ArrayList<>();
//    private List<List<Double>> RTtoValues = new ArrayList<>();
//    private ArrayList<Double> values = new ArrayList<Double>();
//    private List<Double> tempThresholds = new ArrayList<>();
//    private List<Metric> metrics = new ArrayList<Metric>();
//    private ArrayList<Thresholds> thresholds = new ArrayList<Thresholds>();
//    private List tempAvg = new ArrayList<Double>();
//    private List tempPercentile = new ArrayList<Double>();
//    private static Queue<List<MetricRPSperCPUandPodandViolation>> metricsByScalePeriods = new LinkedList<>();
//
//
//    // Add field- Current threshold, transition state (rolling update)
//
//    private final ExecutorService valuesCollector = Executors.newSingleThreadExecutor();
//
//    @PostConstruct
//    public void init() {
//        valuesCollector.execute(() -> {
//            while (true) {
//                try {
//                    AvgResponse percentileResponse = valuesService.getPercentileResponse();
//                    Integer currentReplicas = valuesService.getLastPodCount();
//                    Double throughput = valuesService.getThroughputBench().getValue();
//                    // Velocity txVelocity = valuesService.getTXVelocity();
//                    Double avgCPU = valuesService.getLastKnownCPU().getValue() * 100;
//                    AvgResponse avgResponse = valuesService.getAVGResponse();
//                    SlaFactor slaFactor = valuesService.getSlaFactor();
//                    SLA sla = valuesService.getSla();
//
//                    valuesService.setSufficientNumberOfEventsCollectedFlag(sufficientNumberOfEventsCollectedFlag);
//
//                    metrics.add(new Metric(percentileResponse.getValue(), avgResponse.getValue(), avgCPU, throughput,
//                            currentReplicas));
//                    //noLoadPeriodsCount = percentileResponse.getValue().isNaN() ? noLoadPeriodsCount+1 : noLoadPeriodsCount;
//                    monitoringSamplesCount++;
//
//                    if (!percentileResponse.getValue().isNaN()) {
//                        //bench(percentileResponse, currentReplicas, throughput, avgCPU, avgResponse, sla, metrics);
//                        printMetrics(percentileResponse, currentReplicas, throughput, avgCPU, avgResponse, sla);
//
//                        int violationBoundary = sla.getValue() > MetricsService.TARGET_SLA ? 4 : 3;
//                       if (isTimeToStoreMetricsPeriod(scalePeriod)) {
//                           sla_drop = SLABefore - sla.getValue() > 0.002 * MetricsService.TARGET_SLA ? sla_drop + 1 : 0;
//                           SLABefore = sla.getValue();
//
//                           if (metrics.size() != 0) {
//                               long noLoadEventsCount = metrics.stream().filter(m -> m.getPercentileResponse().isNaN()).count();
//                               long underProvisioningEventsCount = metrics.stream().filter(m -> (m.getPercentileResponse() == 0.0 && m.getThroughput() == 0.0) || m.getPodCount() == 0).count();
//                               System.out.println("Nr of samples with no load in this period is: " + noLoadEventsCount + " Underprovisioning  events " + underProvisioningEventsCount + " out of " + monitoringSamplesCount);
//                               noLoadPeriodsCount = noLoadEventsCount > 2 ? noLoadPeriodsCount + 1 : 0;
//                               //underProvisioningPeriodsCount = metrics.size() > (scalePeriod / monitoringSamplePeriod - 3) ? (underProvisioningEventsCount >= 1 ? underProvisioningPeriodsCount + 1 : 0) : underProvisioningPeriodsCount + 1;
//                               underProvisioningPeriodsCount = ((metrics.size() >= 0.8*(double)(scalePeriod / monitoringSamplePeriod)) || underProvisioningEventsCount <= 2) ? 0 : underProvisioningPeriodsCount + 1;
//                               preprocessMetricsAndStore(metrics);
//                                System.out.println("Metrics size: " + metrics.size() + " Nr of samples with no load in this period is: " + noLoadEventsCount + " Underprovisioning  events " +   underProvisioningEventsCount + " out of " + monitoringSamplesCount + " SLA drop: " + sla_drop + " Undeprov count " + underProvisioningPeriodsCount);
//                               metrics.clear();
//
//                               lastTimeStored = DateUtils.now();
//                           } else {
//                               System.out.println("No metrics collected at this period");
//                           }
//
//
//                           noLoadFlag = setFlag(noLoadPeriodsCount, violationBoundary);
//                           slaDropFlag = setFlag(sla_drop, violationBoundary);
//                           underProvisioningFlag =  setFlag(underProvisioningPeriodsCount, violationBoundary);
//                           monitoringSamplesCount = 0;
//                       }
//
//                        /*if (slaDropFlag || underProvisioningFlag)
//                        {
//                            if (isTimeToUpdateThreshold(thresholdUpdatePeriodLength/2)) {
//                                System.out.println("expediting scaling because SLAflag="+slaDropFlag+ " or Under=" + underProvisioningFlag + " at " + DateUtils.now());
//                                scale(sla, true);
//                                lastTimeUpdated = DateUtils.now();
//                                sla_drop = 0;
//                                underProvisioningPeriodsCount = 0;
//                            }
//                        }
//                        */
//                        boolean expedite = (slaDropFlag || underProvisioningFlag || sla.getValue() < 50.0 ) ? true : false;
//                        System.out.println("expediting SLAflag="+slaDropFlag+ " or Under=" + underProvisioningFlag  +" and expedite flag is " +expedite  + " at " + DateUtils.now());
//
//
//                        if (isTimeToUpdateThreshold(expedite ?   (3*scalePeriod-2 ) : thresholdUpdatePeriodLength   )) {
//                            //in the futurre evalue options for dynamic adjustments:
//                            // number of request per time
//                            //adjust this time based on SLA, if below or above make it more frequent to update)
//                            //need to think how to avoid local minimums and max
//                            //aalso make "sliding winodw approach, that is old metrics are forgotten and latest has biggest impact"
//                            System.out.println("It is time for normal scale at " + DateUtils.now() +" and expedite flag is " +expedite + "scale window length is " + (expedite ? thresholdUpdatePeriodLength :  3*scalePeriod )+ "and vboundary is " + violationBoundary);
//
//                                scale(sla, expedite);
//                                //if (storedMetricsSizeForThresholdEvaluation == 0) {
//                                lastTimeUpdated = DateUtils.now();
//                                //}
//                                sla_drop = sla_drop < violationBoundary ? sla_drop : 0;
//                                underProvisioningPeriodsCount = underProvisioningPeriodsCount < violationBoundary ? underProvisioningPeriodsCount : 0;
//                        }
//                            /*if(!notDefaultThreshold){
//                                if (isTimeToUpdateThreshold(thresholdUpdatePeriodLength/3)) {
//                                System.out.println("scaling before metrics are collected");
//                                scale(sla,false);
//                                lastTimeUpdated = DateUtils.now();
//                        }
//                        */
//
//
//                    }
//
//
//                    Thread.sleep(3000);
//
//                } catch (Exception e) {
//                    System.out.println("Reading metrics " + e.getMessage());
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    private boolean setFlag(int count, int violationBoundary){
//        return count >= violationBoundary ? true : false;
//    }
//
//    private boolean isTimeToUpdateThreshold(int scaleInterval) {
//        if (lastTimeUpdated == null)
//            return true;
//        Timestamp now = DateUtils.now();
//        Timestamp then = DateUtils.fromDate(lastTimeUpdated, Calendar.SECOND, scaleInterval);
//
//        return then.before(now);
//    }
//
//    private boolean isTimeToStoreMetricsPeriod(int interval) {
//
//        if (lastTimeStored == null)
//            return true;
//
//        Timestamp now = DateUtils.now();
//        Timestamp then = DateUtils.fromDate(lastTimeStored, Calendar.SECOND, interval);
//        //System.out.println(interval);
//        return then.before(now);
//    }
//
//    public void printMetrics(AvgResponse percentileResponse, Integer currentReplicas, Double throughput, Double avgCPU,
//                             AvgResponse avgResponse, SLA sla) {
//        double avgvalue = avgResponse.getValue();
//        double value = percentileResponse.getValue();
//        // System.out.println("Avg now: " + Math.floor(avgvalue) + " Percentile now: " +
//        // Math.floor(value) + " RPS: " + throughput + " Pods: " + currentReplicas);
//        try {
//            System.out.println("Avg CPU: " + Math.floor(avgCPU) + " Avg now: " + Math.floor(avgvalue)
//                    + " Percentile now: " + Math.floor(value) + " RPS: " + throughput + " Pods: " + currentReplicas
//                    + "rps: " + Double.valueOf(throughput / currentReplicas) + " SLA: " + sla.getValue());
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }
//
//    public static void updateHPA(double CPU) {
//        System.out.println("Updating HPA with" + (int) Math.ceil(CPU) + " at " + DateUtils.now());
//        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
//            client.autoscaling().v2().horizontalPodAutoscalers().inNamespace("default").withName("demo1-hpa").patch(
//                    PatchContext.of(PatchType.JSON_MERGE),
//                    "{\"spec\":{\"metrics\":[{\"resource\":{\"name\": \"cpu\",\"target\":{\"averageUtilization\":"
//                            + (int) Math.ceil(CPU) + ",\"type\": \"Utilization\"}},\"type\": \"Resource\"}]}}");
//        } catch (Exception e) {
//            System.out.println("Current HPA cannot be updated due to error listed below");
//            System.out.println(e);
//        }
//
//    }
//
//    public static double getCurrentHPACPUThreshold() {
//
//        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
//            double avgCPUThreshold = client.autoscaling().v2().horizontalPodAutoscalers().inNamespace("default").withName("demo1-hpa").get().getSpec().getMetrics().get(0).getResource().getTarget().getAverageUtilization().doubleValue();
//            System.out.println("Current HPA threshold is " + avgCPUThreshold);
//            return avgCPUThreshold;
//        } catch (Exception e) {
//            System.out.println("Current HPA cannot be collected due to error listed below");
//            System.out.println(e);
//            return last_cpu;
//        }
//
//    }
//
//    public double getMedian(List<Metric> metricsSlice) {
//        DoubleStream sortedAvgResponse = metricsSlice.stream().mapToDouble(Metric::getAVGResponse).sorted();
//        double median = metricsSlice.size() % 2 == 0
//                ? sortedAvgResponse.skip(metricsSlice.size() / 2 - 1).limit(2).average().getAsDouble()
//                : sortedAvgResponse.skip(metricsSlice.size() / 2).findFirst().getAsDouble();
//        return median;
//    }
//
//    public static void sortCPURange(List<MetricAggregateByCPU> list) {
//
//        list.sort((o1, o2) -> o1.getCpuRangeFrom().compareTo(
//                o2.getCpuRangeFrom()));
//    }
//
//
//    //NEW APPROACH
//
//    //"ToDo"
//    //When overporvissioning, think how to ensure, that values does not go beyond max real cpu measure
//    //Try CPU range Removal to see better picture? Not suitable for small number of events...
//    public static List<Metric> preprocessMetrics(List<Metric> metrics) {
//        List<Metric> sortedbyCPUMetrics = metrics;
//        //sortCPU(sortedbyCPUMetrics);
//        List<Metric> preprocessedMetrics = sortedbyCPUMetrics.stream()
//                .filter(r -> r.getPercentileResponse() != 0.0 && r.getPodCount() != 0 && r.getThroughput() != 0.0 && r.getAvgCPU() != 0.0)
//                .collect(Collectors.toList());
//        System.out.println(preprocessedMetrics.size() +" of " + metrics.size() + " metrics are preprocessed");
//        return preprocessedMetrics;
//    }
//
//    public static void preprocessMetricsAndStore(List<Metric> metrics) {
//        System.out.println("Storing preprocessed metrics at " + DateUtils.now());
//        List<MetricRPSperCPUandPodandViolation> preprocessedMetrics = calculateRPSperPodPerCPUandViolation(preprocessMetrics(replacePercentileZeroWithValue(removeNA(metrics), 98040d)));
//        if(preprocessedMetrics.size() > 0){
//        metricsByScalePeriods.add(preprocessedMetrics);
//    }
//        System.out.println("preprocessMetricsAndStore: Added " +  preprocessedMetrics.size() );
//        //storedMetricsSizeForThresholdEvaluation = preprocessedMetrics.size() + storedMetricsSizeForThresholdEvaluation;
//
//    }
//
//    private static List<Metric> removeNA(List<Metric> metrics) {
//        List<Metric> nMetrics = metrics.stream().filter(m -> !m.getPercentileResponse().isNaN()).collect(Collectors.toList());
//        System.out.println("NaNs are preprocessed.");
//        return nMetrics;
//    }
//
//    public static void scale( SLA sla, boolean expedite) {
//
//        //System.out.println("Preprocessing metrics");
//        //List<MetricRPSperCPUandPodandViolation> preprocessedMetrics = calculateRPSperPodPerCPUandViolation(replacePercileNaNwithValue(preprocessMetrics(metrics), 98040d));
//        //System.out.println("Adding metrics to queue");
//        if(metricsByScalePeriods.size() > 0){
//            System.out.println("Available periods for scale" + metricsByScalePeriods.size() );
//            //metricsByScalePeriods.add(preprocessedMetrics);
//            if (metricsByScalePeriods.size()>=thresholdEvaluationPeriods ){
//                 System.out.println("Probably need to update number of stored periods");
//                if (concatinatedListFromQueue(metricsByScalePeriods).size() > sufficientEventNumber){
//                System.out.println("Updating queue size");
//                updateQueueSize(getQueueSize(metricsByScalePeriods));
//                }
//            }
//        if(concatinatedListFromQueue(metricsByScalePeriods).size() !=0) {
//        System.out.println("Updating HPA based on " + concatinatedListFromQueue(metricsByScalePeriods).size() + " metrics");
//        updateHPA(returnCPUThreshold(concatinatedListFromQueue(metricsByScalePeriods), sla, expedite));
//        }
//    }
//    }
//
//    public static  Integer getQueueSize(Queue<List<MetricRPSperCPUandPodandViolation>> metrics) {
//        //int i = thresholdEvaluationPeriods;
//        int i = -1;
//
//        Queue<List<MetricRPSperCPUandPodandViolation>> tempMetrics = new LinkedList<>(metrics);
//
//
//        List<MetricRPSperCPUandPodandViolation> tempList = filterOutliers(concatinatedListFromQueue(tempMetrics));
//        System.out.println(" getQueueSize: Received queue of length " + tempMetrics.size() + "with nr of events is " + tempList.size());
//        if(tempList.size()>sufficientEventNumber){
//        while(sufficientNumberOfEvents(tempList) &&  Math.abs(i - metrics.size()) >=thresholdEvaluationPeriods ){
//            tempList = filterOutliers(concatinatedListFromQueue(updateLocalQueue(tempMetrics, 1)));
//            i++;
//            System.out.println(" getQueueSize: shrinking queue to size " + (metrics.size()-i) + "when nr of events is " + tempList.size());
//            }
//        System.out.println(" getQueueSize: The ideal queue size is " + (metrics.size()-i));
//        }
//        else{
//            return metrics.size()-1;
//        }
//
//        return metrics.size()-i;
//    }
//
//    private static Queue<List<MetricRPSperCPUandPodandViolation>> updateLocalQueue( Queue<List<MetricRPSperCPUandPodandViolation>> queue, int i){
//        System.out.println("updateLocalQueue: Receivel queue of length " + i + " with original queue length of is " + queue.size());
//
//        for (int k = 0; k < Math.abs(i); k++) {
//            queue.poll();
//        }
//        return queue;
//    }
//
//    // private static Queue<List<MetricRPSperCPUandPodandViolation>> metricsByScalePeriods = new LinkedList<>();
//    //update queue to size  that contains sufficent events and periods number
//    public static void updateQueueSize(int i) {
//
////        int sumOfEvents = 0;
////        int totalSumOfEvents = 0;
////        List<Integer> sizePerPeriods = new ArrayList<>();
////        int i = 0;
////
////        for (List<MetricRPSperCPUandPodandViolation> period : metricsByScalePeriods) {
////            sizePerPeriods.add(period.size());
////        }
////
////        for (int n : sizePerPeriods) {
////            totalSumOfEvents += n;
////        }
////
////        if (totalSumOfEvents >= sufficientEventNumber) {
////            if (metricsByScalePeriods.size() >= sufficientPeriodsNumber) {
////                while (sumOfEvents <= sufficientEventNumber && i <= sufficientPeriodsNumber) {
////                    sumOfEvents += sizePerPeriods.get(sizePerPeriods.size() - i - 1);
////                    i++;
////                }
////                for (int k = 0; k < Math.abs(i - metricsByScalePeriods.size()); k++) {
////                    metricsByScalePeriods.poll();
////                }
////            }
////        }
//        if(i > thresholdEvaluationPeriods){
//        for (int k = 0; k < Math.abs(i - metricsByScalePeriods.size()); k++) {
//                    metricsByScalePeriods.poll();
//        }
//    }
//
//    }
//
//    public static List<MetricRPSperCPUandPodandViolation> concatinatedListFromQueue(Queue<List<MetricRPSperCPUandPodandViolation>> metricsbyPeriods) {
//        System.out.println("Cancatinating metrics of size " + metricsbyPeriods.size());
//
//        List<MetricRPSperCPUandPodandViolation> concatenatedList = new ArrayList<>();
//
//        for (List<MetricRPSperCPUandPodandViolation> period : metricsbyPeriods) {
//                    concatenatedList.addAll(period);
//        }
//
//        return concatenatedList;
//    }
//
//        //List<MetricRPSperCPUandPodandViolation> preprocessedMetrics = metricsbyPeriods.
//
//
//
//    public static double returnCPUThreshold (List<MetricRPSperCPUandPodandViolation> metrics,  SLA sla, boolean expediteThresholdUpdate)
//    {
//
//       // List<MetricRPSperCPUandPodandViolation> preprocessedMetrics = calculateRPSperPodPerCPUandViolation(replacePercileNaNwithValue(preprocessMetrics(metrics), 98040d));
//        //List<MetricRPSperCPUandPodandViolation> filterOutliersFromMetrics = filterOutliers(preprocessedMetrics);
//        if(metrics.size() > 0){
//        List<MetricRPSperCPUandPodandViolation> preprocessedMetrics = filterOutliers(metrics);
//
//        System.out.println("returnCPUThreshold: Trying to find threshold");
//        SLANow = sla.getValue();
//        if (!sufficientNumberOfEvents(preprocessedMetrics)) {
//            System.out.println("returnCPUThreshold: Not enough metrics are  collected");
//            last_cpu = getCurrentHPACPUThreshold();
//            System.out.println("Last cpu was " + last_cpu);
//            if(( (Math.floor(SLANow) - MetricsService.TARGET_SLA)< 0.5) && n<= 5) {
//                n++;
//                last_cpu = sla.getValue() < 80.0 ? 100.0/(Math.ceil(100.0/last_cpu+1.0)) : (SLANow / MetricsService.TARGET_SLA) * last_cpu;
//                System.out.println("returnCPUThreshold: Underpprovisioning or SLA not compliant with the target returning " + last_cpu + " when SLA value " + SLANow+ " at " + DateUtils.now());
//            }
//            else if (Math.floor(sla.getValue().doubleValue()) >= MetricsService.TARGET_SLA) {
//                double thresholdNow = getCurrentHPACPUThreshold();
//                n = n > 0 ? n-1 : 0;
//                System.out.println("returnCPUThreshold: Staying with current HPA"+ thresholdNow + " at " + DateUtils.now());
//                last_cpu = thresholdNow;
//            }
//            notDefaultThreshold = false;
//            System.out.println("returnCPUThreshold: Threshold is "+ last_cpu + " at " + DateUtils.now());
//            return last_cpu;
//        }
//        sufficientNumberOfEventsCollectedFlag = true;
//
//        System.out.println("returnCPUThreshold: aggregations stage");
//        //List<MetricAggregateByCPU> aggregatedMetricsByCPURange = mapCPURangetoMetrics(filterOutliersFromMetrics);
//        List<MetricAggregateByCPU> aggregatedMetricsByCPURange = mapCPURangetoMetrics(preprocessedMetrics);
//        System.out.println("smoothing stage");
//        List<MetricAggregateByCPU> smoothedValuesByCMAwithZeroAnd100 = smoothValuesByCMA(aggregatedMetricsByCPURange);
//
//        notDefaultThreshold = true;
//
//        List<MetricAggregateByCPU> smoothedValuesByCMA = new ArrayList<>(smoothedValuesByCMAwithZeroAnd100);
//        System.out.println("Removing first " +smoothedValuesByCMA.get(0).getCpuRangeFrom() + " and last " + smoothedValuesByCMA.get(smoothedValuesByCMA.size()-1).getCpuRangeFrom() + "from smoothed Values");
//        smoothedValuesByCMA.remove(0);
//        smoothedValuesByCMA.remove(smoothedValuesByCMA.size()-1);
//        System.out.println("Now first " +smoothedValuesByCMA.get(0).getCpuRangeEvents() + " and last " + smoothedValuesByCMA.get(smoothedValuesByCMA.size()-1).getCpuRangeFrom() + "from smoothed Values");
//
//        if (smoothedValuesByCMA.size() > 0) {
//            if (thereAreSLACompliantThreshold(smoothedValuesByCMA)) {
//                List<MetricAggregateByCPU> rangeOfCPUThresholds = suggestCPUThresholdsWhereSlaMet(smoothedValuesByCMA);
//                if (rangeOfCPUThresholds.size() > 0) {
//                    if (!expediteThresholdUpdate) {
//                        // storedMetricsSizeForThresholdEvaluation = 0;
//                        return last_cpu = findCPUThresholdWhenSlaOK(suggestCPUThresholdsWhereSlaMet(smoothedValuesByCMA));
//                    } else {
//                       // storedMetricsSizeForThresholdEvaluation = 0;
//                        return last_cpu = findCPUThresholdLowerThanCurrent(suggestCPUThresholdsWhereSlaMet(smoothedValuesByCMA));
//                    }
//                }
//            }
//        }
//        System.out.println("No SLA  compliant thresholds found");
//    }
//        if (Math.floor(sla.getValue().doubleValue()) >= MetricsService.TARGET_SLA) {
//            double thresholdNow = getCurrentHPACPUThreshold();
//            System.out.println("Staying with current HPA"+ thresholdNow + " at " + DateUtils.now());
//            return last_cpu = thresholdNow;
//        }
//        System.out.println("Staying with downgraded value of " + 100.0/(Math.ceil(100.0/last_cpu+1.0)) + "instead of "+ last_cpu +" at " + DateUtils.now());
//        return last_cpu = 100.0/(Math.ceil(100.0/last_cpu+1.0));
//
//    }
//
//    public static boolean sufficientNumberOfEvents (List<MetricRPSperCPUandPodandViolation> metrics) {
//        System.out.println("Metrics size for threshold update is " + metrics.size() + "while required nuber is " + (double)sufficientEventNumber );
//        return metrics.size() >= (double)sufficientEventNumber ? true : false;
//    }
//    // trying new approach 2023-07-26
//
//
//    public static List<Metric> replacePercentileZeroWithValue(List<Metric> metrics, double value) {
//        List<Metric> nMetrics = metrics.stream().map( m -> {
//            if(m.getPercentileResponse()==0.0 && m.getThroughput() == 0.0 ) {
//                m.setPercentileResponse(value);
//            }
//            return m;
//        })
//                .collect(Collectors.toList());
//        System.out.println("Zeros are preprocessed.");
//        return nMetrics;
//    }
//
//
//
//    public static List<MetricRPSperCPUandPodandViolation> calculateRPSperPodPerCPUandViolation(List<Metric> metrics) {
//        List<MetricRPSperCPUandPodandViolation> metricWithRPSperPodperCPUandViolation = new ArrayList<MetricRPSperCPUandPodandViolation>();
//        System.out.println("Metrics size: " + metrics.size());
//        for (Metric m : metrics) {
//            double RPSperPod = m.getThroughput() / (double) m.getPodCount();
//            double RPSperCPU = RPSperPod / m.getAvgCPU();
//           // System.out.println("RPS per CPU : " + RPSperCPU);
//            double violation = m.getPercentileResponse() > RESPONSE_TIME_THRESHOLD ? 1.0 : 0.0;
//            metricWithRPSperPodperCPUandViolation.add(new MetricRPSperCPUandPodandViolation(m, RPSperPod, RPSperCPU, violation));
//        }
//        return metricWithRPSperPodperCPUandViolation;
//    }
//
//    public static void sortRpsPerCPU(List<MetricRPSperCPUandPodandViolation> list) {
//
//        list.sort((o1, o2) -> o1.getRpsPerCPU().compareTo(
//                o2.getRpsPerCPU()));
//    }
//
//  /*  public static void sortRpsPerPod(List<MetricRPSperCPUandPodandViolation> list) {
//
//        list.sort((o1, o2) -> o1.getRpsPerPod().compareTo(
//                o2.getRpsPerPod()));
//    }
//    */
//
//    public static MetricRPSperCPUandPodandViolation percentilePerMetric(List<MetricRPSperCPUandPodandViolation> metrics, double percentile) {
//        int index = (int) Math.ceil((percentile / (double) 100.0) * (double) metrics.size());
//        //System.out.println("percentilePerMetric: Index for " + percentile + " percentile when metrics size is " +  metrics.size()+ "is "+ Math.ceil((percentile / (double) 100.0) * (double) metrics.size()));
//        return  metrics.get(index - 1);
//    }
//
//    public static MetricAggregateByCPU percentilePerCPURangeMetric(List<MetricAggregateByCPU> metrics, double percentile) {
//        int index = (int) Math.ceil((percentile / (double) 100.0) * (double) metrics.size());
//        System.out.println("percentilePerCPU: Index for " + percentile + "percentile  when metrics size is " +  metrics.size()+ "is "+ Math.ceil((percentile / (double) 100.0) * (double) metrics.size()));
//        return metrics.get(index - 1);
//    }
//
//    private static Tuple3<Double, Double, Double> calculateIQ(List<MetricRPSperCPUandPodandViolation> metrics, Function<MetricRPSperCPUandPodandViolation, Double> field) {
//        metrics.sort((o1, o2) -> field.apply(o1).compareTo(field.apply(o2)));
//
//        double q1 = field.apply(percentilePerMetric(metrics, 25));
//        double q3 = field.apply(percentilePerMetric(metrics, 75));
//        double IQR = q3 - q1;
//        System.out.println("IQR :" + IQR + " q1 " + q1 + " q3 " + q3);
//        return new Tuple3<>(q1, q3, IQR);
//    }
//
//    private static double calculatePercentile(List<MetricRPSperCPUandPodandViolation> metrics, Function<MetricRPSperCPUandPodandViolation, Double> field, double percentile) {
//        metrics.sort((o1, o2) -> field.apply(o1).compareTo(field.apply(o2)));
//        double p = field.apply(percentilePerMetric(metrics, percentile));
//        return p;
//    }
//
//
//    static class Tuple3<T1, T2, T3> {
//        public final T1 field1;
//        public final T2 field2;
//        public final T3 field3;
//
//        public Tuple3(T1 field1, T2 field2, T3 field3) {
//            this.field1 = field1;
//            this.field2 = field2;
//            this.field3 = field3;
//        }
//    }
//
//    private static Tuple3<Double, Double, Double> calculateIQRrpsPerCPU(List<MetricRPSperCPUandPodandViolation> metrics) {
//        return calculateIQ(metrics, o -> o.getRpsPerCPU());
////        sortRpsPerCPU(metrics);
////        double q1 = percentilePerMetric(metrics,25).getRpsPerCPU();
////        double q3 = percentilePerMetric(metrics,75).getRpsPerCPU();
////        double IQR = q3-q1;
////        return IQR;
//    }
//
//    private static Tuple3<Double, Double, Double> calculateIQRrpsPerPod(List<MetricRPSperCPUandPodandViolation> metrics) {
//
//        return calculateIQ(metrics, o -> o.getRpsPerPod());
////        sortRpsPerPod(metrics);
////        double q1 = percentilePerMetric(metrics,25).getRpsPerPod();
////        double q3 = percentilePerMetric(metrics,75).getRpsPerPod();
////        double IQR = q3-q1;
////        return IQR;
//    }
//
//    private static List<MetricRPSperCPUandPodandViolation> filterOutliers(List<MetricRPSperCPUandPodandViolation> metrics) {
//        System.out.println("Filtering outliers");
//        Tuple3<Double, Double, Double> IQRrpsPerCPU = calculateIQRrpsPerCPU(metrics);
//        Tuple3<Double, Double, Double> IQRrpsPerPod = calculateIQRrpsPerPod(metrics);
//
//        //df = df[~((df["RPSperPod"] < (Q1 - 1.5 * IQR)) |(df["RPSperPod"] > (Q3 + 1.5 * IQR)))]
//        double podQ1IQR = IQRrpsPerPod.field1 - 1.5d * IQRrpsPerPod.field3;
//        double podQ3IQR = IQRrpsPerPod.field2 + 1.5d * IQRrpsPerPod.field3;
//        double cpuQ1IQR = IQRrpsPerCPU.field1 - 1.5d * IQRrpsPerCPU.field3;
//        double cpuQ3IQR = IQRrpsPerCPU.field2 + 1.5d * IQRrpsPerCPU.field3;
//
//
//
//        List<MetricRPSperCPUandPodandViolation> metricsWithoutRPSOutliers = metrics.stream()
//                .filter(r -> r.getRpsPerPod() >= podQ1IQR && r.getRpsPerPod() <= podQ3IQR)
//                .filter(r -> r.getRpsPerCPU() >= cpuQ1IQR && r.getRpsPerCPU() <= cpuQ3IQR)
//                .collect(Collectors.toList());
//
//        System.out.println("Pod: Q3IQR " + podQ3IQR + " Q1IQR " + podQ1IQR +" CPU: cpuQ3IQR " + cpuQ3IQR + " cpuQ1IQR " + cpuQ1IQR + " calulated from " + metricsWithoutRPSOutliers.size() + " out of " + metrics.size());
//
//        return metricsWithoutRPSOutliers;
//    }
//
//
//    private static List<MetricAggregateByCPU> aggregateMetricsByCPURange(List<MetricRPSperCPUandPodandViolation> metrics, double cpu_start, double cpu_finish, double cpu_step) {
//        double cpu_end = cpu_start + cpu_step > 100d ? 100d : cpu_start + cpu_step ;
//        List<MetricAggregateByCPU> cpuRangeAggregatedMetrics = new ArrayList<>();
//        while (cpu_end <= cpu_finish) {
//            double finalCpu_start = cpu_start;
//            double finalCpu_end = cpu_end;
//            List<MetricRPSperCPUandPodandViolation> cpuRangeMetrics = metrics.stream()
//                    .filter(m -> m.getMetric().getAvgCPU() >= finalCpu_start && m.getMetric().getAvgCPU() < finalCpu_end)
//                    .collect(Collectors.toList());
//            double events =  cpuRangeMetrics.size();
//            double violations = events != 0 ? (double) cpuRangeMetrics.stream()
//                    .mapToInt(o -> o.getViolation().intValue()).sum() : -1d;
//            double avgRps = events != 0 ? cpuRangeMetrics.stream()
//                    .mapToDouble(o -> o.getRpsPerCPU()).sum() / events : -1d;
//            double sla = events != 0 ? (100d - violations * 100 / events) : -1d;
//            double percentileLow = events != 0 ? calculatePercentile(cpuRangeMetrics, o -> o.getViolation(), 100d - MetricsService.TARGET_SLA) : -1d;
//            //Does it sort correctly? Need to sort by CPU and Get second percentiles. Try sort by CPU
//            double percentileHigh = events != 0 ? calculatePercentile(cpuRangeMetrics, o -> o.getViolation(), MetricsService.TARGET_SLA) : -1d;
//            cpuRangeAggregatedMetrics.add(new MetricAggregateByCPU(cpu_start, cpu_end, violations, events, avgRps, sla, percentileLow, percentileHigh));
//            //System.out.println("CPU start: "+ cpu_start +"CPU end : "+ cpu_end + "Events size " + events + "Violations: " + violations + "SLA: " + sla);
//            cpu_start = cpu_end;
//            cpu_end += cpu_step;
//            // PODUMAT cto delat esli range ne imeet eventos => event = 0;
//            //System.out.println("Final CPU end " + finalCpu_end);
//        }
//        return cpuRangeAggregatedMetrics;
//    }
//
//    private static List<MetricAggregateByCPU> mapCPURangetoMetrics(List<MetricRPSperCPUandPodandViolation> metrics) {
//        // List<MetricAggregateByCPU> metricsPer1PercentRange = aggregateMetricsByCPURange(metrics,0d,1d,0.01d);
//        double collectedMetricsSize = (double) metrics.size();
//        List<MetricAggregateByCPU> metricsWithAtLeast1PercentOfEventsPerCPURange = new ArrayList<>();
//        double start = 1d;
//        double finish = 100d;
//        double end = 0d;
//        double step = 1d;
//        metricsWithAtLeast1PercentOfEventsPerCPURange.add(new MetricAggregateByCPU(0.0, 0.0, 0d, 100000d, 0d, 100, 0d, 0d));
//        metricsWithAtLeast1PercentOfEventsPerCPURange.add(new MetricAggregateByCPU(100.0, 100.0, 100000d, 100000d, 0d, 0, 1, 1));
//
//
//
//        while (end < finish) {
//            double intermediate_step = step;
//            end = start + intermediate_step > 100d ? 100d : start + intermediate_step;
//
//            while(aggregateMetricsByCPURange(metrics, start, end, intermediate_step).size() == 0 ) {
//                intermediate_step += step;
//                end = start + intermediate_step > 100d ? 100d : start + intermediate_step;
//                //System.out.println(" step updated 1");
//            }
//            //&& intermediate_step <= 3 * step
//            //System.out.println("Size: " + Math.ceil(collectedMetricsSize / 100d));
//            while ((aggregateMetricsByCPURange(metrics, start, end, intermediate_step).get(0).getCpuRangeEvents() < (Math.ceil(collectedMetricsSize / 100d) < 5 ? 5 :  Math.ceil(collectedMetricsSize / 100d) ) ) && intermediate_step <= 3 * step ) {
//                intermediate_step += step;
//                end = start + intermediate_step > 100d ? 100d : start + intermediate_step;
//                //System.out.println(" step updated 2 to end " + end +" and events number is " + aggregateMetricsByCPURange(metrics, start, end, intermediate_step).get(0).getCpuRangeEvents()  );
//            }
//            System.out.println(" checking range Start: " +start + " End: " +end + "length, which is :" + aggregateMetricsByCPURange(metrics, start, end, intermediate_step).get(0).getCpuRangeEvents());
//            metricsWithAtLeast1PercentOfEventsPerCPURange.add(aggregateMetricsByCPURange(metrics, start, end, intermediate_step).get(0));
//            start = end;
//        }
//        System.out.println("returned metricsWithAtLeast1PercentOfEventsPerCPURange");
//        return metricsWithAtLeast1PercentOfEventsPerCPURange;
//    }
//
//
//
//        private static List<MetricAggregateByCPU> smoothValuesByCMA (List<MetricAggregateByCPU> metrics){
//        //Centric Moving Average
//         sortCPURange(metrics);
//         List<MetricAggregateByCPU> smoothedMetrics = new ArrayList<>();
//         int length = metrics.size();
//         if (length > 5 ) {
//             double SLA_before = 100d;
//             double SLA_after = 100d;
//             double SLA_now = 100d;
//             double smoothedSLA = 100d;
//             double lastSLAnotD = 100d;
//             double percentileHigh = 1d;
//             double percentileLow = 1d;
//             int k = 0;
//
//             while(metrics.get(length-k-2).getCpuRangeEvents()<2){
//                 metrics.get(length-k-2).setCpuRangeSLA(0.0);
//                 metrics.get(length-k-2).setCpuRangePercentileHigh(1.0);
//                 metrics.get(length-k-2).setCpuRangePercentileLow(1.0);
//                 k++;
//             }
//
//             for (int i = 0; i < length-k-1; i++) {
//                 smoothedSLA = (SLA_now+SLA_after+SLA_before)/(double) 3;
//                 lastSLAnotD = SLA_before;
//                 SLA_before = metrics.get(i).getCpuRangeSLA() > -1d ? metrics.get(i).getCpuRangeSLA() : lastSLAnotD;
//                 SLA_now = metrics.get(i+1).getCpuRangeSLA() > -1d ? metrics.get(i+1).getCpuRangeSLA() : lastSLAnotD;
//                 SLA_after = (i+2) == metrics.size() ? (metrics.get(i+1).getCpuRangeSLA()  > -1d ? metrics.get(i+1).getCpuRangeSLA() : lastSLAnotD) : (metrics.get(i+2).getCpuRangeSLA()  > -1d ? metrics.get(i+2).getCpuRangeSLA() : lastSLAnotD);
//                 percentileHigh = metrics.get(i+1).getCpuRangePercentileHigh() == -1d ? SLA_before >= MetricsService.TARGET_SLA ? 0d : 1d : metrics.get(i+1).getCpuRangePercentileHigh();
//                 percentileLow = metrics.get(i+1).getCpuRangePercentileLow() == -1d ? SLA_before >= MetricsService.TARGET_SLA ? 0d : 1d : metrics.get(i+1).getCpuRangePercentileLow() ;
//                 metrics.get(i+1).setCpuRangeSLA(smoothedSLA);
//                 metrics.get(i+1).setCpuRangePercentileHigh(percentileHigh);
//                 metrics.get(i+1).setCpuRangePercentileLow(percentileLow);
//                 System.out.println("CPU to SLA. CPU from: "+ metrics.get(i+1).getCpuRangeFrom() + "CPU to: "+ metrics.get(i+1).getCpuRangeTo() + " SLA: " + metrics.get(i+1).getCpuRangeSLA() + " PerHigh: " + metrics.get(i+1).getCpuRangePercentileHigh() + " PerLow: " + metrics.get(i+1).getCpuRangePercentileLow());
//             }
//             return metrics;
//         }
//         return metrics;
//        }
//
//        private static  boolean thereAreSLACompliantThreshold(List<MetricAggregateByCPU> metrics) {
//            sortCPURange(metrics);
//
//
//            if (metrics.stream().anyMatch(m -> Math.floor(m.getCpuRangeSLA()) >= MetricsService.TARGET_SLA )){
//                System.out.println("There are SLA compliant metrics");
//                return true;
//            }
//            return false;
//        }
//        private static List<MetricAggregateByCPU> suggestCPUThresholdsWhereSlaMet(List<MetricAggregateByCPU> metrics) {
//            sortCPURange(metrics);
//            List<MetricAggregateByCPU> sorted = new ArrayList<>(metrics);
//
//        /*     double minThreshold = metrics.stream().filter(t -> t.getCpuRangePercentileHigh() == 1.0).findFirst()
//                    .orElse(metrics.get(0)).getCpuRangeFrom();
//            double maxThreshold = metrics.stream().filter(t -> t.getCpuRangePercentileLow() == 1.0).findFirst()
//                    .orElse(metrics.get(metrics.size() - 1)).getCpuRangeFrom();
//            */
//            List<MetricAggregateByCPU> cpuRangeWhereSLAMet = metrics.stream()
//                        .filter(r -> Math.floor(r.getCpuRangeSLA()) >= MetricsService.TARGET_SLA)
//                        .collect(Collectors.toList());
//
//            if (metrics.size() > 2 ) {
//            List<MetricAggregateByCPU> tempMetrics = new ArrayList<>(metrics);
//            List<MetricAggregateByCPU> tempMetrics2 = new ArrayList<>(metrics);
//            double minThreshold = tempMetrics.stream().filter(t -> t.getCpuRangePercentileHigh() == 1.0 && t.getCpuRangePercentileLow() == 0.0 ).findFirst()
//                    .orElse(tempMetrics.get(0)).getCpuRangeFrom();
//            double maxThreshold = tempMetrics2.stream().filter(t -> t.getCpuRangePercentileLow() == 1.0  && t.getCpuRangePercentileHigh() == 1.0 ).findFirst()
//                    .orElse(tempMetrics2.get(tempMetrics2.size() - 1)).getCpuRangeFrom();
//            //System.out.println("thereAreSLACompliantThreshold: Metrics size: "+ metrics.size() + " Max " + maxThreshold +" Min :  " + minThreshold );
//
//                    //.findFirst()
//                    //.map(m -> m.getCpuRangeFrom()).orElse(0.0);
//
//            System.out.println("suggestCPUThresholdsWhereSlaMe: Metrics size: "+ metrics.size() +  " and metrics met in  " + cpuRangeWhereSLAMet.size() + " Max " + maxThreshold +" Min :  " + minThreshold );
//            //System.out.println(metrics.size() + "Max: " + maxThreshold + " Min:  " + minThreshold);
//
//                //&& r.getCpuRangeFrom() >= minThreshold && r.getCpuRangeTo() <= maxThreshold)
//
//                if( (maxThreshold > minThreshold)   && (cpuRangeWhereSLAMet.size() > 1)) {
//
//                     cpuRangeWhereSLAMet =  cpuRangeWhereSLAMet.stream()
//                        .filter(r ->  r.getCpuRangeTo() >= minThreshold && r.getCpuRangeTo() <= maxThreshold  )
//                        .collect(Collectors.toList());
//                }
//            }
//
//                System.out.println("cpuRangeWhereSLAMet size: " + cpuRangeWhereSLAMet.size());
//                return cpuRangeWhereSLAMet;
//    }
//
//    private static double findCPUThresholdLowerThanCurrent(List<MetricAggregateByCPU> cpuRangeWhereSLAMet) {
//        sortCPURange(cpuRangeWhereSLAMet);
//        System.out.println("Searching for lower CPU threshold ");
//        double cpu = last_cpu;
//            List<MetricAggregateByCPU> l = cpuRangeWhereSLAMet.stream()
//                    .filter(m -> m.getCpuRangeTo() < last_cpu)
//                    .collect(Collectors.toList());
//            if (l.size() > 0 ) {
//                System.out.println("Proposed CPU is " + l.get(l.size() - 1).getCpuRangeTo() + " when Max SLA:  " +  l.get(l.size() - 1).getCpuRangeSLA());
//                cpu = l.get(l.size()-1).getCpuRangeTo();
//            }
//            else {
//                System.out.println("No candidates are found while SLA dropping under SLA violation state");
//                cpu = 100.0/(Math.ceil(100.0/last_cpu+1.0));
//            }
//
//        return cpu;
//    }
//
//
//
//    private static double findCPUThresholdWhenSlaOK(List<MetricAggregateByCPU> cpuRangeWhereSLAMet) {
//
//        double maxSLA = 100.00;
//        double diffSLA = MetricsService.TARGET_SLA;
//        double startSLA = maxSLA - 1.0;
//
//
//        //while (startSLA  <= diffSLA) {
//            System.out.println("Searching max threshold where SLA is met:  " + maxSLA);
//            List<MetricAggregateByCPU> l = cpuRangeWhereSLAMet;
//
//            //List<MetricAggregateByCPU> l = cpuRangeWhereSLAMet.stream()
//            //        .filter(r -> Math.floor(r.getCpuRangeSLA()) >= startSLA && r.getCpuRangeSLA() < maxSLA)
//            //        .collect(Collectors.toList());
//                sortCPURange(l);
//                System.out.println("Proposed CPU is " + l.get(l.size() - 1).getCpuRangeTo() + " when Max SLA:  " + l.get(l.size() - 1).getCpuRangeSLA());
//                return l.get(l.size() - 1).getCpuRangeTo();
//            //startSLA--;
//            //maxSLA --;
//       // }
//
//    }
//
//
//    public static double calculateAvgRPSperCPU(List<Metric> metrics) {
//        double RPSperCPUtotal = 0.0;
//        System.out.println("Raw metrics size: " + metrics.size());
//
//        for (Metric m : metrics) {
//            double RPSperPod = m.getThroughput() / (double) m.getPodCount();
//            RPSperCPUtotal = RPSperCPUtotal + RPSperPod / m.getAvgCPU();
//        }
//        System.out.println(RPSperCPUtotal / (double) metrics.size());
//        return RPSperCPUtotal / (double) metrics.size();
//    }
//
//    public static List<Metric> filterMetricsBelowPercentile(List<Metric> metrics, double percentile) {
//        return metrics.stream()
//                .filter(r -> r.getPercentileResponse() <= percentile && r.getPodCount() >1)
//                .collect(Collectors.toList());
//    }
//
//    public static List<MetricSLA> mapMetricsToSLA(List<Metric> metrics, Double rtThreshold) {
//
//        List<MetricSLA> SLAperMetric = new ArrayList<MetricSLA>();
//        double tempSLA = 0.0;
//        double index = 0;
//        for (Metric m : metrics) {
//            tempSLA = m.getAVGResponse() >= rtThreshold ? tempSLA : tempSLA + 1.0;
//            SLAperMetric.add(new MetricSLA(m, (tempSLA / (index + 1.0)) * 100.0));
//            index = index + 1.0;
//            System.out.println("CPU: " + m.getAvgCPU() + " SLA: " + (tempSLA / (index)));
//            System.out.println(SLAperMetric.get((int) (index - 1.0)).getSLA());
//        }
//        return SLAperMetric;
//    }
//
//    static boolean containsSLA(List<MetricSLA> metrics, Double SLA) {
//        return metrics.stream().anyMatch(m -> Math.floor(m.getSLA())*100.0 == SLA);
//    }
//
//    //provide result of mapMetricsToSLA as metrics
//    public static double findCPUThreshold(List<MetricSLA> metrics, Double SLA, double avgRPSperCPU) {
//        double CPUThreshold = 0.0;
//        double rps_max = 0.0;
//
//        MetricSLA max_SLA = metrics.stream().max(Comparator.comparing(m -> m.getSLA())).get();
//
//        //CPU values where SLA is achieved
//
//        System.out.println("Max SLA: " + Math.floor(max_SLA.getSLA()));
//
//        if (Math.floor(max_SLA.getSLA()*100.0) >= SLA){
//            if (containsSLA(metrics, SLA)){
//            List<MetricSLA> SLAMetrics = metrics.stream()
//                    .filter(r -> Math.floor(r.getSLA())*100.0 == SLA)
//                    .collect(Collectors.toList());
//            for (MetricSLA m : SLAMetrics)
//                CPUThreshold = m.getMetric().getAvgCPU().doubleValue() > CPUThreshold ? m.getMetric().getAvgCPU().doubleValue() : CPUThreshold;
//            System.out.println("Normal Threshold: " + CPUThreshold);
//            return CPUThreshold;
//        }
//            else{
//            List<MetricSLA> SLAMetrics = metrics.stream()
//                    .filter(r -> Math.floor(r.getSLA())*100.0 > SLA)
//                    .collect(Collectors.toList());
//            for (MetricSLA m : SLAMetrics)
//                rps_max = m.getMetric().getThroughput().doubleValue() / m.getMetric().getPodCount().doubleValue() > rps_max ? m.getMetric().getThroughput().doubleValue() / m.getMetric().getPodCount().doubleValue() : rps_max;
//            System.out.println("rps max : " + rps_max + "avg " + avgRPSperCPU);
//            return rps_max / avgRPSperCPU;
//        }
//    }
//        else {
//            CPUThreshold = Math.ceil(max_SLA.getMetric().getAvgCPU());
//            System.out.println("UnderProvisioning Threshold: "+ CPUThreshold);
//
//        }
//        return CPUThreshold;
//    }
//
//
//}
