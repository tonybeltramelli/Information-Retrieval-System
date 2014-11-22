package com.tonybeltramelli.desktop.core.perf

class Relevance
{
  private var _documentNumber = 0.0

  private var _totalPrecision = 0.0
  private var _totalRecall = 0.0
  private var _totalF1Score = 0.0
  
  def reset
  {
    _totalPrecision = 0.0
    _totalRecall = 0.0
    _totalF1Score = 0.0
    
    _documentNumber = 0.0
  }
  
  def assess(retrievedTopics: Set[String], expecedTopics: Set[String]) =
  {
    val truePositive = (retrievedTopics & expecedTopics).size
    val falsePositive = (retrievedTopics -- expecedTopics).size
    val falseNegative = (expecedTopics -- retrievedTopics).size
    val trueNegative = retrievedTopics.size - truePositive - falsePositive - falseNegative
    
    val precision = truePositive / _denominator((truePositive + falsePositive).toDouble)
    val recall = truePositive / _denominator((truePositive + falseNegative).toDouble)
    
    val f1Score = 2 * (precision * recall) / _denominator(precision + recall)
    
    _totalPrecision += precision
    _totalRecall += recall
    _totalF1Score += f1Score
    
    _documentNumber += 1
    
    (precision, recall, f1Score)
  }
  
  private def _denominator(d: Double) =
  {
    if(d <= 0.0) 1.0 else d
  }
  
  def totalAverageRelevance = (_totalPrecision / _documentNumber, _totalRecall / _documentNumber, _totalF1Score / _documentNumber)
}