package ch.ethz.dal.tinyir.alerts

import ch.ethz.dal.tinyir.io.TipsterStream
import ch.ethz.dal.tinyir.util.StopWatch
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import ch.ethz.dal.tinyir.lectures.PrecisionRecall

class AlertsTipster(q: String, n: Int) extends Alerts(q,n) 

object AlertsTipster {
  
  def main(args: Array[String]) {  
    val query = "Airbus Subsidies"
    val num = 100
    val alerts = new AlertsTipster(query,num)    
    val tipster = new TipsterStream("zips")
    
    val sw = new StopWatch; sw.start
    var iter = 0
    for (doc <- tipster.stream) {
      iter += 1
      alerts.process(doc.name, doc.tokens)
      if (iter % 20000 ==0) {
        println("Iteration = " + iter)
        alerts.results.foreach(println)    
      }  
    }
    sw.stop
    println("Stopped time = " + sw.stopped)
    alerts.results.foreach(println)  
    val rel = new TipsterGroundTruth("qrels").judgements.get("51").get.toSet
    val ret = alerts.results.map(r => r.title)
    val pr = new PrecisionRecall(ret,rel)
    println(pr.relevIdx.mkString(" "))
    println(pr.precs.mkString(" "))
    println(pr.iprecs.mkString(" "))
  }
  
}