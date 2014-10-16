package com.tonybeltramelli.desktop

import scala.io.Source

import com.github.aztek.porterstemmer.PorterStemmer
import com.tonybeltramelli.desktop.core.QueryProcessor
import com.tonybeltramelli.desktop.core.scoring.AScoring
import com.tonybeltramelli.desktop.core.scoring.LanguageBasedScoring
import com.tonybeltramelli.desktop.core.scoring.TermBasedScoring
import com.tonybeltramelli.desktop.util.Helper

import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer

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
		  topics = _getTopics
		  qu = topics.map(_._1).take(2)
	  }
	  
	  val tipster = new TipsterStream(Helper.ZIP_PATH)	  
	  val queriesTokens = qu.map(q => Tokenizer.tokenize(q)).map(t => _stemTokens(t)).zipWithIndex
	  
	  Helper.time
	  println("stemming documents...")
	  
	  val documents = tipster.stream.take(10)
	  val collection = documents.map(doc => (doc.name, _stemTokens(doc.tokens)))
	  
	  var qp : QueryProcessor = null
	  
	  Helper.time
	  println("building collection frequency...")
	  
	  val scoringModel: AScoring = if(!useLanguageModel) new TermBasedScoring(collection) else new LanguageBasedScoring(collection)
	  
	  Helper.time
	  println("processing...")
	  
	  for(query <- queriesTokens)
	  {
	    qp = null
	    qp = new QueryProcessor(query, collection, topics, scoringModel)
	  }
	  
	  Helper.time
	}
	
	private def _getTopics : List[(String, Int)] =
	{
	  val topicsTitle = Source.fromFile(Helper.TOPIC_PATH).getLines.filter(l => l.contains("<title>")).map(l => l.split(":")(1).trim.toLowerCase)
	  val topicsNumber = Source.fromFile(Helper.TOPIC_PATH).getLines.filter(l => l.contains("<num>")).map(l => l.split(":")(1).trim.toInt)
	  
	  topicsTitle.zip(topicsNumber).toList
	}
	
	private val _stemStore : collection.mutable.Map[String, String] = collection.mutable.Map()
	
	private def _stemTokens(list: List[String]) : List[String] = 
	{
	  list.map(t => t.toLowerCase()).map(v => _stemStore.getOrElseUpdate(v, PorterStemmer.stem(v)))
	}
}