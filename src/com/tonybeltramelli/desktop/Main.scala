package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.ZipExplorer
import ch.ethz.dal.tinyir.io.ZipDirStream
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer

object Main {
	def main(args: Array[String])
	{
	  new Main(args)
	}
}

class Main
{
	def this(queries: Array[String])
	{
	  this()
	  
	  val tipster = new TipsterStream("data/tipster/zips")
	  val queryTerms = Tokenizer.tokenize(queries(0).toLowerCase())
	  
	  println("queries : "+queries.mkString(", "))
	  println("documents : "+tipster.length)
	  
	  var length : Long = 0 
	  var tokens : Long = 0
	  for (doc <- tipster.stream.take(100)) { 
		  length += doc.content.length   
		  tokens += doc.tokens.length
		  
		  val score = _getScore(doc.tokens.map(t => t.toLowerCase()), queryTerms)
	  }
	  
	  println("final number of characters : " + length)
	  println("final number of tokens : " + tokens) 
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
		
		println("tfs : " + tfs.mkString(", "))
		println("qtfs : " + qtfs.mkString(", "))
		println(numTermsInCommon+"\n")
		
		//val docEuLen = tfs.mapValues{case(x, y) => y*y}.sum.toDouble
		/*
		val queryLen = queryTerms.length.toDouble
		val termOverlap = qtfs.sum / (docEuLen * queryLen)
		
		numTermsInCommon + termOverlap*/
		0.0
	}
}