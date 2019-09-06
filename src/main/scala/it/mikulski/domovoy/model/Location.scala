package it.mikulski.domovoy.model

case class Location(name: String, address: String, lat: Double, lng: Double)

object Location {

  type LocationRow = (String, String, Double, Double)

  def toRow(location: Location): LocationRow = Location.unapply(location).get

  def from(row: LocationRow): Location = (Location.apply _).tupled(row)

}