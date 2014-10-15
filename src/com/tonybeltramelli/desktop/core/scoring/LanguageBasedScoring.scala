package com.tonybeltramelli.desktop.core.scoring

import ch.ethz.dal.tinyir.processing.XMLDocument

class LanguageBasedScoring extends AScoring
{  
	override def getScore (collection: Stream[(String, List[String])], document: List[String], query: List[String]) : Double =
	{
	  println("fill the language-based scoring method")
	  0.0
    }
}