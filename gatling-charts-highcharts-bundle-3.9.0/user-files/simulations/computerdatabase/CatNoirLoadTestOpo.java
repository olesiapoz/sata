//package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;
import java.io.*;

/**
 * This sample is based on our official tutorials:
 * <ul>
 * <li><a href="https://gatling.io/docs/gatling/tutorials/quickstart">Gatling
 * quickstart tutorial</a>
 * <li><a href="https://gatling.io/docs/gatling/tutorials/advanced">Gatling
 * advanced tutorial</a>
 * </ul>
 */
public class CatNoirLoadTestOpo extends Simulation {

  int WARMUP_STEP_TIME = 15;
  int STEP_TIME =30;
  int WARMUP_STEP_RPS = 5;
  int HOLD_STEP_TIME = STEP_TIME - WARMUP_STEP_TIME;
  boolean RUMPUP = false;
  int MAX_RPS = 7*WARMUP_STEP_RPS;

  //.baseUrl("http://opo-demo1.northeurope.cloudapp.azure.com:30036") // 5
  //size_from=15000000&size_to=15010000&sleep_from=100&sleep_to=500

  HttpProtocolBuilder httpProtocol = http // 4
      .baseUrl("http://phd-demo1.swedencentral.cloudapp.azure.com:30036") // 5
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6
      .doNotTrackHeader("1")
      .acceptLanguageHeader("en-US,en;q=0.5")
      .acceptEncodingHeader("gzip, deflate")
      .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");

  ScenarioBuilder scn = scenario("BasicSimulation") // 7
      .exec(http("reque") // 8
          .get("/api/execute/")
          .queryParam("size_from", "1000000") //3m-Median 200ms, Max 240, 19RPS
          .queryParam("size_to",   "3000000") // 1m-Median 160ms, Max 240, 50+ RPS
          .queryParam("sleep_from", "50")
          .queryParam("sleep_to", "100")); // 9
  // .pause(5); // 10

  public CatNoirLoadTestOpo() throws Exception {
    List<Integer> records = new ArrayList<>();
    List<OpenInjectionStep> steps = new ArrayList<OpenInjectionStep>();

      for (int i = 0; i < Math.floor((MAX_RPS / WARMUP_STEP_RPS))-1; i++) {
        steps.add(rampUsersPerSec(WARMUP_STEP_RPS * i).to(WARMUP_STEP_RPS * (i + 1)).during(WARMUP_STEP_TIME));
        steps.add(constantUsersPerSec(WARMUP_STEP_RPS * (i + 1)).during(HOLD_STEP_TIME));
      }

    OpenInjectionStep[] stepsArr = new OpenInjectionStep[steps.size()];
    setUp( // 11
        scn.injectOpen(steps.toArray(stepsArr)) // 12
    ).protocols(httpProtocol); // 13
  }
}

// List<List<String>> records = new ArrayList<List<String>>();
// FileReader("wc_day78_2.csv"))) {
// String line;
// while ((line = br.readLine()) != null) {
// String[] values = line.split(COMMA_DELIMITER);
// records.add(Arrays.asList(values[1]));
// }

// Iterator<String> crunchifyIterator = records.iterator();

// while (crunchifyIterator.hasNext()) {
// System.out.println(crunchifyIterator.next());
// }

// while (crunchifyIterator.hasNext()) {
// System.out.println(crunchifyIterator.next());

// setUp(
// scn.injectOpen(
// //nothingFor(4), // 1
// //atOnceUsers(10), // 2
// //rampUsers(10).during(5), // 3

// //constantUsersPerSec(20).during(15).randomized(), // 5
// rampUsersPerSec(0).to(20).during(120), // 6
// rampUsersPerSec(20).to(60).during(120),
// rampUsersPerSec(60).to(180).during(120),
// rampUsersPerSec(180).to(240).during(120),
// rampUsersPerSec(240).to(360).during(120),
// rampUsersPerSec(360).to(480).during(120),
// //rampUsersPerSec(10).to(20).during(10).randomized(), // 7
// //stressPeakUsers(100).during(480), // 8
// //constantUsersPerSec(100).during(300), // 4
// rampUsersPerSec(100).to(80).during(120),
// rampUsersPerSec(80).to(60).during(120),
// rampUsersPerSec(60).to(40).during(120),
// rampUsersPerSec(40).to(20).during(120),
// rampUsersPerSec(20).to(0).during(120)
// ).protocols(httpProtocol)
// );
// }}
// }