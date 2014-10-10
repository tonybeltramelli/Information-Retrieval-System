package ch.ethz.dal.tinyir.alerts

import ch.ethz.dal.tinyir.processing.Tokenizer

class Query (query: String) {  
  val qterms = Tokenizer.tokenize(query).distinct
  val length = qterms.length

  def score (doc: List[String]) : Double = {
    val tfs : Map[String,Int]= doc.groupBy(identity).mapValues(l => l.length)
    val qtfs = qterms.flatMap(q => tfs.get(q))
    val numTermsInCommon = qtfs.length 
    val docLen = tfs.values.map(x => x*x).sum.toDouble  // Euclidian norm
    val queryLen = qterms.length .toDouble  
    val termOverlap = qtfs.sum.toDouble / (docLen * queryLen)
    
    // top ordering is by terms in common (and semantics)
    // integer range from 0...qterms.length
    // on top of this a tf-based overlap score in range [0;1[ is added
    numTermsInCommon + termOverlap
  }
}