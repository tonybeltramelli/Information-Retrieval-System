package com.tonybeltramelli.desktop.util

import scala.collection.mutable.{Map => MutMap}

import com.github.aztek.porterstemmer.PorterStemmer

class Helper {
}

object Helper {
  val ROOT_PATH = "data/tipster/"
  val ZIP_PATH = ROOT_PATH + "/zips"
  val QRELS_PATH = ROOT_PATH + "/qrels"
  val TOPIC_PATH = ROOT_PATH + "/topics"
  
  val RESULT_NUMBER = 100
  var TOKEN_MAX_SIZE = 100000

  val IS_DEBUG_MODE: Boolean = false
  
  private var _i = 0
  private var _time : Long = System.nanoTime()

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
}