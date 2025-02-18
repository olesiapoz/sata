package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.*;
import com.autoscaling.autoscaler.MetricsService;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.micrometer.core.instrument.util.TimeUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.Math;

@Service
public class PrometheusClientBench {

    int BT_MAX = Velocity.MAX_THROUGHPUT_VALUE;
    int  Monitoring_Period_Seconds = 3;
    Long eval_period_seconds = MetricsService.OFFSET_SECONDS;
    double RESPONSE_TIME = MetricsService.RESPONSE_TIME;

    private final ExecutorService txReader = Executors.newSingleThreadExecutor();
    private final ExecutorService slaReader = Executors.newSingleThreadExecutor();
    private final ExecutorService avgCpuReader = Executors.newSingleThreadExecutor();
    private final ExecutorService totalCpuReader = Executors.newSingleThreadExecutor();
    private final ExecutorService podCountReader = Executors.newSingleThreadExecutor();
    private final ExecutorService podUpTimeReader = Executors.newSingleThreadExecutor();

    //slaAdoptedPodNumber
    //jMeterBTCount
    //jMeterThroughput
    //jMeterSla
    //btCount

    
    @Value("${prometheus.url}")
    private String prometheusURL;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    ValuesService valuesService;

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private FileWriterBench FileWriter;

    private Timestamp startDate = DateUtils.now();
    private double cpu_before;
    private Timestamp sufficientEventsCollectedTime = startDate;
    private boolean sufficientEventsCollectedTimeDefaults = true;


    ParameterizedTypeReference<HashMap<String, Object>> responseType =
            new ParameterizedTypeReference<>() {
            };

