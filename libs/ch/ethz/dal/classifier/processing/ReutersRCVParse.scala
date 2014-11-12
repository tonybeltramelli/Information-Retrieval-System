package ch.ethz.dal.classifier.processing

import javax.xml.parsers._
import java.io.InputStream
import scala.util.Try

class ReutersRCVParse(is: InputStream) extends XMLDocument(is) { 
  override def name = getAttrFromFirstNode("itemid", "newsitem").getOrElse("") 
  override def date = getAttrFromFirstNode("date","newsitem").getOrElse("")   
  override def codes = getAttrFromAllNodes("code","code").toSet // All codes.
  override def topics = {
    val attr = "class"
    val tag = "codes"
    val nodelist = doc.getElementsByTagName(tag)
    if (nodelist == null) List().toSet
    else if (nodelist.getLength==0) List().toSet
    else {
      val result = new collection.mutable.ListBuffer[String]
      for (i <- 0 until nodelist.getLength) {
        val attributes = nodelist.item(i).getAttributes
        val text = Try(attributes.getNamedItem(attr).getTextContent)

        if (text.isSuccess && text.get.contains("topics")) {
          val childNodes = nodelist.item(i).getChildNodes
          for (j <- 0 until childNodes.getLength) {
            val childAttr = childNodes.item(j).getAttributes
            val textChild = Try(childAttr.getNamedItem("code").getTextContent)
            if (textChild.isSuccess) {
              result += textChild.get
            }
          }
        }
      }
      result.toList.toSet
    }    
  }
}


