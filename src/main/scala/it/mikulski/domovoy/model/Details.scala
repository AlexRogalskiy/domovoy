package it.mikulski.domovoy.model

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.jdk.CollectionConverters._

case class Details(id: String, overviewHtml: Option[String], descriptionHtml: Option[String], featuresHtml: Option[String]) {

  def overview: Map[String, String] = {
    //val items = Jsoup.parse(overviewHtml).getElementsByTag("ul").first.children().asScala
    ???
  }

}

object Details {

  type DetailsRow = (String, Option[String], Option[String], Option[String])

  def toRow(details: Details): DetailsRow = Details.unapply(details).get

  def from(row: DetailsRow): Details = (Details.apply _).tupled(row)

  def from(root: Document, id: String): Details = {
    val overview = getOptionalSection(root, "section-overview")
    val description = getOptionalSection(root, "section-description")
    val features = getOptionalSection(root, "section-features")
    Details(id, overview, description, features)
  }

  private def getOptionalSection(root: Document, section: String): Option[String] = {
    val elements = root.getElementsByClass(section)
    if (!elements.isEmpty) {
      val nonStyleNodes: Seq[Element] = elements.first().children().asScala.filter(_.tagName != "style").toSeq
      Some(new Elements(nonStyleNodes: _*).html)
    } else None
  }
}
