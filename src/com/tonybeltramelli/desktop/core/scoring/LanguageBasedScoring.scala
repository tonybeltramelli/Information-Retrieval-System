package com.tonybeltramelli.desktop.core.scoring

import com.tonybeltramelli.desktop.util.Helper

class LanguageBasedScoring(collection: Stream[(String, List[String])]) extends AScoring(collection: Stream[(String, List[String])])
{  
	override def getScore(documentName: String, query: List[String]) : Double =
	{
	  val lambda : Double = 0.1 //0.1 for title queries and 0.7 for long queries, Jelinek-Mercer smoothing method 
	  //"A Study of Smoothing Methods for Language Models Applied to Information Retrieval"
	  //Chengxiang Zhai and John Lafferty, 2001
	  
	  val tfs = _allTfs.get(documentName).get
	  val tfsSum = _allTfsSum.get(documentName).get
	  
	  Helper.debug("tfs : " + tfs.mkString(", "))
      Helper.debug("cfs : " + _cfs.mkString(", "))

	  val qtfs = query.flatMap(q => tfs.get(q))
	  val qcfs = query.flatMap(q => _cfs.get(q))
	  
	  ((1 - lambda) * (qtfs.sum / tfsSum)) + (lambda * (qcfs.sum / _cfsSum))
    }
}