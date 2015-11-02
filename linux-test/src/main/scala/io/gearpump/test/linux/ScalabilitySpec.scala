package io.gearpump.test.linux

import Util._
import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpec}

class ScalabilitySpec extends FlatSpec with Matchers with BeforeAndAfterAll{

  lazy val appName = "app" + System.currentTimeMillis()
  lazy val timeForRunning = 10 * 60 * 1000
  lazy val splitNum = 500
  lazy val sumNum = 500
  lazy val master = YarnBuilder.getMaster
  lazy val uiAddress = YarnBuilder.getUiAddress

  override def beforeAll() = {
    YarnBuilder.init()
    Thread.sleep(5000)
  }

  "Scalability test: Example" should "submit successfully" in {
    val example = Util.examples(5)
    val options = s"-namePrefix $appName -split $splitNum -sum $sumNum"
    submitApp(example, options, master)
    val appInfo = getAppInfo(appName, master)
    val start = appInfo.indexOf("status:")
    val status = appInfo.substring(start + 8, appInfo.indexOf(",", start))
    status should be ("active")
  }

  it should "be running" in {
    Thread.sleep(timeForRunning)
    val appId = getAppId(appName, master)
    val running = isRunning(appId, uiAddress)
    running should be (right = true)
  }

  it should "be killed" in {
    val appId = getAppId(appName, master)
    killApp(appId, master)
    val appInfo = getAppInfo(appName, master)
    val start = appInfo.indexOf("status:")
    val status = appInfo.substring(start + 8, appInfo.indexOf(",", start))
    status should be ("inactive")
  }

}