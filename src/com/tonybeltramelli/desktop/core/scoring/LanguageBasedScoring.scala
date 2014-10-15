package com.tonybeltramelli.desktop.core.scoring

class LanguageBasedScoring extends AScoring
{  
	override def getScore (collection: Stream[(String, List[String])], document: List[String], query: List[String]) : Double =
	{
	  val lambda = 0.1f //(0.1f -> 0.7f)
	  
	  val tfs = _getTermFreq(document)
	  val cfs = _getTermFreq(collection.flatMap(d => d._2).toList)
	  
	  val qtfs = query.flatMap(q => tfs.get(q))
	  val ctfs = query.flatMap(q => cfs.get(q))
	 
	  ((1 - lambda) * (qtfs.sum / tfs.map(_._2).sum)) + (lambda) * (qtfs.sum / cfs.map(_._2).sum)
    }
}