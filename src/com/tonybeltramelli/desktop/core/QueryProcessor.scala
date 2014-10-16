package com.tonybeltramelli.desktop.core

import com.tonybeltramelli.desktop.core.scoring.AScoring
import com.tonybeltramelli.desktop.util.Helper

import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth

class QueryProcessor
{
	def this(query: (List[String], Int), collection: Stream[(String, List[String])], topics: List[(String, Int)], scoringModel: AScoring)
	{
	  this
	  
	  val results = collection.map(d => (
	      d._1 -> scoringModel.getScore(d._1, query._1)
	      )).toList.sortBy(res => -res._2)
	  
	  println("results for \""+query+"\" : "+results.mkString(", "))
	  
	  if(topics != null)
	  {
	      _assessPerformance(topics(query._2)._2.toString, results)
	  }
	}
	
	private def _assessPerformance(number: String, results : List[(String, Double)])
	{
	  val judgements = new TipsterGroundTruth(Helper.QRELS_PATH).judgements
		    
	  if(!judgements.contains(number)) return
	  
	  val relevant = judgements.get(number).get.toSet
	  val retrieved = results.map(r => r._1)
	  val precisionRecall = new PrecRec(retrieved, relevant)
		    
	  val precision = PrecRec.evaluate(retrieved.toSet, relevant).precision
	  val recall = PrecRec.evaluate(retrieved.toSet, relevant).recall
		
	  println("	precision "+precision+", recall : "+recall)
	  println("	precision at recall " + recall + " = " + precisionRecall.precAt(recall, false))
	  println("	precision at recall " + recall + " = " + precisionRecall.precAt(recall, true) + " (interpolated)")
	}
}