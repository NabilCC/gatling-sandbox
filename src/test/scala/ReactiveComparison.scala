import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.FiniteDuration

class ReactiveComparison extends Simulation {
  private val serverPort = Integer.getInteger("server.port")
  private val minConcurrentUsers = Integer.getInteger("min.concurrent.users").toDouble
  private val maxConcurrentUsers = Integer.getInteger("max.concurrent.users").toDouble
  private val testDurationMins   = Integer.getInteger("test.duration").toLong

  private val baseUrl = "http://localhost:" + serverPort
  private val contentType = "application/json"
  private val jsonFeeder = jsonFile("reactive-comparison/request-values.json").random

  println(f"Using server port: $serverPort%s, min concurrent users: $minConcurrentUsers%s,  maxConcurrentUsers: $maxConcurrentUsers%s and duration: $testDurationMins%s minute(s)")

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .acceptHeader("*/*")
    .contentTypeHeader(contentType)
    .userAgentHeader("curl/7.54.0")

  val postRequestLoop = repeat(5) {
    feed(jsonFeeder)
      .exec(http("Make POST").post("/impacts/")
        .body(StringBody("""{"startDate" : "${startDate}", "endDate" : "${endDate}", "frameIds" : [${frameIds}], "days" : [${days}], "hours" : [${hours}]}""")).asJson)
      .pause(FiniteDuration(500, TimeUnit.MILLISECONDS))
  }

  val scn: ScenarioBuilder = scenario("Impacts Requests")
    .exec(postRequestLoop)

  setUp(
    scn.inject(rampUsersPerSec(minConcurrentUsers).to(maxConcurrentUsers).during(FiniteDuration(testDurationMins, TimeUnit.MINUTES)))
      .protocols(httpProtocol)
  )
}
