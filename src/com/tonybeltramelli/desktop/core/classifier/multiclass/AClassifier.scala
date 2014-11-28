package com.tonybeltramelli.desktop.core.classifier.multiclass

import scala.collection.mutable.{Map => MutMap}
import com.tonybeltramelli.desktop.util.UMath
import scala.collection.mutable.ListBuffer
import com.tonybeltramelli.desktop.util.Helper

trait AClassifier
{
  private val _TERM_CUT_SIZE = 50
  
  protected val _classesToDoc : MutMap[String, Set[Int]] = MutMap() //class name -> document indexes
  protected val _documents : MutMap[Int, (Map[String, Int], Int, Set[String])] = MutMap() //document index -> ((term -> tfs), size, topics)
	
  protected var _documentCounter = 0
  
  protected var _inverseFreq : MutMap[String, Double] = MutMap() //term -> tf-idf
  
  def preprocess(tokens: List[String], classCodes : Set[String])
  {
    _documents += _documentCounter -> (_getTermFreq(tokens), tokens.length, classCodes)
    
    for(c <- classCodes)
    {
      val cl = _classesToDoc.getOrElseUpdate(c, Set[Int]())  
      _classesToDoc.update(c, cl + _documentCounter)
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
    //to override
  }
  
  def apply(tokens: List[String]) =
  {
    //to override
    Set("")
  }
  
  protected def _getNormalizedAndPrunedResults(res: MutMap[String, Double], threshold: Double, cut: Int) =
  {
    val result = res.toSeq.sortWith(_._2 > _._2)
    
    result.map(f => f._1 -> UMath.normalize(0, 1, f._2, result.last._2, result.head._2)).filter(_._2 >= threshold).take(cut).map(_._1).toSet
  }
	
  protected def _getTermFreq(doc: List[String]) =
  {
    doc.groupBy(identity).mapValues(l => l.length).toSeq.sortBy(-_._2).take(_TERM_CUT_SIZE).toMap
  }
  
  private def _computeTermFreqInverseDocumentFreq
  {
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