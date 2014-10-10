package ch.ethz.dal.tinyir.processing

class StringDocument (val id: Int, val s: String) extends Document {
  def title = ""
  def body = s
  def name = id.toString
  def date = ""
  def content = body  
  override def ID = id  
}