package com.tonybeltramelli.desktop.core.scoring

import scala.collection.mutable.{Map => MutMap}

trait AScoring
{
	protected var _tfss : MutMap[String, (Map[String, Int], Int)] = MutMap[String, (Map[String, Int], Int)]()
	
	protected var _cfs : MutMap[String, Double] = MutMap() 
	protected var _cfsSum : Double = 0.0
	
	def feed(documentName: String, document: List[String], queries: List[(List[String], Int)])
	{
	  val tfs = _getTermFreq(document)
	  val tfsSum = tfs.map(v => v._2).sum
	  
	  _cfsSum += tfsSum
	  tfs.filter(t => queries.map(q => q._1).exists(q => q.contains(t._1))).foreach(t => _cfs(t._1) = _cfs.get(t._1).getOrElse(0.0) + t._2)
	  
	  if(queries.map(q => q._1.filter(w => tfs.contains(w))).map(q => q.size).sum > 0) _tfss += (documentName -> (tfs, tfsSum))
	}

	def getScore(f: (Map[String, Int], Int), query: List[String]) : Double
	
	private def _getTermFreq(doc: List[String]) =
	{
	  doc.groupBy(identity).mapValues(l => l.length)
	}
	
	private def _getCollectionFreq(collection: Stream[(String, List[String])]) =
	{
	  _getTermFreq(collection.flatMap(d => d._2).toList)
	}
	
	def get = _tfss
	def getNames = {_tfss.map(f =>  f._1)}
}