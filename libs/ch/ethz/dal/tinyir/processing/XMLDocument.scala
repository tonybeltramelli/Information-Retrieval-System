package ch.ethz.dal.tinyir.processing

import io.Source
import io.Codec
import util.Try
import util.Success

import java.nio.charset.Charset
import java.io.InputStream
import java.io.FileInputStream
import java.io.BufferedInputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.{Document => XMLDoc}
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

class XMLDocument (is: InputStream) extends Document {
  
 def this(fname: String) = this(new BufferedInputStream(new FileInputStream(fname)))
  
  val doc : XMLDoc = {   
    val dbFactory = DocumentBuilderFactory.newInstance
    val dBuilder  = dbFactory.newDocumentBuilder
    dBuilder.parse(is)
  }

  // all accesses to specific elements are optional 
  def title  : String = read(doc.getElementsByTagName("title")) 
  def body   : String = read(doc.getElementsByTagName("text"))
  def name   : String = "" 
  def date   : String = ""
  def content: String = body  

  // helper method that reads from specific nodelist 
  protected def read(nlist: NodeList) : String = {
 	if (nlist==null) ""
 	else if (nlist.getLength==0) ""
 	else {
 	    val length = nlist.getLength
 	    val text  = for (i <- 0 until length) yield nlist.item(i).getTextContent
	    text.mkString(System.getProperty("line.separator"))
   	  }
 	}
  
  // helper method to combine two content fields
  protected def append(a: Option[String], b: Option[String]) = (a, b) match {
      case (None, None)      => ""
      case (Some(a), None)   => a
      case (None, Some(b))   => b
      case (Some(a),Some(b)) => a + System.getProperty("line.separator") + b
    }

  // get attribute value from first node of given tag
  protected def getAttrFromFirstNode(attr: String, tag: String) : Option[String] = { 
    val lst = getAttrFromAllNodes(attr,tag)
    if (lst.length==0) None else Some(lst.head)
  }  

  // get attribute values from all nodes of given tag
  protected def getAttrFromAllNodes(attr: String, tag: String) : List[String] = { 
    val nodelist = doc.getElementsByTagName(tag)
    if (nodelist == null) List()
    else if (nodelist.getLength==0) List()
    else {
      val result = new collection.mutable.ListBuffer[String]
      for (i <- 0 until nodelist.getLength) {
        val attributes = nodelist.item(i).getAttributes;
        val text = Try(attributes.getNamedItem(attr).getTextContent);
        if (text.isSuccess) result += text.get 
      }
      result.toList
    }
  }  
}

object XMLDocument 
  
 