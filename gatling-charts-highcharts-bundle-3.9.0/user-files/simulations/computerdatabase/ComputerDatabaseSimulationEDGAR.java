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
public class ComputerDatabaseSimulationEDGAR extends Simulation {

  int WARMUP_STEP_TIME = 70;
  int WARMUP_STEP_RPS = 50;
  int WARMUP_STEP_RAMPUP = 100;
  boolean RUMPUP = false;

  //.baseUrl("http://opo-demo1.northeurope.cloudapp.azure.com:30036") // 5
  //.baseUrl("http://phd-demo1.swedencentral.cloudapp.azure.com:30036") // 5

  HttpProtocolBuilder httpProtocol = http // 4
      .baseUrl("http://vipo-demo1.northeurope.cloudapp.azure.com:30036") // 5
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6
      .doNotTrackHeader("1")
      .acceptLanguageHeader("en-US,en;q=0.5")
      .acceptEncodingHeader("gzip, deflate")
      .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");

  ScenarioBuilder scn = scenario("BasicSimulation") // 7
      .exec(http("reque") // 8
          .get("/api/execute/")); // 9
  // .pause(5); // 10

  public ComputerDatabaseSimulationEDGAR() throws Exception {
    List<Integer> records = new ArrayList<>();
    InputStream is = getClass().getClassLoader().getResourceAsStream("batch.csv");
    if(is == null){
      is=getClass().getClassLoader().getResourceAsStream("batch_01");
      RUMPUP=true;
    }
    try (InputStreamReader streamReader = new InputStreamReader(is);
         BufferedReader reader = new BufferedReader(streamReader)) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] values = line.split(",");
        Integer noOfRequests = Integer.parseInt(values[1]);
        records.add(noOfRequests);
      }
    }
    List<OpenInjectionStep> steps = new ArrayList<OpenInjectionStep>();

    // steps.add(rampUsersPerSec(0).to(20).during(60)); // 6
    // steps.add(constantUsersPerSec(20).during(120));
    // steps.add(rampUsersPerSec(20).to(350).during(60));
    // steps.add(constantUsersPerSec(35).during(120));
    // steps.add(rampUsersPerSec(35).to(55).during(60));
    // steps.add(constantUsersPerSec(55).during(120));
    // steps.add(rampUsersPerSec(55).to(90).during(60));
    // steps.add(constantUsersPerSec(90).during(120));
    // steps.add(rampUsersPerSec(90).to(140).during(60));
    // steps.add(constantUsersPerSec(140).during(120));
    // steps.add(rampUsersPerSec(140).to(200).during(60));
    // steps.add(constantUsersPerSec(200).during(120));
    double devidor = 3;

    int first_rps_size = (int) Math.floor(records.get(1)/devidor);
    int last_rps_size = (int) Math.ceil(records.get(records.size()-1)/devidor);
    System.out.print(Math.ceil((first_rps_size/WARMUP_STEP_RPS)));

    if(RUMPUP) {
      for (int i = 0; i < Math.floor((first_rps_size / WARMUP_STEP_RPS))-1; i++) {
        steps.add(rampUsersPerSec(WARMUP_STEP_RPS * i).to(WARMUP_STEP_RPS * (i + 1)).during(WARMUP_STEP_RAMPUP));
        steps.add(constantUsersPerSec(WARMUP_STEP_RPS * (i + 1)).during(WARMUP_STEP_TIME));
      }
    }

    for (int i = 0; i < records.size() - 1; i++) {
      steps.add(rampUsersPerSec((int) Math.ceil(records.get(i)/devidor)).to((int)Math.ceil(records.get(i+1)/devidor)).during(1));
    }

   /*  for (int i = (int)(Math.ceil(last_rps_size/WARMUP_STEP_RPS)); i > 1; i = i - 2) {
      steps.add(rampUsersPerSec(WARMUP_STEP_RPS*i).to(WARMUP_STEP_RPS*(i-2)).during(WARMUP_STEP_RAMPUP));
      steps.add(constantUsersPerSec(WARMUP_STEP_RPS*(i-2)).during(WARMUP_STEP_TIME));
    }
*/
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