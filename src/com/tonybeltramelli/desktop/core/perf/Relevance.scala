package com.tonybeltramelli.desktop.core.perf

class Relevance
{
  var totalPrecision = 0.0
  var totalRecall = 0.0
  var totalF1Score = 0.0
  
  def reset
  {
    totalPrecision = 0.0
    totalRecall = 0.0
    totalF1Score = 0.0
  }
  
  def assess(retrievedTopics: Set[String], expecedTopics: Set[String]) =
  {
    val truePositive = (retrievedTopics & expecedTopics).size
    val falsePositive = (retrievedTopics -- expecedTopics).size
    val falseNegative = (expecedTopics -- retrievedTopics).size
    val trueNegative = retrievedTopics.size - truePositive - falsePositive - falseNegative
    
    val precision = truePositive / (truePositive + falsePositive).toDouble
    val recall = truePositive / (truePositive + falseNegative).toDouble
    
    val f1Score = 2 * (precision * recall) / (if(precision + recall == 0) 1 else (precision + recall))
    
    totalPrecision += precision
    totalRecall += recall
    totalF1Score += f1Score
    
    (precision, recall, f1Score)
  }
}