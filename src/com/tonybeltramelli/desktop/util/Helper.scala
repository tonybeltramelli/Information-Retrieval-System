package com.tonybeltramelli.desktop.util

import com.github.aztek.porterstemmer.PorterStemmer

class Helper {
}

object Helper {
  val ROOT_PATH = "data/tipster/"
  val ZIP_PATH = ROOT_PATH + "/zips"
  val QRELS_PATH = ROOT_PATH + "/qrels"
  val TOPIC_PATH = ROOT_PATH + "/topics"
  val RESULT_NUMBER = 100

  val isDebugMode: Boolean = false

  def debug(s: Any) {
    if (!isDebugMode) return
    println(s)
  }
  
  def stemTokens(list: List[String]) : List[String] = 
  {
    list.map(t => t.toLowerCase()).map(PorterStemmer.stem(_))
  }
  
  def ordering(row: (String, Double)) = row._2
	
  def log2(x: Double) = Math.log10(x) / Math.log10(2.0)
}