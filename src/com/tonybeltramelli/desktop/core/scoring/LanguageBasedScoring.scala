package com.tonybeltramelli.desktop.core.scoring

class LanguageBasedScoring(collection: Stream[(String, List[String])]) extends AScoring(collection: Stream[(String, List[String])])
{  
	override def getScore(document: List[String], query: List[String]) : Double =
	{
	  val lambda : Double = 0.1 //0.1 for title queries and 0.7 for long queries, Jelinek-Mercer smoothing method 
	  //"A Study of Smoothing Methods for Language Models Applied to Information Retrieval"
	  //Chengxiang Zhai and John Lafferty, 2001
	  
	  val tfs = _getTermFreq(document)
	  
	  val qtfs = query.flatMap(q => tfs.get(q))
	  val qcfs = query.flatMap(q => _cfs.get(q))
	  
	  ((1 - lambda) * (qtfs.sum / tfs.map(_._2).sum)) + (lambda * (qcfs.sum / _cfs.map(_._2).sum))
    }
}