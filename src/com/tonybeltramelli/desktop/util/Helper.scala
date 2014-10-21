package com.tonybeltramelli.desktop.util

import scala.collection.mutable.{Map => MutMap}
import com.github.aztek.porterstemmer.PorterStemmer
import scala.collection.mutable.Iterable
import java.io.File
import java.io.FileWriter

class Helper {
}

object Helper {
  val ZIP_PATH = "/zips"
  val QRELS_PATH = "/qrels"
  val TOPIC_PATH = "/topics"
    
  val OUTPUT_FILE = "/output/ranking-M-tony-beltramelli.run"
  
  val RESULT_NUMBER = 100
  var TOKEN_MAX_SIZE = 100000

  val IS_DEBUG_MODE: Boolean = false

  private var _rootPath = ""
  
  private var _i = 0
  private var _time : Long = System.nanoTime()
  
  def getPath(r: String ) : String = {
    _rootPath + r
  }
  
  def setRootPath(r: String) {
    _rootPath = r
  }

  def debug(s: Any) {
    if (!IS_DEBUG_MODE) return
    println(s)
  }
  
  private val _stemStore : MutMap[String, String] = MutMap()
  
  def stemTokens(list: List[String]) : List[String] = 
  {
    if(_stemStore.size > TOKEN_MAX_SIZE) _stemStore.clear
	  
    list.map(t => t.toLowerCase()).map(v => _stemStore.getOrElseUpdate(v, PorterStemmer.stem(v)))
  }
  
  def time {
    println("time "+_i+" : " + (System.nanoTime() - _time) / 1000000000.0 + " seconds")
    _i += 1
  }
	
  def log2(x: Double) = Math.log10(x) / Math.log10(2.0)
  
  def flipDimensions(original : Iterable[(String, List[(Int, Double)])]) : Map[Int, Iterable[(String, Double)]] =
  {
    val flatten = for {
      (s, v) <- original
      (i, d) <- v
    } yield (i, s, d)
	
    implicit class RichTuple2[A, B, C](t: (A, B, C)) {
      def tail: (B, C) = (t._2, t._3)
    }
	
    flatten.groupBy(_._1).mapValues(_.map(_.tail))
  }
  
  def printToFile(results : Map[Int, List[(String, Double)]], topics : List[(String, Int)], useLanguageModel : Boolean)
  {
    val file = new File(_rootPath + OUTPUT_FILE.replace('M', if(useLanguageModel) 'l' else 't'))
    file.getParentFile.mkdirs
    
    val fw = new FileWriter(file)
    
    results.foreach(r => r._2.zipWithIndex.foreach{case(l, i) => fw.write(topics(r._1)._2.toString + " " + (i + 1) + " " + l._1 + "\n")})
    
    fw.close
  }
}