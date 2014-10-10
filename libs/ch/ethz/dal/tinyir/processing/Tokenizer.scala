package ch.ethz.dal.tinyir.processing

object Tokenizer {
  def tokenize (text: String) : List[String] =
    text.split("[ .,;:?!\t\n\r\f]+").toList
}