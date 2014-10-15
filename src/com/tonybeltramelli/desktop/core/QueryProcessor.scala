package com.tonybeltramelli.desktop.core

import scala.collection.mutable.PriorityQueue

import com.tonybeltramelli.desktop.util.Helper

import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import ch.ethz.dal.tinyir.processing.XMLDocument

class QueryProcessor
{
	def this(query: (List[String], Int), documents: Stream[XMLDocument], topics: List[(String, Int)])
	{
	  this
	  
	  val priorityQ : PriorityQueue[(String, Double)] = new PriorityQueue[(String, Double)]()(Ordering.by(Helper.ordering))
	  
	  for(d <- documents)
	  {
	    priorityQ.enqueue((d.name, _getScore(Helper.stemTokens(d.tokens), query._1)))
	  }
	  
	  val results : List[(String, Double)] = priorityQ.toList.sortBy(res => -res._2).take(Helper.RESULT_NUMBER)
		  
	  println("results for \""+query+"\" : "+results.mkString(", "))
		  
	  _assessPerformance(topics(query._2)._2.toString, results)
	}
	
	private def _getScore (docTokens: List[String], queryTerms: List[String]) : Double =
	{ 
	  val tfs = _getlogTermFreq(_getTermFreq(docTokens))
	  val qtfs = queryTerms.flatMap(q => tfs.get(q))

	  val numTermsInCommon = qtfs.filter(_ > 0).length
		
	  Helper.debug("tfs : " + tfs.mkString(", "))
      Helper.debug("qtfs : " + qtfs.mkString(", "))
	  Helper.debug(numTermsInCommon)
		
	  val docEuclideanLen = tfs.map{case(a, b) => b * b}.sum.toDouble		
      val queryLength = queryTerms.length.toDouble
	  val termOverlap = qtfs.sum / (docEuclideanLen * queryLength)
		
	  numTermsInCommon + termOverlap
	}
	
	private def _getlogTermFreq(tf: Map[String,Int]) : Map[String,Double] =
	{
	  tf.mapValues(v => Helper.log2(v.toDouble / tf.values.sum) + 1.0)	  
	}
	
	private def _getTermFreq(list : List[String]) : Map[String,Int] =
	{
	  list.groupBy(identity).mapValues(l => l.length)
	}
	
	private def _assessPerformance(number: String, results : List[(String, Double)])
	{
	  val judgements = new TipsterGroundTruth(Helper.QRELS_PATH).judgements
		    
	  if(judgements.contains(number))
	  {
	    val relevant = judgements.get(number).get.toSet
	    val retrieved = results.map(r => r._1)
	    val precisionRecall = new PrecisionRecall(retrieved, relevant)
		    
	    val precision = PrecisionRecall.evaluate(retrieved.toSet, relevant).precision
		val recall = PrecisionRecall.evaluate(retrieved.toSet, relevant).recall
		
		println("	precision "+precision+", recall : "+recall)
		println("	precision at recall " + recall + " = " + precisionRecall.precAt(recall, false))
		println("	precision at recall " + recall + " = " + precisionRecall.precAt(recall, true) + " (interpolated)")
	  }
	}
}