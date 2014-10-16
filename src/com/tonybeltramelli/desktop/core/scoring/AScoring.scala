package com.tonybeltramelli.desktop.core.scoring

import collection.mutable.{Map => MutMap}

abstract class AScoring(collection: Stream[(String, List[String])])
{
	protected val _cfs = _getCollectionFreq(collection)
	protected val _cfsSum = _cfs.map(_._2).sum
	
	protected val _allTfs : Map[String, Map[String, Double]] = collection.map(doc => (doc._1 -> _getTermFreq(doc._2))).toMap
	protected val _allTfsSum : Map[String, Double] = _allTfs.mapValues(v => v.map(_._2).sum)
  
	def getScore(documentName: String, query: List[String]) : Double

	private def _getTermFreq(doc: List[String]) : Map[String, Double] =
	{
	  doc.groupBy(identity).mapValues(l => l.length)
	}
	
	private def _getCollectionFreq(collection: Stream[(String, List[String])]) : Map[String, Double] =
	{
	  _getTermFreq(collection.flatMap(d => d._2).toList)
	}
}