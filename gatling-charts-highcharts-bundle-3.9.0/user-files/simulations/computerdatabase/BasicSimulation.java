package computerdatabase; // 1

// 2
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

class BasicSimulationJava extends Simulation { // 3

  HttpProtocolBuilder httpProtocol = http // 4
    .baseUrl("http://phd-demo1.swedencentral.cloudapp.azure.com:30036") // 5
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");

  ScenarioBuilder scn = scenario("BasicSimulation") // 7
    .exec(http("request_1") // 8
      .get("/api/execute/")); // 9
    //.pause(5); // 10

  {
    //setUp( // 11
      //scn.injectOpen(atOnceUsers(1)) // 12     
    //).protocols(httpProtocol); // 13

    setUp(
    scn.injectOpen(
        nothingFor(4), // 1
        atOnceUsers(10), // 2
        rampUsers(10).during(5), // 3
        constantUsersPerSec(20).during(15), // 4
        constantUsersPerSec(20).during(15).randomized(), // 5
        rampUsersPerSec(10).to(20).during(10), // 6
        rampUsersPerSec(10).to(20).during(10).randomized(), // 7
        stressPeakUsers(1000).during(20) // 8
    ).protocols(httpProtocol)
    );
  }
}