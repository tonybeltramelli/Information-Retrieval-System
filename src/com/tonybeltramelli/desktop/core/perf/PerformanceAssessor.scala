package com.tonybeltramelli.desktop.core.perf

import scala.collection.mutable.ListBuffer
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import com.tonybeltramelli.desktop.util.Helper

class PerformanceAssessor
{
  private val _precs = new ListBuffer[Double]()

  def assessPerformance(number: String, results: List[(String, Double)])
  {
    val judgements = new TipsterGroundTruth(Helper.getPath(Helper.QRELS_PATH)).judgements

    if (!judgements.contains(number)) return

    val relevant = judgements.get(number).get.toSet
    val retrieved = results.map(r => r._1)
    val precisionRecall = new PrecisionRecallAssessor(retrieved, relevant)

    val precision : Double = PrecisionRecallAssessor.evaluate(retrieved.toSet, relevant).precision
    val recall : Double = PrecisionRecallAssessor.evaluate(retrieved.toSet, relevant).recall
    
    val avg : Double = precisionRecall.precAt(recall, true)
    _precs += avg

    Helper.debug("query " + number)
    Helper.debug("	precision " + precision + ", recall : " + recall)
    Helper.debug("	average precision at recall " + recall + " = " + avg)
    
    println("query " + number + ", average precision : " + avg)
  }
  
  def getMeanAveragePrecision : Double =
  {
    _precs.sum / _precs.length
  }
}