    @PostConstruct
    public void init() {
        txReader.execute(() -> {
            while (true) {
                try {

                        AvgCPU avgCPU = readAvgCPULoad();
                        BTCount btCount = readMetrics();
                        BTTotal btTotal = readBTTotal();
                        //BTTotal btDelta = readBtDelta();
                        PodCount podCount = readPodCount();
                        SLA sla = readSLA();
                        PodUpTime podUpTime = readPodUpTime();
                        AvgResponse avgResponse = readAvgResponseTime();
                        AvgResponse percentileResponse = readPercentileResponseTime();
                        TouchstonePodCount touchStonePods = calculateTouchstonePodCount(btCount);
                        Throughput throughput = readThroughput();
                        AvgCPU suggestedThreshold = readThreshold();
                        SLA slaAfterLearningPeriod = readSLAonceEventsCollected();

                        valuesService.publishLastKnownCPU(avgCPU);
                        valuesService.publishPodCount(podCount);
                        valuesService.publishAvgResponse(avgResponse);
                        valuesService.publishPercentileResponse(percentileResponse);
                        valuesService.publishThroughputBench(throughput);
                        valuesService.publishLastThreshold(suggestedThreshold);

                        if(valuesService.getSufficientNumberOfEventsCollectedFlag())
                        {
                            if (sufficientEventsCollectedTimeDefaults == valuesService.getSufficientNumberOfEventsCollectedFlag() ){
                                sufficientEventsCollectedTime = DateUtils.now();
                                sufficientEventsCollectedTimeDefaults  = false;
                            }
                        }


                        metricsService.processSLA(sla);
                        //metricsService.process(btCount);

                        FileWriter.writeAll(avgCPU, btCount, podCount, sla, podUpTime, btTotal,  avgResponse, percentileResponse, touchStonePods, throughput, suggestedThreshold,slaAfterLearningPeriod);

                        Thread.sleep(Monitoring_Period_Seconds * 1000);

                } catch (Exception e) {
                    System.out.println("Reading bench metrics" + e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(Monitoring_Period_Seconds * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private SLA readSLA() {
        Timestamp now = DateUtils.now();
        Long difference = now.getTime() - startDate.getTime();
        Long minutes = (difference / 1000 / 60) + 1;

        RequestEntity<Void> request = buildRequest("sum(rate(request_duration_bucket{le=\""+RESPONSE_TIME+"\", app=\"demo1\"}["+minutes+"m]))/ignoring(le)sum(rate(request_duration_count{app=\"demo1\"}["+minutes+"m]))*100");

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        SLA sla = parseSLA(result.getBody());
        //System.out.println(sla);

        return sla;
    }

    private SLA readSLAonceEventsCollected() {
        Timestamp now = DateUtils.now();
        Long difference = now.getTime() - sufficientEventsCollectedTime.getTime();
        Long minutes = (difference / 1000 / 60) + 1;

        RequestEntity<Void> request = buildRequest("sum(rate(request_duration_bucket{le=\""+RESPONSE_TIME+"\", app=\"demo1\"}["+minutes+"m]))/ignoring(le)sum(rate(request_duration_count{app=\"demo1\"}["+minutes+"m]))*100");

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        SLA sla = parseSLA(result.getBody());
        //System.out.println(sla);

        return sla;
    }
    //sum(rate(request_duration_bucket{le="1.0", app="demo1"}[5s]))

    private AvgResponse readAvgResponseTime() {
        RequestEntity<Void> request = buildRequest("sum(rate(request_duration_sum{app=\"demo1\"}["+eval_period_seconds+"s]))/sum(rate(request_duration_count{app=\"demo1\"}["+eval_period_seconds+"s]))*1000");

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        AvgResponse avgResponse = parseAvgResponse(result.getBody());

        return avgResponse;
    }

//"sum(rate(request_duration_bucket{le=\"1.0\", app=\"demo1\"}["+eval_period_seconds+"s]))*1000"
    private AvgResponse readPercentileResponseTime() {
        RequestEntity<Void> request = buildRequest("histogram_quantile(0.97, sum(rate(request_duration_bucket{app=\"demo1\"}[45s])) by (le))*1000");

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        AvgResponse avgResponse = parseAvgResponse(result.getBody());

        return avgResponse;
    }

    private BTCount readMetrics()  {
        RequestEntity<Void> request = buildRequest("sum by (demo1) (rate(bt_count_total{app=\"demo1\"}["+eval_period_seconds+"s]))");
        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        BTCount btCount = parseBTCount(result.getBody());
        //System.out.println(btCount);
        return btCount;
    }

    private BTTotal readBTTotal()  {
        RequestEntity<Void> request = buildRequest("sum by (demo1) (bt_count_total{app=\"demo1\"})");
        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        BTTotal btTotal = parseBTTotal(result.getBody());
        //System.out.println( btTotal );
        return  btTotal;
    }

    // private BTTotal readBtDelta()  { 
    //     Timestamp now = DateUtils.now();
    //     Long difference = now.getTime() - startDate.getTime();
    //     Long minutes = (difference / 1000 / 60) + 1;

    //     RequestEntity<Void> request = buildRequest("sum by (app) (increase(bt_count_total["+minutes+"m]))");
    //     ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
    //     BTTotal btDelta = parseBTDelta(result.getBody());
    //     System.out.println(btDelta);
    //     return btDelta;
    // }

    private AvgCPU readAvgCPULoad()  {
        //RequestEntity<Void> request = buildRequest("avg(rate(container_cpu_usage_seconds_total{container=\"demo1\"}[45s]) * on (pod) group_left kube_pod_container_status_ready{container=\"demo1\"} > 0)");
        RequestEntity<Void> request = buildRequest("avg(rate(container_cpu_usage_seconds_total{container=\"demo1\"}[60s]) * on (pod) group_left kube_pod_container_status_ready{container=\"demo1\"} > 0) /  avg(kube_pod_container_resource_limits{resource=\"cpu\",container=\"demo1\"})");

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        AvgCPU avgCpu = parseAvgCPU(result.getBody());
        //System.out.println(avgCpu);
        //avg(rate(container_cpu_usage_seconds_total{container="demo1"}[60s]) * on (pod) group_left kube_pod_container_status_ready{container="demo1"} > 0) /  avg(kube_pod_container_resource_limits{resource="cpu",container="demo1"})

        return avgCpu;

    }

    private AvgCPU readThreshold() {
         double avgCPUThreshold = cpu_before;
         try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            
            avgCPUThreshold = client.autoscaling().v2().horizontalPodAutoscalers().inNamespace("default").withName("demo1-hpa").get().getSpec().getMetrics().get(0).getResource().getTarget().getAverageUtilization().doubleValue();
            
        } catch (Exception e) {
            System.out.println("Current HPA cannot be collected due to error listed below");
            System.out.println(e);
        }
        if(avgCPUThreshold == cpu_before){
            //System.out.println("Current HPA threshold is " + avgCPUThreshold);
            return new AvgCPU((DateUtils.now() ), Double.valueOf(avgCPUThreshold));
        } 
            else {
                AvgCPU tr =  new AvgCPU((DateUtils.now()), Double.valueOf(cpu_before));
                cpu_before = avgCPUThreshold;
                return tr;
            } 



    }

     private Throughput readThroughput()  {
        RequestEntity<Void> request = buildRequest("sum by (demo1) (rate(bt_count_total{app=\"demo1\"}["+eval_period_seconds+"s]))"); 

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        Throughput throughput = parseThroughput(result.getBody());
        //System.out.println(throughput);

        return throughput;

    }

    private RequestEntity<Void> buildRequest(String url) {
        try {
            StringBuilder builder = new StringBuilder(prometheusURL+"api/v1/query");
            builder.append("?query=");
            builder.append(URLEncoder.encode(url,StandardCharsets.UTF_8.toString()));
            URI uri = URI.create(builder.toString());

            return RequestEntity.get(uri).accept(MediaType.APPLICATION_JSON).build();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private PodCount readPodCount()  {
        RequestEntity<Void> request = buildRequest("count(kube_pod_info{pod=~\"demo1-.*\"} * on (pod) group_left kube_pod_container_status_ready{container=\"demo1\"} > 0)");
        //RequestEntity<Void> request = buildRequest("count(kube_pod_info{pod=~\"demo1-.*\"} * on (pod) group_left kube_pod_container_status_running{container=\"demo1\"} > 0)");

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        PodCount podCount = parsePodCount(result.getBody());
        //System.out.println(podCount);
        return podCount;
    }
    
    private TouchstonePodCount calculateTouchstonePodCount(BTCount btCount)  {
        int pods = (int)Math.ceil(btCount.getValue()/BT_MAX);
        //System.out.println(pods);
        return new TouchstonePodCount(pods);
    }

    private PodUpTime readPodUpTime()  {
        Timestamp now = DateUtils.now();
        Long difference = now.getTime() - startDate.getTime();
        Long minutes = (difference / 1000 / 60) + 1;

        RequestEntity<Void> request = buildRequest("sum(sum_over_time( kube_pod_info{pod=~\"demo1-.*\"}["+minutes+"m]) * 60)");

        ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
        PodUpTime podUpTime = parsePodUpTime(result.getBody());
        //System.out.println(podUpTime);
        return podUpTime;
    }


    private SLA parseSLA(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new SLA(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
    }

    private AvgResponse parseAvgResponse(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new AvgResponse(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
    }

    private BTCount parseBTCount(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new BTCount(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
    }

    private BTTotal parseBTDelta(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new BTTotal(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
    }

    private BTTotal parseBTTotal(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new BTTotal(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
    }
    private AvgCPU parseAvgCPU(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new AvgCPU(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
    }

    private Throughput parseThroughput(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new Throughput(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
    }

    private PodCount parsePodCount(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new PodCount(new Timestamp(result.getLong(0) * 1000), result.getInt(1));
    }

    private PodUpTime parsePodUpTime(HashMap<String, Object> responseBody) {
        JSONArray result = parsePROMQLResponse(responseBody);
        return new PodUpTime(new Timestamp(result.getLong(0) * 1000), result.getInt(1));
    }

    private JSONArray parsePROMQLResponse(HashMap<String, Object> responseBody) {

        try{
        JSONObject json = new JSONObject(responseBody);
        
        //System.out.println(json.getJSONObject("data")
                // .getJSONArray("result")
                // .getJSONObject(0)
                // .getJSONArray("value"));

        return json.getJSONObject("data")
                .getJSONArray("result")
                .getJSONObject(0)
                .getJSONArray("value");
        }
          catch(org.json.JSONException e)  
        {  
            //System.out.println(e); 
           // System.out.println("Outpute: " + responseBody);
            //e.printStackTrace();
            
           return new JSONArray()
            .put(System.currentTimeMillis() / 1000)
            .put(0);
             
        }  
        
    }
    
}
