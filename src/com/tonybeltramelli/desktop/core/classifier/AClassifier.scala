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
  
  protected var _documentFreq : Map[String, Int] = Map()
  
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
    
    println(_getInverseDocumentFreq(_documentFreq, _documentCounter).size)
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
  
  private def _getInverseDocumentFreq(df: Map[String, Int], documentNumber: Int) =
  {
    df.mapValues(Math.log(documentNumber) - Math.log(_))
  }
  
  private def _updateDocumentFreq(tf: Map[String, Int])
  {
    /*
     * for(t <- m2)
    {
      val v = m1.getOrElse(t._1, 0)
      
      if(v != 0) m1.update(t._1, t._2 + v) else m1 += t._1 -> t._2
    }
     */
    _documentFreq = (_documentFreq.toSeq ++ tf.toSeq.sortBy(-_._2).take(_TERM_CUT_SIZE)).groupBy(_._1).mapValues(_.map(_._2).reduce(_ + _))
  }
}