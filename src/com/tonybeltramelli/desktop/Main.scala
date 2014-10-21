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
	  if(args.length != 2)
	  {
		println("Usage: \n")
		println(" <root path> <use language model> <queries>(optional)\n")
		println(" <root path> : String, path to the tipster folder\n")
		println(" <use language model> : Boolean, true = language-based model / false = term-based model\n")
		println(" <queries> : String, \"query1\" \"query2\" ... \"queryN\" / if not defined topics queries processed")
		
		System.exit(1)
	  }
	  
	  new Main(args(0).toString, args(1).toBoolean, args.slice(2, args.length).toList)
	}
}

class Main
{	
	def this(rootPath: String, useLanguageModel: Boolean, queries: List[String])
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
		  qu = topics.map(_._1)
	  }
	  
	  Helper.time
	  println("tokenize and stem queries...")
	  
	  val queriesTokens = qu.map(q => Tokenizer.tokenize(q)).map(t => Helper.stemTokens(t).groupBy(identity).map(_._1).toList).zipWithIndex	 
	  
	  Helper.time
	  println("get collection...")
	  
	  val collection = new TipsterStream(Helper.getPath(Helper.ZIP_PATH)).stream.take(1000)  

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
	  
	  Helper.time
	  println("assess performances")
	  
	  step = 0
	  val perfAssessor = new PerformanceAssessor
	  
	  for(q <- results)
	  {
	    perfAssessor.assessPerformance(topics(q._1)._2.toString, q._2.toList)
	    
	    Helper.time
	    println(step + " done")
	    
	    step += 1
	  }
	  
	  println("script done, MAP : "+perfAssessor.getMeanAveragePrecision)
	  Helper.time
	}
	
	private def _getTopics : List[(String, Int)] =
	{
	  val topicsTitle = Source.fromFile(Helper.getPath(Helper.TOPIC_PATH)).getLines.filter(l => l.contains("<title>")).map(l => l.split(":")(1).trim.toLowerCase)
	  val topicsNumber = Source.fromFile(Helper.getPath(Helper.TOPIC_PATH)).getLines.filter(l => l.contains("<num>")).map(l => l.split(":")(1).trim.toInt)
	  
	  topicsTitle.zip(topicsNumber).toList
	}
}