package it.mikulski.domovoy

import org.jsoup.Jsoup

import scala.collection.mutable

import scala.collection.JavaConverters._

object Domovoy extends App with Query {

  val adverts = mutable.Set[Advert]()

  for(i <- 1 to 1)  {
    println("Processing page: " + i)
    val doc = Jsoup.connect(getQueryUrl(i)).get()

    /*
    val text = doc.body().text()
    "liczba ofert: (\\d+)".r.findFirstMatchIn(text) match {
      case Some(n) => println("total found: " + n)
      case None => println("ERROR!")
    }*/

    val articles = doc.getElementsByClass("section-listing__row-content").first.getElementsByClass("offer-item")
    println("  articles on page: " + articles.size)
    articles.asScala.foreach(a => adverts.add(Advert(a)))
  }

  println

  println("Processing complete, total adverts retrieved: " + adverts.size)

  println

  adverts.groupBy(_.location).toList.sortBy(_._2.size).reverse.foreach(t => println(s"${t._2.size}\t${t._1}"))

  println

  //adverts.groupBy(_.seller).toList.sortBy(_._2.size).reverse.foreach(t => println(s"${t._2.size}\t${t._1}"))

}
