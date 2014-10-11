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
	  //val queryTerms = Tokenizer.tokenize(query)
	  
	  println("queries : "+queries.mkString(", "))
	  println("documents : "+tipster.length)
	  
	  var length : Long = 0 
	  var tokens : Long = 0
	  for (doc <- tipster.stream.take(1000)) { 
		  length += doc.content.length          
		  tokens += doc.tokens.length
	  }
	  
	  println("Final number of characters = " + length)
	  println("Final number of tokens = " + tokens) 
	  
	  /*
	  val zipDocStream = new ZipDirStream("data/tipster/zips")
	  println("stream with "+zipDocStream.stream.length+" documents")
	  
	  val zipExplorer = new ZipExplorer("data/tipster/zips")
	  
	  println(zipExplorer.all.length)
	  println(zipExplorer.all.mkString("\n"))*/
	}
}