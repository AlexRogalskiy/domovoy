package it.mikulski.domovoy

import java.net.URLEncoder

import io.circe.optics.JsonPath._
import io.circe.parser._
import it.mikulski.domovoy.model.Location
import it.mikulski.domovoy.utils.DbHandler

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.Source

object ScannerLocation extends App with DbHandler {

  val API_KEY = "DO NOT COMMIT"

  implicit val ec = ExecutionContext.global

  val missingNamesF: Future[Seq[String]] = for {
    _ <- createSchemaIfMissing
    adverts <- allAdverts
    locations <- allLocations
  } yield adverts.filterNot(ad => locations.exists(_.name == ad.location)).map(_.location).distinct.sorted

  val missingNames = Await.result(missingNamesF, Duration.Inf)

  println("Total locations to retrieve: " + missingNames.size)

  missingNames.take(1).foreach { name =>
    println(name)
    val loc = getLocation(name)
    println("\t" + loc)
    Await.result(insert(loc), Duration.Inf)
  }

  println("All done.")

  def query(place: String) = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
    s"key=$API_KEY&" +
    s"input=${URLEncoder.encode(place, "UTF-8")}&" +
    s"inputtype=textquery&" +
    s"fields=formatted_address,geometry,name,types"

  def getLocation(name: String): Location = {
    val raw = Source.fromURL(query(name)).mkString
    val json = parse(raw).right.get
    val _address = root.candidates.index(0).formatted_address.string
    val _lat = root.candidates.index(0).geometry.location.lat.double
    val _lng = root.candidates.index(0).geometry.location.lng.double
    Location(name, _address.getOption(json).get, _lat.getOption(json).get, _lng.getOption(json).get)
  }

}
