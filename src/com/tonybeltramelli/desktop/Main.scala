package com.tonybeltramelli.desktop

import scala.io.Source
import com.github.aztek.porterstemmer.PorterStemmer
import com.tonybeltramelli.desktop.util.Helper
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer
import com.tonybeltramelli.desktop.core.scoring.AScoring
import com.tonybeltramelli.desktop.core.scoring.TermBasedScoring
import com.tonybeltramelli.desktop.core.scoring.LanguageBasedScoring
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth

object Main {
	def main(args: Array[String])
	{
	  new Main(args(0).toBoolean, args.slice(1, args.length).toList)
	}
}

class Main
{	
	def this(useLanguageModel: Boolean, queries: List[String])
	{
	  this
	  
	  var qu = queries
	  var topics : List[(String, Int)] = null
	  
	  if(queries.length == 0)
	  {
		  Helper.time
		  println("get topics...")
	    
		  topics = _getTopics
		  qu = topics.map(_._1).take(3)
	  }
	  
	  Helper.time
	  println("tokenize and stem queries...")
	  
	  val queriesTokens = qu.map(q => Tokenizer.tokenize(q)).map(t => Helper.stemTokens(t).groupBy(identity).map(_._1).toList).zipWithIndex	 
	  
	  Helper.time
	  println("get collection...")
	  
	  val collection = new TipsterStream(Helper.ZIP_PATH).stream.take(4)  

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
	  
	  for(n <- scoringModel.getNames)
	  {
	    val results = queriesTokens.map(q => (q._2, scoringModel.getScore(scoringModel.get(n), q._1)))
	    
	    for(r <- results)
	    {
	      println(n + ", query : " + r._1 + ", result : " + r._2)
	    }
	    
	    //doc.name -> (query.name : score)
	    //_._1 -> _._2._1 : _._2._2
	    //query.name -> (doc.name : score)
	    //_._2._1 -> _._1 : _._2._2
	  }	  
	  
	  val f = Helper.flipDimensions(scoringModel.getNames.map(n => (n, queriesTokens.map(q => (q._2, scoringModel.getScore(scoringModel.get(n), q._1))))))
	  
	  println(f.mkString(", "))
	  
	  println("script done")
	  Helper.time
	}
	
	private def _assessPerformance(topics: List[(String, Int)])
	{
	  val judgements = new TipsterGroundTruth(Helper.QRELS_PATH).judgements
		
	  for(topic <- topics)
	  {
		  if(!judgements.contains(topic._2.toString)) return
		  
		  
	  }  
	}
	
	private def _getTopics : List[(String, Int)] =
	{
	  val topicsTitle = Source.fromFile(Helper.TOPIC_PATH).getLines.filter(l => l.contains("<title>")).map(l => l.split(":")(1).trim.toLowerCase)
	  val topicsNumber = Source.fromFile(Helper.TOPIC_PATH).getLines.filter(l => l.contains("<num>")).map(l => l.split(":")(1).trim.toInt)
	  
	  topicsTitle.zip(topicsNumber).toList
	}
}