package com.tonybeltramelli.desktop.performance

import collection.Seq
import math.{ min, max }

class PrecisionRecall[A](ranked: Seq[A], relev: Set[A])
{ 
  val num = relev.size

  // indices in result list at which relevant items occur
  val relevIdx = ranked.zipWithIndex.filter { case (r, _) => relev(r) }.map(_._2).toArray

  // precision values at index positions relevIdx
  val precs = relevIdx.zipWithIndex.map { case (rnk, rel) => (rel + 1) / (rnk + 1).toDouble }

  // interpolation of precision to all recall levels 
  val iprecs = precs.scanRight(0.0)((a, b) => Math.max(a, b)).dropRight(1)

  // number of results to reach recall level 
  private def recall2num(recall: Double) = {
    assert(recall >= 0.0 && recall <= 1.0)
    min((recall * num).ceil.toInt, num)
  }

  // precision at recall level 
  def precAt(recall: Double, interpolated: Boolean = false) = {
    assert(recall >= 0.0 && recall <= 1.0)
    val n = max(1, recall2num(recall))

    if (interpolated) {
      if (iprecs.length > 0) iprecs(n - 1) else 0
    } else {
      if (precs.length > 0) precs(n - 1) else 0
    }
  }
}

object PrecisionRecall {
  case class PrecRec(precision: Double, recall: Double) {
    def mkstr: String = "P = " + precision + ", R = " + recall
  }

  def evaluate[A](retriev: Set[A], relev: Set[A]) = {
    val truePos = (retriev & relev).size
    PrecRec(
      precision = truePos.toDouble / retriev.size.toDouble,
      recall = truePos.toDouble / relev.size.toDouble)
  }
}