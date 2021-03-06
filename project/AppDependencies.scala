import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "simple-reactivemongo" % "7.12.0-play-25",
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % "4.8.0",
    "uk.gov.hmrc" %% "wco-dec" % "0.18.0",
    "uk.gov.hmrc" %% "logback-json-logger" % "4.1.0"
  )

  def test(scope: String = "test"): Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "hmrctest" % "3.4.0-play-25" % scope,
    "org.scalatest" %% "scalatest" % "3.0.4" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % "test",
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "org.mockito" % "mockito-core" % "2.13.0" % "test"
  )
}
