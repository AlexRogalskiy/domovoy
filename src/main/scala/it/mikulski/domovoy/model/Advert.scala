package it.mikulski.domovoy.model

import java.time.Instant

import org.jsoup.nodes.Element

case class Advert(id: String, title: String, location: String, area: String, price: String, pricePerMeter: String, seller: String, url: String, firstSeenAt: Long, lastSeenAt: Long) {
  override def equals(obj: Any): Boolean = obj match {
    case a: Advert => id == a.id
    case _ => false
  }

  override def hashCode(): Int = id.hashCode()
}

object Advert {

  type AdvertRow = (String, String, String, String, String, String, String, String, Long, Long)

  def toRow(advert: Advert): AdvertRow = Advert.unapply(advert).get

  def from(row: AdvertRow): Advert = (Advert.apply _).tupled(row)

  def from(article: Element): Advert = {
    val id = article.attr("data-item-id")
    val details = article.getElementsByClass("offer-item-details").first
    val header = details.getElementsByClass("offer-item-header").first

    val title = header.getElementsByClass("offer-item-title").first.text
    val location = cleanLocation(header.child(1).text)
    val area = details.getElementsByClass("offer-item-area").first.text
    val price = details.getElementsByClass("offer-item-price").first.text
    val pricePerMeter = details.getElementsByClass("offer-item-price-per-m").first.text
    val seller = article.getElementsByClass("offer-item-details-bottom").text
    val url = article.attr("data-url")
    val firstSeenAt = Instant.now().toEpochMilli
    val lastSeenAt = firstSeenAt

    Advert(id, title, location, area, price, pricePerMeter, seller, url, firstSeenAt, lastSeenAt)
  }

  def cleanLocation(l: String): String = {
    var cleaned = l.replaceAll("Działka na sprzedaż: ", "")
    cleaned = cleaned.replaceAll("Kraków(, )?", "").replaceAll("krakowski(, )?", "").replaceAll("małopolskie(, )?", "").trim
    if (cleaned.endsWith(",")) cleaned = cleaned.substring(0, cleaned.length - 1).trim
    if (cleaned.trim.isEmpty) cleaned = "Kraków"
    cleaned
  }

}
