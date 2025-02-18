package com.autoscaling.autoscaler;// package com.autoscaling.autoscaler;

// import com.autoscaling.autoscaler.model.*;
// import org.json.JSONArray;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.http.MediaType;
// import org.springframework.http.RequestEntity;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import javax.annotation.PostConstruct;
// import java.io.UnsupportedEncodingException;
// import java.net.URI;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.sql.Timestamp;
// import java.util.HashMap;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.lang.Math;

// @Service
// public class PrometheusClient {

//     int BT_MAX = 20;

//     private final ExecutorService txReader = Executors.newSingleThreadExecutor();
//     private final ExecutorService slaReader = Executors.newSingleThreadExecutor();
//     private final ExecutorService avgCpuReader = Executors.newSingleThreadExecutor();
//     private final ExecutorService totalCpuReader = Executors.newSingleThreadExecutor();
//     private final ExecutorService podCountReader = Executors.newSingleThreadExecutor();
//     private final ExecutorService podUpTimeReader = Executors.newSingleThreadExecutor();

//     //slaAdoptedPodNumber
//     //jMeterBTCount
//     //jMeterThroughput
//     //jMeterSla
//     //btCount

    
//     @Value("${prometheus.url}")
//     private String prometheusURL;

//     RestTemplate restTemplate = new RestTemplate();

//     @Autowired
//     ValuesService valuesService;

//     @Autowired
//     private MetricsService metricsService;

//     @Autowired
//     private FileWriter fileWriter;

//     private Timestamp startDate = DateUtils.now();


//     ParameterizedTypeReference<HashMap<String, Object>> responseType =
//             new ParameterizedTypeReference<>() {
//             };

//     @PostConstruct
//     public void init() {
//         txReader.execute(() -> {
//             while (true) {
//                 try {

//                         AvgCPU avgCPU = readAvgCPULoad();
//                         BTCount btCount = readMetrics();
//                         BTTotal btTotal = readBTTotal();
//                         //BTTotal btDelta = readBtDelta();
//                         PodCount podCount = readPodCount();
//                         SLA sla = readSLA();
//                         PodUpTime podUpTime = readPodUpTime();
//                         AvgResponse avgResponse = readAvgResponseTime();
//                         TouchstonePodCount touchStonePods = calculateTouchstonePodCount(btCount);

//                         //valuesService.publishLastKnownCPU(avgCPU);
//                         //valuesService.publishPodCount(podCount);
//                         //valuesService.publishAvgResponse(avgResponse);

//                         //metricsService.processSLA(sla);
//                         //metricsService.process(btCount);

//                         fileWriter.writeAll(avgCPU, btCount, podCount, sla, podUpTime, btTotal,  avgResponse, touchStonePods);

//                         Thread.sleep(3000);

//                 } catch (Exception e) {
//                     System.out.println("Reading metrics" + e.getMessage());
//                     e.printStackTrace();
//                 } finally {
//                     try {
//                         Thread.sleep(3000);
//                     } catch (InterruptedException e) {
//                         e.printStackTrace();
//                     }
//                 }
//             }
//         });
//     }


//     private SLA readSLA() {
//         Timestamp now = DateUtils.now();
//         Long difference = now.getTime() - startDate.getTime();
//         Long minutes = (difference / 1000 / 60) + 1;

//         RequestEntity<Void> request = buildRequest("sum(rate(request_duration_bucket{le=\"1.0\", app=\"demo1\"}["+minutes+"m]))/ignoring(le)sum(rate(request_duration_count{app=\"demo1\"}["+minutes+"m]))*100");

//         ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//         SLA sla = parseSLA(result.getBody());
//         //System.out.println(sla);

//         return sla;
//     }

//     private AvgResponse readAvgResponseTime() {
//         RequestEntity<Void> request = buildRequest("sum(rate(request_duration_sum{app=\"demo1\"}[30s]))/sum(rate(request_duration_count{app=\"demo1\"}[30s]))*1000");

//         ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//         AvgResponse avgResponse = parseAvgResponse(result.getBody());


//         return avgResponse;
//     }

//     private BTCount readMetrics()  {
//         RequestEntity<Void> request = buildRequest("sum by (demo1) (rate(bt_count_total{app=\"demo1\"}[30s]))");
//         ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//         BTCount btCount = parseBTCount(result.getBody());
//         //System.out.println(btCount);
//         return btCount;
//     }

//     private BTTotal readBTTotal()  {
//         RequestEntity<Void> request = buildRequest("sum by (demo1) (bt_count_total{app=\"demo1\"})");
//         ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//         BTTotal btTotal = parseBTTotal(result.getBody());
//         //System.out.println( btTotal );
//         return  btTotal;
//     }

