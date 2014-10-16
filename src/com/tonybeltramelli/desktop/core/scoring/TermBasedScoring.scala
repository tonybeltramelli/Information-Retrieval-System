package com.tonybeltramelli.desktop.core.scoring

import com.tonybeltramelli.desktop.util.Helper

class TermBasedScoring extends AScoring
{
	override def getScore(f: (Map[String, Double], Double), query: List[String]) : Double =
	{ 
	  val tfs = _getSquareRootTermFreq(f._1)
	  val qtfs = query.flatMap(q => tfs.get(q))

	  val numTermsInCommon = qtfs.filter(_ > 0).length
		
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
