package ch.ethz.dal.tinyir.processing

object StopWords {
  val stopWords = Set("the", "a", "is", "it", "are")
  def filter(tokens : Seq[String]) = tokens.filter(stopWords)
}