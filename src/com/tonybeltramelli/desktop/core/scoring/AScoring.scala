package com.tonybeltramelli.desktop.core.scoring

trait AScoring {
	def getScore (collection: Stream[(String, List[String])], document: List[String], query: List[String]) : Double

	protected def _getTermFreq(doc : List[String]) : Map[String,Int] =
	{
	  doc.groupBy(identity).mapValues(l => l.length)
	}
}