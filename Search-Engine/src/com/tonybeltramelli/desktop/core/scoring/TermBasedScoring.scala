package com.tonybeltramelli.desktop.core.scoring

import com.tonybeltramelli.desktop.util.Helper

class TermBasedScoring extends AScoring
{
	override def getScore(f: (Map[String, Int], Int), query: List[String]) : Double =
	{ 
	  val tfs = _getSquareRootTermFreq(f._1)
	  val qtfs = query.flatMap(q => tfs.get(q))

	  val numTermsInCommon = qtfs.filter(_ > 0).length
		
	  val docEuclideanLen = tfs.map{case(a, b) => b * b}.sum.toDouble		
      val queryLength = query.length.toDouble
	  val termOverlap = qtfs.sum / (docEuclideanLen * queryLength)
		
	  numTermsInCommon + termOverlap
	}
	
	private def _getLogTermFreq(tf: Map[String, Int]) : Map[String, Double] =
	{
	  tf.mapValues(v => Helper.log2(v.toDouble / tf.values.sum) + 1.0)
	}
	
	private def _getSquareRootTermFreq(tf: Map[String, Int]) : Map[String, Double] =
	{
	  tf.mapValues(v => Math.sqrt(v) + Math.sqrt(v + 1))
	}
	
	private def _getAugmentedTermFreq(tf : Map[String, Int]) : Map[String, Double] =
	{
		val max = tf.values.max.toDouble
		tf.mapValues( f => 0.5 * f / max + 0.5 )
	}
}
