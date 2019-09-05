package it.mikulski.domovoy

import it.mikulski.domovoy.model.{Advert, Details}
import slick.lifted.Tag
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.{ExecutionContext, Future}

trait DbHandler {

  implicit val ec: ExecutionContext

  private val db = Database.forConfig("domovoy")

  private val adverts = TableQuery[AdvertsTable]

  def createSchema: Future[Unit] = {
    val action = DBIO.seq(adverts.schema.create)
    db.run(action)
  }

  def insert(advert: Advert): Future[Unit] = {
    val action = DBIO.seq(adverts += Advert.toRow(advert))
    db.run(action)
  }

  def readAll: Future[Seq[Advert]] = {
    db.run(adverts.result).map(_.map(Advert.from))
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
  def overview: Rep[String] = column[String]("overview")
  def description: Rep[String] = column[String]("description")
  def features: Rep[String] = column[String]("features")
  def * = (id, overview, description, features)
}