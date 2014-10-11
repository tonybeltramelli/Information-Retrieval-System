package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.ZipExplorer
import ch.ethz.dal.tinyir.io.ZipDirStream
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer
import com.github.aztek.porterstemmer.PorterStemmer
import com.sun.xml.internal.bind.v2.TODO
import scala.collection.mutable.PriorityQueue
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import scala.collection.immutable.Stream
import java.io.InputStream
import scala.io.Source

object Main {
	def main(args: Array[String])
	{
	  new Main(args(0).toInt, args.slice(1, args.length).toList)
	}
}

class Main
{
	val ROOT_PATH = "data/tipster/"
	val ZIP_PATH = ROOT_PATH + "/zips"
	val QRELS_PATH = ROOT_PATH + "/qrels"
	val TOPIC_PATH = ROOT_PATH + "/topics"
	
	def this(resultNumber: Int, queries: List[String])
	{
	  this()
	  
	  var qu = queries
	  
	  //for topic
	  qu = _getTopics.map(_._1)
	  
	  val time = System.nanoTime()
	  
	  val tipster = new TipsterStream(ZIP_PATH)	  
	  val queriesTokens = qu.map(q => Tokenizer.tokenize(q)).map(t => _stemTokens(t))
	  
	  println("time 0 : " + (System.nanoTime() - time) / 1000000000.0 + " seconds")
	  
	  prt("queries : "+queries.mkString(", "))
	  prt("documents : "+tipster.length)

	  val docs = tipster.stream.take(10)
	  
	  for (query <- queriesTokens)
	  {
		  val priorityQ = new PriorityQueue[(String, Double)]()(Ordering.by(_ordering))
		  docs.foreach(d => priorityQ.enqueue((d.name, _getScore(_stemTokens(d.tokens), query))))
		  
		  val result = priorityQ.take(resultNumber)
		  
		  println("results for \""+query+"\" : "+result.mkString(", "))
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
		
		prt("tfs : " + tfs.mkString(", "))
		prt("qtfs : " + qtfs.mkString(", "))
		prt(numTermsInCommon)
		
		val docEuclideanLen = tfs.map{case(a, b) => b * b}.sum.toDouble		
		val queryLen = queryTerms.length.toDouble
		val termOverlap = qtfs.sum / (docEuclideanLen * queryLen)
		
		numTermsInCommon + termOverlap
	}
	
	private def _stemTokens(list: List[String]) : List[String] = 
	{
	  list.map(t => t.toLowerCase()).map(PorterStemmer.stem(_))
	}
	
	private def _getTopics =
	{
	  val lines = Source.fromFile(TOPIC_PATH).getLines
	  val topicsTitle = lines.filter(l => l.contains("<title>")).map(l => l.split(":")(1).trim.toLowerCase)
	  val topicsNumber = lines.filter(l => l.contains("<num>")).map(l => l.split(":")(1).trim.toInt)
	  
	  topicsTitle.zip(topicsNumber).toList
	}
	
	private def _ordering(row: (String, Double)) = row._2
	
	val isDebugMode: Boolean = false
	
	def prt(s : Any)
	{
	  if(!isDebugMode) return
	  println(s)
	}
}