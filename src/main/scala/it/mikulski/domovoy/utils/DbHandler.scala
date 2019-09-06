package it.mikulski.domovoy.utils

import java.time.Instant

import it.mikulski.domovoy.model.{Advert, Details, Location}
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.Tag

import scala.concurrent.{ExecutionContext, Future}

trait DbHandler {

  implicit val ec: ExecutionContext

  private val db = Database.forConfig("domovoy")

  private val adverts = TableQuery[AdvertsTable]
  private val details = TableQuery[DetailsTable]
  private val locations = TableQuery[LocationTable]

  def createSchemaIfMissing: Future[Unit] = {
    val action = DBIO.seq(
      adverts.schema.createIfNotExists,
      details.schema.createIfNotExists,
      locations.schema.createIfNotExists
    )
    db.run(action)
  }

  def insert(advert: Advert): Future[Unit] = {
    val action = DBIO.seq(adverts += Advert.toRow(advert))
    db.run(action)
  }

  def insert(dt: Details): Future[Unit] = {
    val action = DBIO.seq(details += Details.toRow(dt))
    db.run(action)
  }

  def insert(location: Location): Future[Unit] = {
    val action = DBIO.seq(locations += Location.toRow(location))
    db.run(action)
  }

  def advertSeen(advert: Advert): Future[Int] = {
    val action = adverts.filter(_.id === advert.id).map(_.lastSeenAt).update(Instant.now.toEpochMilli)
    db.run(action)
  }

  def allAdverts: Future[Seq[Advert]] = {
    db.run(adverts.result).map(_.map(Advert.from))
  }

  def allDetails: Future[Seq[Details]] = {
    db.run(details.result).map(_.map(Details.from))
  }

  def allLocations: Future[Seq[Location]] = {
    db.run(locations.result).map(_.map(Location.from))
  }

}

class AdvertsTable(tag: Tag) extends Table[Advert.AdvertRow](tag, "adverts") {
  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def title: Rep[String] = column[String]("title")

  def location: Rep[String] = column[String]("location")

  def area: Rep[String] = column[String]("area")

  def price: Rep[String] = column[String]("price")

  def pricePerMeter: Rep[String] = column[String]("price_per_meter")

  def seller: Rep[String] = column[String]("seller")

  def url: Rep[String] = column[String]("url")

  def firstSeenAt: Rep[Long] = column[Long]("first_seen_at")

  def lastSeenAt: Rep[Long] = column[Long]("last_seen_at")

  def * = (id, title, location, area, price, pricePerMeter, seller, url, firstSeenAt, lastSeenAt)
}

class DetailsTable(tag: Tag) extends Table[Details.DetailsRow](tag, "details") {
  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def overview: Rep[Option[String]] = column[Option[String]]("overview")

  def description: Rep[Option[String]] = column[Option[String]]("description")

  def features: Rep[Option[String]] = column[Option[String]]("features")

  def * = (id, overview, description, features)
}

class LocationTable(tag: Tag) extends Table[Location.LocationRow](tag, "locations") {
  def name: Rep[String] = column[String]("name", O.PrimaryKey)

  def address: Rep[String] = column[String]("address")

  def lat: Rep[Double] = column[Double]("lat")

  def lng: Rep[Double] = column[Double]("lng")

  def * = (name, address, lat, lng)
}