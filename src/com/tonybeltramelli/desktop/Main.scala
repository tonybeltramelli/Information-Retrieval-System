package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.ZipExplorer
import com.tonybeltramelli.desktop.performance.PrecisionRecall
import ch.ethz.dal.tinyir.io.ZipDirStream
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import com.github.aztek.porterstemmer.PorterStemmer
import com.sun.xml.internal.bind.v2.TODO
import scala.collection.mutable.PriorityQueue
import scala.collection.immutable.Stream
import scala.io.Source
import java.io.InputStream
import math.{min,max}

object Main {
	def main(args: Array[String])
	{
	  new Main(args(0).toBoolean, args.slice(1, args.length).toList)
	}
}

class Main
{
	val ROOT_PATH = "data/tipster/"
	val ZIP_PATH = ROOT_PATH + "/zips"
	val QRELS_PATH = ROOT_PATH + "/qrels"
	val TOPIC_PATH = ROOT_PATH + "/topics"
	val RESULT_NUMBER = 100
	
	def this(useTopic: Boolean, queries: List[String])
	{
	  this()

	  var qu = queries
	  var topics : List[(String, Int)] = null
	  
	  if(useTopic)
	  {
		  topics = _getTopics
		  qu = topics.map(_._1)
	  }
	  
	  //TODO remove
	  qu = qu.take(10)
	  
	  val time = System.nanoTime()
	  
	  val tipster = new TipsterStream(ZIP_PATH)	  
	  val queriesTokens = qu.map(q => Tokenizer.tokenize(q)).map(t => _stemTokens(t)).zipWithIndex
	  
	  println("time 0 : " + (System.nanoTime() - time) / 1000000000.0 + " seconds")
	  
	  val docs = tipster.stream.take(1000)
	  
	  var priorityQ : PriorityQueue[(String, Double)] = null
	  var results : List[(String, Double)] = null
	  
	  for(query <- queriesTokens)
	  {
		priorityQ = new PriorityQueue[(String, Double)]()(Ordering.by(_ordering))
		
		for(d <- docs)
		{
		  priorityQ.enqueue((d.name, _getScore(_stemTokens(d.tokens), query._1)))
		}
		
		results = priorityQ.toList.sortBy(res => -res._2).take(RESULT_NUMBER)
		  
		println("results for \""+query+"\" : "+results.mkString(", "))
		  
		if(useTopic) _assessPerformance(topics(query._2)._2.toString, results)
	  }
	  
	  println("time 1 : " + (System.nanoTime() - time) / 1000000000.0 + " seconds")
	}
	
	private def _getTermFreq(list : List[String]) : Map[String,Int] =
	{
	  list.groupBy(identity).mapValues(l => l.length)
	}
	
	private def _getScore (docTokens: List[String], queryTerms: List[String]) : Double =
	{
	  val tfs = _getTermFreq(docTokens)
	  val qtfs = queryTerms.flatMap(q => tfs.get(q))

	  val numTermsInCommon = qtfs.filter(_ > 0).length
		
	  debug("tfs : " + tfs.mkString(", "))
      debug("qtfs : " + qtfs.mkString(", "))
	  debug(numTermsInCommon)
		
	  val docEuclideanLen = tfs.map{case(a, b) => b * b}.sum.toDouble		
      val queryLen = queryTerms.length.toDouble
	  val termOverlap = qtfs.sum / (docEuclideanLen * queryLen)
		
	  numTermsInCommon + termOverlap
	}
	
	private def _assessPerformance(number: String, results : List[(String, Double)])
	{
	  val judgements = new TipsterGroundTruth(QRELS_PATH).judgements
		    
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
	
	private def _stemTokens(list: List[String]) : List[String] = 
	{
	  list.map(t => t.toLowerCase()).map(PorterStemmer.stem(_))
	}
	
	private def _getTopics : List[(String, Int)] =
	{
	  val lines = Source.fromFile(TOPIC_PATH).getLines
	  val topicsTitle = lines.filter(l => l.contains("<title>")).map(l => l.split(":")(1).trim.toLowerCase)
	  val topicsNumber = lines.filter(l => l.contains("<num>")).map(l => l.split(":")(1).trim.toInt)
	  
	  topicsTitle.zip(topicsNumber).toList
	}
	
	private def _ordering(row: (String, Double)) = row._2
	
	val isDebugMode: Boolean = false
	
	def debug(s : Any)
	{
	  if(!isDebugMode) return
	  println(s)
	}
}