package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import scala.collection.mutable.ListBuffer
import com.tonybeltramelli.desktop.util.Helper

trait AClassifier
{
  private val _TERM_CUT_SIZE = 50
  
  protected val _classesToDoc : MutMap[String, List[Int]] = MutMap() // className -> documentIndexes
  protected val _documents : MutMap[Int, (Map[String, Int], Int)] = MutMap() // documentIndex -> (tfs, size)
	
  protected var _documentCounter = 0
  
  protected var _documentFreq : MutMap[String, Double] = MutMap()
  
  def preprocess(tokens: List[String], classCodes : Set[String])
  {
    val tf = _getTermFreq(tokens)
    _documents += _documentCounter -> (tf, tokens.length)
    _updateDocumentFreq(tf)
    
    for(c <- classCodes)
    {
      val cl = _classesToDoc.getOrElseUpdate(c, List[Int]())  
      _classesToDoc.update(c, cl :+ _documentCounter)      
    }
    
    _documentCounter += 1
  }
  
  def trainAll
  {
    for(classToDoc <- _classesToDoc.par)
    {
      train(classToDoc._1)
    }
    
    _documentFreq = _getInverseDocumentFreq(_documentFreq , _documentCounter)
  }
  
  def train(topic: String)
  {
    //to be overridden
  }
  
  def apply(tokens: List[String]) =
  {
    //to be overridden
    Set("")
  }
	
  protected def _getTermFreq(doc: List[String]) =
  {
    doc.groupBy(identity).mapValues(l => l.length)
  }
	
  private def _getCollectionFreq(collection: Stream[(String, List[String])]) =
  {
    _getTermFreq(collection.flatMap(d => d._2).toList)
  }
  
  private def _getInverseDocumentFreq(df: MutMap[String, Double], documentNumber: Int) =
  {
    df.map(f => f._1 -> (Math.log(documentNumber) - Math.log(f._2)))
  }
  
  private def _updateDocumentFreq(tf: Map[String, Int])
  {
    for(t <- tf.toSeq.sortBy(-_._2).take(_TERM_CUT_SIZE))
    {
      val v = _documentFreq.getOrElse(t._1, -1.0)
      
      if(v >= 0) _documentFreq.update(t._1, t._2 + v) else _documentFreq += t._1 -> t._2
    }
  }
}