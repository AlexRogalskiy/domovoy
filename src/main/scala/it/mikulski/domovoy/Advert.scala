package it.mikulski.domovoy

import org.jsoup.nodes.Element

case class Advert(id: String, title: String, location: String, area: String, price: String, pricePerMeter: String, seller: String)

object Advert {

  def apply(article: Element): Advert = {
    val id = article.attr("data-item-id")
    val details = article.getElementsByClass("offer-item-details").first
    val header = details.getElementsByClass("offer-item-header").first

    val title = header.getElementsByClass("offer-item-title").first.text
    val location = cleanLocation(header.child(1).text)
    val area = details.getElementsByClass("offer-item-area").first.text
    val price = details.getElementsByClass("offer-item-price").first.text
    val pricePerMeter = details.getElementsByClass("offer-item-price-per-m").first.text
    val seller = article.getElementsByClass("offer-item-details-bottom").text

    Advert(id, title, location, area, price, pricePerMeter, seller)
  }

  def cleanLocation(l: String): String = {
    var cleaned = l.replaceAll("Działka na sprzedaż: ", "")
    cleaned = cleaned.replaceAll("Kraków(, )?", "").replaceAll("krakowski(, )?", "").replaceAll("małopolskie(, )?", "").trim
    if(cleaned.endsWith(",")) cleaned = cleaned.substring(0, cleaned.length - 1).trim
    if(cleaned.trim.isEmpty) cleaned = "Kraków"
    cleaned
  }

}
