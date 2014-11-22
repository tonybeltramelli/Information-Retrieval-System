package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import scala.collection.mutable.ListBuffer
import com.tonybeltramelli.desktop.util.Helper

trait AClassifier
{
  private val _TERM_CUT_SIZE = 50
  
  protected val _classesToDoc : MutMap[String, List[Int]] = MutMap() //class name -> document indexes
  protected val _documents : MutMap[Int, (Map[String, Int], Int)] = MutMap() //document index -> ((term -> tfs), size)
	
  protected var _documentCounter = 0
  
  protected var _inverseFreq : MutMap[String, Double] = MutMap() //term -> tf-idf
  
  def preprocess(tokens: List[String], classCodes : Set[String])
  {
    val tf = _getTermFreq(tokens).toSeq.sortBy(-_._2).take(_TERM_CUT_SIZE).toMap
    _documents += _documentCounter -> (tf, tokens.length)
    
    for(c <- classCodes)
    {
      val cl = _classesToDoc.getOrElseUpdate(c, List[Int]())  
      _classesToDoc.update(c, cl :+ _documentCounter)      
    }
    
    _documentCounter += 1
  }
  
  def trainAll
  {
    _computeTermFreqInverseDocumentFreq
    
    for(classToDoc <- _classesToDoc.par)
    {
      train(classToDoc._1)
    }
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
  
  private def _computeTermFreqInverseDocumentFreq
  {
    Helper.time("compute tf-idf")
    
    for(d <- _documents.map(_._2._1))
    {
      for(t <- d)
      {
	      val v = _inverseFreq.getOrElse(t._1, -1.0)
	      
	      if(v >= 0) _inverseFreq.update(t._1, t._2 + v) else _inverseFreq += t._1 -> t._2
      }
    }
    
    _inverseFreq = _inverseFreq.map(f => f._1 -> (Math.log(_documentCounter) - Math.log(f._2)))
  }
}