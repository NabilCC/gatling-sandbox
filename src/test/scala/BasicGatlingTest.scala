import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.FiniteDuration


class BasicGatlingTest extends Simulation {
  private val baseUrl = "https://stackoverflow.com"

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .acceptHeader("*/*")
    .userAgentHeader("curl/7.54.0")

  val scn: ScenarioBuilder = scenario("Basic")
    .exec(
      http("Root GET").get("/")
    )

  setUp(
    scn.inject(atOnceUsers(1)).protocols(httpProtocol)
  )
}
