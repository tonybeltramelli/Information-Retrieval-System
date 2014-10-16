package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.Helper
import ch.ethz.dal.tinyir.processing.XMLDocument
import com.tonybeltramelli.desktop.core.QueryProcessor
import com.tonybeltramelli.desktop.core.scoring.AScoring
import com.tonybeltramelli.desktop.core.scoring.TermBasedScoring
import com.tonybeltramelli.desktop.core.scoring.LanguageBasedScoring
import collection.mutable.{Map => MutMap}
import com.github.aztek.porterstemmer.PorterStemmer

class Task(stream: Stream[XMLDocument], queriesTokens: List[(List[String], Int)], topics: List[(String, Int)], useLanguageModel: Boolean) extends Runnable {
	def run()
	{
	  Helper.time
	  println("stemming documents...")
	  
	  val collection = stream.map(doc => (doc.name, _stemTokens(doc.tokens)))
	  
	  var qp : QueryProcessor = null
	  
	  Helper.time
	  println("building frequencies...")
	  
	  /*
	  val scoringModel: AScoring = if(!useLanguageModel) new TermBasedScoring(collection) else new LanguageBasedScoring(collection)
	  
	  Helper.time
	  println("processing...")
	  
	  for(query <- queriesTokens)
	  {
	    qp = null
	    qp = new QueryProcessor(query, collection, topics, scoringModel)
	  }
	  
	  Helper.time*/
	}
	
	private val _stemStore : MutMap[String, String] = MutMap()
	
	private def _stemTokens(list: List[String]) : List[String] = 
	{
	  list.map(t => t.toLowerCase()).map(v => _stemStore.getOrElseUpdate(v, PorterStemmer.stem(v)))
	}
}