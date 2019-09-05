package it.mikulski.domovoy

import it.mikulski.domovoy.model.Advert
import it.mikulski.domovoy.utils.DbHandler
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.language.postfixOps

object ScannerAdvert extends App with DbHandler {

  implicit val ec = ExecutionContext.global

  val adverts = mutable.Set[Advert]()

  for(i <- 1 to 8)  {
    getArticlesFromPage(i).foreach(adverts.add)
  }

  println("Processing complete, total adverts retrieved: " + adverts.size)

  //adverts.groupBy(_.location).toList.sortBy(_._2.size).reverse.foreach(t => println(s"${t._2.size}\t${t._1}"))

  val f = for {
    _ <- createSchemaIfMissing
    existing <- allAdverts
    toInsert = adverts.filterNot(existing.contains)
    toUpdate = adverts.filter(existing.contains)
    _ <- Future.sequence(toInsert.toSeq.map(insert))
    _ <- Future.sequence(toUpdate.toSeq.map(advertSeen))
  } yield ()

  Await.result(f, 5 minutes)

  println("Done.")

  private def getQueryUrl(page: Int): String = {
    "https://www.otodom.pl/sprzedaz/dzialka/krakow/budowlana/?" +
      "search%5Bfilter_float_price%3Ato%5D=400000&" +
      "search%5Bfilter_float_m%3Afrom%5D=600&" +
      "search%5Bfilter_float_m%3Ato%5D=1500&" +
      "search%5Bdescription%5D=1&" +
      "search%5Border%5D=created_at_first%3Adesc&" +
      "search%5Bregion_id%5D=6&" +
      "search%5Bsubregion_id%5D=410&" +
      "search%5Bcity_id%5D=38&" +
      "search%5Bdist%5D=5&" +
      "nrAdsPerPage=72&" +
      "page=" + page
  }

  private def getArticlesFromPage(pageNumber: Int): Set[Advert] = {
    val doc = Jsoup.connect(getQueryUrl(pageNumber)).get()
    val articles = doc.getElementsByClass("section-listing__row-content").first.getElementsByClass("offer-item")
    articles.asScala.map(Advert.from).toSet
  }

  private def getTotalFoundFromPage(doc: Document): Int = {
    "liczba ofert: (\\d+)".r.findFirstMatchIn(doc.body.text) match {
      case Some(n) => n.group(1).toInt
      case None => -1
    }
  }

}
