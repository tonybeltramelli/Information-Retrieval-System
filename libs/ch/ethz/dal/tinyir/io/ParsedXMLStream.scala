package ch.ethz.dal.tinyir.io

import ch.ethz.dal.tinyir.processing.XMLDocument

abstract class ParsedXMLStream (val unparsed: DocStream) { 
  def stream : Stream[XMLDocument]
  def length : Int

}
