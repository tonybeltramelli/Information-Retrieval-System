package com.tonybeltramelli.desktop

import scala.io.Source

import com.tonybeltramelli.desktop.core.QueryProcessor
import com.tonybeltramelli.desktop.util.Helper

import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer

object Main {
	def main(args: Array[String])
	{
	  new Main(args.toList)
	}
}

class Main
{	
	def this(queries: List[String])
	{
	  this
	  
	  var qu = queries
	  var topics : List[(String, Int)] = null
	  
	  if(queries.length == 0)
	  {
		  topics = _getTopics
		  qu = topics.map(_._1)
	  }
	  
	  val time = System.nanoTime()
	  
	  val tipster = new TipsterStream(Helper.ZIP_PATH)	  
	  val queriesTokens = qu.map(q => Tokenizer.tokenize(q)).map(t => Helper.stemTokens(t)).zipWithIndex
	  
	  println("time 0 : " + (System.nanoTime() - time) / 1000000000.0 + " seconds")
	  
	  val docs = tipster.stream.take(1000)
	  var qp : QueryProcessor = null
	  
	  for(query <- queriesTokens)
	  {
	    qp = null
	    qp = new QueryProcessor(query, docs, topics)
	  }
	  
	  println("time 1 : " + (System.nanoTime() - time) / 1000000000.0 + " seconds")
	}
	
	private def _getTopics : List[(String, Int)] =
	{
	  val lines = Source.fromFile(Helper.TOPIC_PATH).getLines
	  val topicsTitle = lines.filter(l => l.contains("<title>")).map(l => l.split(":")(1).trim.toLowerCase)
	  val topicsNumber = lines.filter(l => l.contains("<num>")).map(l => l.split(":")(1).trim.toInt)
	  
	  topicsTitle.zip(topicsNumber).toList
	}
}