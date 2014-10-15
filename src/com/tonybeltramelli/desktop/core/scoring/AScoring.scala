package com.tonybeltramelli.desktop.core.scoring

import ch.ethz.dal.tinyir.processing.XMLDocument

trait AScoring {
	def getScore (collection: Stream[(String, List[String])], document: List[String], query: List[String]) : Double
}