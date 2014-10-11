package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.ZipExplorer
import ch.ethz.dal.tinyir.io.ZipDirStream
import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.processing.Tokenizer
import com.github.aztek.porterstemmer.PorterStemmer
import com.sun.xml.internal.bind.v2.TODO

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
	  this()
	  
	  val tipster = new TipsterStream("data/tipster/zips")
	  
	  val queriesTokens = _stemTokens(queries).map(q => Tokenizer.tokenize(q))
	  
	  prt("queries : "+queries.mkString(", "))
	  prt("documents : "+tipster.length)
	  
	  for (query <- queriesTokens)
	  {
		  //val docs = tipster.stream.take(100)
		  
		  //val scores = docs.map(doc => doc.name -> _getScore(_stemTokens(doc.tokens), query))
		  
		  for (doc <- tipster.stream.take(100))
		  { 			  
			  val score = queriesTokens.map(q => q -> _getScore(_stemTokens(doc.tokens), q))
			  
			  /*
			  for (query <- queriesTokens)
			  {
				  val score = _getScore(_stemTokens(doc.tokens), query)
				  prt("score : "+score+"\n")
			  }*/
			  
			  prt(score)
		  }
		  
		  //prt(scores)
	  }
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
	
	val isDebugMode: Boolean = true
	
	def prt(s : Any)
	{
	  if(!isDebugMode) return
	  println(s)
	}
}