//     // private BTTotal readBtDelta()  { 
//     //     Timestamp now = DateUtils.now();
//     //     Long difference = now.getTime() - startDate.getTime();
//     //     Long minutes = (difference / 1000 / 60) + 1;

//     //     RequestEntity<Void> request = buildRequest("sum by (app) (increase(bt_count_total["+minutes+"m]))");
//     //     ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//     //     BTTotal btDelta = parseBTDelta(result.getBody());
//     //     System.out.println(btDelta);
//     //     return btDelta;
//     // }

//     private AvgCPU readAvgCPULoad()  {
//         RequestEntity<Void> request = buildRequest("avg(rate(container_cpu_usage_seconds_total{container=\"demo1\"}[45s]) * on (pod) group_left kube_pod_container_status_ready{container=\"demo1\"} > 0)");

//         ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//         AvgCPU avgCpu = parseAvgCPU(result.getBody());
//         //System.out.println(avgCpu);

//         return avgCpu;

//     }

//     private RequestEntity<Void> buildRequest(String url) {
//         try {
//             StringBuilder builder = new StringBuilder(prometheusURL+"api/v1/query");
//             builder.append("?query=");
//             builder.append(URLEncoder.encode(url,StandardCharsets.UTF_8.toString()));
//             URI uri = URI.create(builder.toString());

//             return RequestEntity.get(uri).accept(MediaType.APPLICATION_JSON).build();
//         } catch (UnsupportedEncodingException e) {
//             System.out.println(e.getMessage());
//             throw new RuntimeException(e);
//         }

//     }

//     private PodCount readPodCount()  {
//         RequestEntity<Void> request = buildRequest("count(kube_pod_info{pod=~\"demo1-.*\"} * on (pod) group_left kube_pod_container_status_ready{container=\"demo1\"} > 0)");

//         ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//         PodCount podCount = parsePodCount(result.getBody());
//         //System.out.println(podCount);
//         return podCount;
//     }

    
//     private TouchstonePodCount calculateTouchstonePodCount(BTCount btCount)  {
//         int pods = (int)Math.ceil(btCount.getValue()/BT_MAX);
//         //System.out.println(pods);
//         return new TouchstonePodCount(pods);
//     }

//     private PodUpTime readPodUpTime()  {
//         Timestamp now = DateUtils.now();
//         Long difference = now.getTime() - startDate.getTime();
//         Long minutes = (difference / 1000 / 60) + 1;

//         RequestEntity<Void> request = buildRequest("sum(sum_over_time( kube_pod_info{pod=~\"demo1-.*\"}["+minutes+"m]) * 60)");

//         ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
//         PodUpTime podUpTime = parsePodUpTyime(result.getBody());
//         //System.out.println(podUpTime);
//         return podUpTime;
//     }


//     private SLA parseSLA(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new SLA(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
//     }

//     private AvgResponse parseAvgResponse(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new AvgResponse(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
//     }

//     private BTCount parseBTCount(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new BTCount(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
//     }

//     private BTTotal parseBTDelta(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new BTTotal(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
//     }

//     private BTTotal parseBTTotal(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new BTTotal(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
//     }
//     private AvgCPU parseAvgCPU(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new AvgCPU(new Timestamp(result.getLong(0) * 1000), result.getDouble(1));
//     }

//     private PodCount parsePodCount(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new PodCount(new Timestamp(result.getLong(0) * 1000), result.getInt(1));
//     }

//     private PodUpTime parsePodUpTyime(HashMap<String, Object> responseBody) {
//         JSONArray result = parsePROMQLResponse(responseBody);
//         return new PodUpTime(new Timestamp(result.getLong(0) * 1000), result.getInt(1));
//     }

//     private JSONArray parsePROMQLResponse(HashMap<String, Object> responseBody) {

//         try{
//         JSONObject json = new JSONObject(responseBody);
//         // System.out.println(json.getJSONObject("data")
//         //         .getJSONArray("result")
//         //         .getJSONObject(0)
//         //         .getJSONArray("value"));

//         return json.getJSONObject("data")
//                 .getJSONArray("result")
//                 .getJSONObject(0)
//                 .getJSONArray("value");
//         }
//           catch(org.json.JSONException e)  
//         {  
//             System.out.println(e); 
            
//            return new JSONArray()
//             .put(System.currentTimeMillis() / 1000)
//             .put(0);
             
//         }  
        
//     }

// }
