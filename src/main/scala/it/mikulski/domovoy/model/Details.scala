package it.mikulski.domovoy.model

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.jdk.CollectionConverters._

case class Details(id: String, overviewHtml: String, descriptionHtml: String, featuresHtml: String) {

  def overview: Map[String, String] = {
    val items = Jsoup.parse(overviewHtml).getElementsByTag("ul").first.children().asScala
    ???
  }

}

object Details {

  type DetailsRow = (String, String, String, String)

  def toRow(details: Details): DetailsRow = Details.unapply(details).get

  def from(row: DetailsRow): Details = (Details.apply _).tupled(row)

  def from(root: Document, id: String): Details = {
    val overview = root.getElementsByClass("section_overview").first.html
    val description = root.getElementsByClass("section_description").first.html
    val features = root.getElementsByClass("section_features").first.html
    Details(id, overview, description, features)
  }
}
