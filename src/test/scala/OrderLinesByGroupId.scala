import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.FiniteDuration

class OrderLinesByGroupId extends Simulation {
  private val baseUrl = "http://localhost:7004/orderLines/group/"
  private val groupIdFeeder = csv("order-line-grouping/order-line-group-ids.csv").random

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .acceptHeader("*/*")
    .userAgentHeader("curl/7.54.0")

  val scn: ScenarioBuilder = scenario("Order Line By Group Id")
    .feed(groupIdFeeder)
    .exec(http("Order lines by group id").get("${groupId}"))

  setUp(
    scn.inject(rampUsersPerSec(5).to(20).during(FiniteDuration(2, TimeUnit.MINUTES))).protocols(httpProtocol)
  )
}
