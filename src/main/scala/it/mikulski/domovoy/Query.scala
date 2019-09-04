package it.mikulski.domovoy

trait Query {

  //3A = ':'
  //5D = ']'

  private val PRICE_MAX = 400000
  private val AREA_MIN = 600
  private val AREA_MAX = 1500

  def getQueryUrl(page: Int): String = {
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

}
