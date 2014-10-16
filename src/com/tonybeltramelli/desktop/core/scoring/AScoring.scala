package com.tonybeltramelli.desktop.core.scoring

abstract class AScoring(collection: Stream[(String, List[String])])
{
	protected val _cfs = _getCollectionFreq(collection)
  
	def getScore(document: List[String], query: List[String]) : Double

	protected def _getTermFreq(doc: List[String]) : Map[String, Double] =
	{
	  doc.groupBy(identity).mapValues(l => l.length)
	}
	
	private def _getCollectionFreq(collection: Stream[(String, List[String])]) : Map[String, Double] =
	{
	  _getTermFreq(collection.flatMap(d => d._2).toList)
	}
}