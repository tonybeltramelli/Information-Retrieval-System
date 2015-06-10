package com.tonybeltramelli.desktop.core.perf

import scala.collection.Seq
import scala.math.max
import ch.ethz.dal.tinyir.lectures.PrecisionRecall

class PrecisionRecallAssessor[A](ranked: Seq[A], relev: Set[A]) extends PrecisionRecall[A](ranked: Seq[A], relev: Set[A])
{
  override def precAt(recall: Double, interpolated: Boolean = false) =
  {
    assert(recall >= 0.0 && recall <= 1.0)
    val n = max(1, recall2num(recall))

    if (interpolated) {
      if (iprecs.length > 0) iprecs(n - 1) else 0
    } else {
      if (precs.length > 0) precs(n - 1) else 0
    }
  }
}

object PrecisionRecallAssessor
{
  def evaluate[A](retriev: Set[A], relev: Set[A]) =
  {
    PrecisionRecall.evaluate(retriev, relev)
  }
}