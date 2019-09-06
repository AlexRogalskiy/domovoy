package it.mikulski.domovoy

import it.mikulski.domovoy.model.{Advert, Details}
import it.mikulski.domovoy.utils.DbHandler
import org.jsoup.Jsoup

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object ScannerDetails extends App with DbHandler {

  implicit val ec = ExecutionContext.global

  val adverts = Await.result(getAdvertsWithoutDetails, Duration.Inf)

  val dts = adverts.map { ad =>
    println(ad.url)
    Await.result(scrapeAndInsertDetails(ad), Duration.Inf)
  }

  println("Done, all details processed: " + dts.size)

  def getAdvertsWithoutDetails: Future[Seq[Advert]] = {
    for {
      _ <- createSchemaIfMissing
      adverts <- allAdverts
      details <- allDetails
    } yield adverts.filterNot(ad => details.exists(_.id == ad.id))
  }

  def scrapeAndInsertDetails(ad: Advert): Future[Details] = {
    Future {
      val doc = Jsoup.connect(ad.url).get()
      Details.from(doc, ad.id)
    }.flatMap(dt => insert(dt).map(_ => dt))
  }

}
