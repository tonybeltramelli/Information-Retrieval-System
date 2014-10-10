package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.ZipExplorer
import ch.ethz.dal.tinyir.io.ZipDirStream
import ch.ethz.dal.tinyir.io.TipsterStream

object Main {
	def main(args: Array[String])
	{
	  new Main(args)
	}
}

class Main
{
	//private val _zipDocStream = new ZipDirStream("data/tipster/zips")

	def this(queries: Array[String])
	{
	  this()
	  
	  val t1 = System.nanoTime()
	  
	  val tipster = new TipsterStream("data/tipster/zips")  
	  
	  val t2 = System.nanoTime()
	  
	  println("stream with "+tipster.length+" documents")
	  
	  var length : Long = 0 
    var tokens : Long = 0
    for (doc <- tipster.stream.take(10000)) { 
      length += doc.content.length          
      tokens += doc.tokens.length
    }
    println("Final number of characters = " + length)
    println("Final number of tokens     = " + tokens)
    
	  println("time : "+(t2 - t1)/1000000000.0+"s")
	  
	  /*
	  println("queries : "+queries.mkString(", "))
	  println("stream with "+_getStream.length+" documents")
	  
	  val zipExplorer = new ZipExplorer("data/tipster/zips")
	  
	  println(zipExplorer.all.length)
	  println(zipExplorer.all.mkString("\n"))*/
	}
	/*
	private def _getStream = {
	  _zipDocStream.stream
	}*/
}