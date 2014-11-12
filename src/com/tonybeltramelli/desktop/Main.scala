package com.tonybeltramelli.desktop

import scala.io.Source

import com.tonybeltramelli.desktop.core.perf.PerformanceAssessor
import com.tonybeltramelli.desktop.core.scoring.AScoring
import com.tonybeltramelli.desktop.core.scoring.LanguageBasedScoring
import com.tonybeltramelli.desktop.core.scoring.TermBasedScoring
import com.tonybeltramelli.desktop.util.Helper

import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer

object Main {
	def main(args: Array[String])
	{
	  if(args.length != 4)
	  {
		println("Usage: \n")
		println(" <root path> <use language model> <only last 10 queries> <document number> <queries>(optional)\n")
		println(" <root path> : String, path to the tipster data folder\n")
		println(" <use language model> : Boolean, true = language-based model / false = term-based model\n")
		println(" <only last 10 queries> : Boolean, true = only use the last test queries / false = use all the provided topics queries\n")
		println(" <document number> : Int, number of document in the stream to process, -1 for all\n")
		println(" <queries> : String, possibility to input custom queries such as \"query1\" \"query2\" ... \"queryN\" / if not defined the topics queries are used as input")
		
		System.exit(1)
	  }
	  
	  new Main(args(0).toString, args(1).toBoolean, args(2).toBoolean, args(3).toInt, args.slice(4, args.length).toList)
	}
}

class Main
{	
	def this(rootPath: String, useLanguageModel: Boolean, onlyUseLastQueries: Boolean, documentNumber: Int, queries: List[String])
	{
	  this
	  
	  Helper.setRootPath(rootPath)
	  
	  var qu = queries
	  var topics : List[(String, Int)] = null
	  
	  if(queries.length == 0)
	  {
		  Helper.time
		  println("get topics...")
	    
		  topics = _getTopics
		  
		  if(onlyUseLastQueries) topics = topics.takeRight(10)
		  
		  qu = topics.map(_._1)
	  }
	  
	  println("search for " + qu.length + " queries : " + qu.mkString(", "))
	  
	  Helper.time
	  println("tokenize and stem queries...")
	  
	  val queriesTokens = qu.map(q => Tokenizer.tokenize(q)).map(t => Helper.stemTokens(t).groupBy(identity).map(_._1).toList).zipWithIndex	 
	  
	  Helper.time
	  println("get collection...")
	  
	  val collection = new TipsterStream(Helper.getPath(Helper.ZIP_PATH)).stream.take(if(documentNumber < 0) Int.MaxValue else documentNumber)
	  
	  Helper.time
	  println("start processing...")
	  
	  var step = 0

	  val scoringModel: AScoring = if(!useLanguageModel) new TermBasedScoring else new LanguageBasedScoring
	  
	  for(doc <- collection)
	  {
	    scoringModel.feed(doc.name, Helper.stemTokens(doc.tokens), queriesTokens) 
	    
	    if(step > 0 && step % 1000 == 0)
	    {
	      Helper.time
	      println(step + " documents processed")
	    }
	    
	    step += 1
	  }  
	  
	  Helper.time
	  println("gather results")
	  
	  val results = Helper.flipDimensions(scoringModel.getNames.map(n => (n, queriesTokens.map(q => (q._2, scoringModel.getScore(scoringModel.get(n), q._1))))))
	  				.map(q => q._1 -> q._2.toList.sortBy(res => -res._2).take(Helper.RESULT_NUMBER))
	  				
	  Helper.time
	  println("assess performances")
	  
	  if(!onlyUseLastQueries)
	  {
		  val perfAssessor = new PerformanceAssessor
		  
		  for(q <- results)
		  {
		    perfAssessor.assessPerformance(topics(q._1)._2.toString, q._2.toList)
		  }
	
		  println("MAP : "+perfAssessor.getMeanAveragePrecision)
	  }else{
		  Helper.printToFile(results, topics, useLanguageModel)	    
	  }
	  
	  println("script done")
	  Helper.time
	}
	
	private def _getTopics : List[(String, Int)] =
	{
	  val topicsTitle = Source.fromFile(Helper.getPath(Helper.TOPIC_PATH)).getLines.filter(l => l.contains("<title>")).map(l => l.split(":")(1).trim.toLowerCase)
	  val topicsNumber = Source.fromFile(Helper.getPath(Helper.TOPIC_PATH)).getLines.filter(l => l.contains("<num>")).map(l => l.split(":")(1).trim.toInt)
	  
	  topicsTitle.zip(topicsNumber).toList
	}
}