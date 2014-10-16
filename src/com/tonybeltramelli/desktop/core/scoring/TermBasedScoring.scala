package com.tonybeltramelli.desktop.core.scoring

import com.tonybeltramelli.desktop.util.Helper

class TermBasedScoring(collection: Stream[(String, List[String])]) extends AScoring(collection: Stream[(String, List[String])])
{
	override def getScore(documentName: String, query: List[String]) : Double =
	{
	  val tfs = _getSquareRootTermFreq(_allTfs.get(documentName).get)
	  val qtfs = query.flatMap(q => tfs.get(q))

	  val numTermsInCommon = qtfs.filter(_ > 0).length
		
	  Helper.debug("tfs : " + tfs.mkString(", "))
      Helper.debug("qtfs : " + qtfs.mkString(", "))
	  Helper.debug(numTermsInCommon)
		
	  val docEuclideanLen = tfs.map{case(a, b) => b * b}.sum.toDouble		
      val queryLength = query.length.toDouble
	  val termOverlap = qtfs.sum / (docEuclideanLen * queryLength)
		
	  numTermsInCommon + termOverlap
	}
	
	private def _getLogTermFreq(tf: Map[String, Double]) : Map[String, Double] =
	{
	  tf.mapValues(v => Helper.log2(v.toDouble / tf.values.sum) + 1.0)
	}
	
	private def _getSquareRootTermFreq(tf: Map[String, Double]) : Map[String, Double] =
	{
	  tf.mapValues(v => Math.sqrt(v) + Math.sqrt(v + 1))
	}
}