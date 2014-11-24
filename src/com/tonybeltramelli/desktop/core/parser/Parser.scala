package com.tonybeltramelli.desktop.core.parser

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import com.tonybeltramelli.desktop.util.Helper
import ch.ethz.dal.classifier.processing.ReutersRCVParse

class Parser {
  
  var doc : ReutersRCVParse = null
  var documentNumber : Int = 0 
  
  def parse(resource: String, callback: => Unit)
  {
    val path = Helper.getResource(resource)
    val iter = new ReutersCorpusIterator(path)
    
    var documentCounter = 0
    
    while (iter.hasNext && documentCounter < documentNumber) {
      doc = iter.next
      
      documentCounter += 1
      
      callback

      if(documentCounter > 0 && documentCounter % 1000 == 0)
      {
        Helper.time(documentCounter + " documents processed")
      }
    }

    println("parsing "+documentCounter+" documents done at "+path)
  }
}