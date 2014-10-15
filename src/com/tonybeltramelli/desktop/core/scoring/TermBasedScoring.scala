package com.tonybeltramelli.desktop.core.scoring

import com.tonybeltramelli.desktop.util.Helper
import ch.ethz.dal.tinyir.processing.XMLDocument

class TermBasedScoring extends AScoring
{
	override def getScore (collection: Stream[(String, List[String])], document: List[String], query: List[String]) : Double =
	{
	  val tfs = _getSquareRootTermFreq(_getTermFreq(document))
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
	
	private def _getLogTermFreq(tf: Map[String,Int]) : Map[String,Double] =
	{
	  tf.mapValues(v => Helper.log2(v.toDouble / tf.values.sum) + 1.0)
	}
	
	private def _getSquareRootTermFreq(tf: Map[String,Int]) : Map[String,Double] =
	{
	  tf.mapValues(v => Math.sqrt(v) + Math.sqrt(v + 1))
	}
	
	private def _getTermFreq(doc : List[String]) : Map[String,Int] =
	{
	  doc.groupBy(identity).mapValues(l => l.length)
	}
}