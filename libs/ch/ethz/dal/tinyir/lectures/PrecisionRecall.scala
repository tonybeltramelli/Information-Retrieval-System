
package ch.ethz.dal.tinyir.lectures

import collection.Seq
import util.Random
import math.{min,max}

class PrecisionRecall[A] (ranked: Seq[A], relev: Set[A]) {

  // total number of relevant items  
  val num = relev.size

  // indices in result list at which relevant items occur
  val relevIdx = ranked.zipWithIndex.filter{ case (r,_) => relev(r) }.map(_._2).toArray
  
  // precision values at index positions relevIdx
  val precs = relevIdx.zipWithIndex.map{case(rnk,rel)=> (rel+1)/(rnk+1).toDouble}
    
  // interpolation of precision to all recall levels 
  val iprecs = precs.scanRight(0.0)( (a,b) => Math.max(a,b)).dropRight(1)
  
  // number of results to reach recall level 
  private def recall2num(recall: Double) = {
    assert(recall >=0.0 && recall <=1.0)
    min((recall * num).ceil.toInt,num) 
  }
    
  // precision at recall level 
  def precAt(recall: Double, interpolated: Boolean = false) = {
    assert(recall >=0.0 && recall <=1.0)
    val n = max(1,recall2num(recall))
    if (interpolated) iprecs(n-1)
    else precs(n-1)
  } 
}

object PrecisionRecall {
  
  case class PrecRec (precision: Double, recall: Double) {
    def mkstr: String = "P = " + precision + ", R = " + recall
  }

  def evaluate[A] (retriev: Set[A], relev: Set[A]) = { 
    val truePos = (retriev & relev).size
    PrecRec(
        precision = truePos.toDouble / retriev.size.toDouble,
        recall    = truePos.toDouble / relev.size.toDouble
        )
  }
    
  def main(args: Array[String]) = {    
    {
      val relevant   = Set(3,6,7,8,9)
      val retrieved  = Set(1,2,3,6) 
	  println(PrecisionRecall.evaluate(retrieved, relevant).mkstr)
    }

    {
      val relevant = Set(3,7,9,15,19)
      val ranked = Random.shuffle((0 to 19).toList)

      val pr = new PrecisionRecall(ranked,relevant)
      println(pr.relevIdx.mkString(" "))
      println(pr.precs.mkString(" "))
      println(pr.iprecs.mkString(" "))
      
      val recall = 0.65
      println("Precision (non interp. ) at " + recall +" = " + pr.precAt(recall,false))
      println("Precision (interpolated) at " + recall +" = " + pr.precAt(recall,true))
    }
  }  
}

/*
class PrecisionRecallCurve (num: Int) {

  var prvalues = new Array[] 
  
  def evaluate[A] (ranked: Iterable[A], relev: Set[A]) = {
    val nrelev = relev.size.toDouble
    val binary = ranked.map(e => if (relev(e)) 1 else 0)
    val cummul = binary.scanLeft(0)(_+_).drop(1).map(_/nrelev)
    
  }  
     */
