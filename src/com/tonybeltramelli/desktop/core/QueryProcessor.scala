package com.tonybeltramelli.desktop.core

import scala.collection.mutable.PriorityQueue
import com.tonybeltramelli.desktop.util.Helper
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import ch.ethz.dal.tinyir.processing.XMLDocument
import com.tonybeltramelli.desktop.core.scoring.AScoring

class QueryProcessor
{
	def this(query: (List[String], Int), collection: Stream[(String, List[String])], topics: List[(String, Int)], scoringModel: AScoring)
	{
	  this
	  
	  val priorityQ : PriorityQueue[(String, Double)] = new PriorityQueue[(String, Double)]()(Ordering.by(Helper.ordering))
	  
	  for(d <- collection)
	  {
	    priorityQ.enqueue((d._1, scoringModel.getScore(collection, d._2, query._1)))
	  }
	  
	  val results : List[(String, Double)] = priorityQ.toList.sortBy(res => -res._2).take(Helper.RESULT_NUMBER)
		  
	  println("results for \""+query+"\" : "+results.mkString(", "))
	  
	  if(topics != null && topics.contains(query._2))
	  {
	      _assessPerformance(topics(query._2)._2.toString, results)
	  }
	}
	
	private def _assessPerformance(number: String, results : List[(String, Double)])
	{
	  val judgements = new TipsterGroundTruth(Helper.QRELS_PATH).judgements
		    
	  if(judgements.contains(number))
	  {
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
}