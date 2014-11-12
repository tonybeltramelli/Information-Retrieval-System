package ch.ethz.dal.classifier.processing

object Tokenizer {
  def tokenize (text: String) : List[String] =
    text.split("[ -\'\".,;:?!\t\n\r\f]+").toList
}