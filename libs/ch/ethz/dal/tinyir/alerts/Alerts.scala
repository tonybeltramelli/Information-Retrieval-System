package ch.ethz.dal.tinyir.alerts

import scala.collection.mutable.PriorityQueue
import scala.math.Ordering.Implicits._

case class ScoredResult (title : String, score: Double)

class Alerts (q: String, n: Int) {

  val query = new Query(q)

  
  // score a document and try to add to results
  def process(title: String, doc: List[String]) : Boolean = {
    val score = query.score(doc)
    add(ScoredResult(title,score))
  }
  
  // get top n results (or m<n, if not enough docs processed)
  def results = heap.toList.sortBy(res => -res.score)    

    // heap and operations on heap
  private val heap = new PriorityQueue[ScoredResult]()(Ordering.by(score))
  private def score (res: ScoredResult) = -res.score 
  private def add(res: ScoredResult) : Boolean = {    
    if (heap.size < n)  { // heap not full
      heap += res
      true
    } else if (heap.head.score < res.score) {
        heap.dequeue
        heap += res
        true
      } else false
  }
  

}

