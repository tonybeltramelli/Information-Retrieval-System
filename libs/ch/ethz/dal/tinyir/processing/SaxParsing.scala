package ch.ethz.dal.tinyir.processing

import scala.collection.mutable.HashMap
import javax.xml.parsers._
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class MySAXHandler extends DefaultHandler {
   var tagCounts :  HashMap[Integer,String] = null
   override def startDocument = { tagCounts = new HashMap() }
   override def endDocument = { /* retrieve counts from tagCounts */ }
   override def startElement(uri: String, name: String, qName: String, atts: Attributes) = {
      incrementTagCount(name);
   }
   def endElement = {}
   private def incrementTagCount(key: String) = { /* helper function */ }
}