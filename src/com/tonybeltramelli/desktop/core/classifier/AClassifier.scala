package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import scala.collection.mutable.ListBuffer

trait AClassifier
{
  protected val _classesToDoc : MutMap[String, List[Int]] = MutMap() // className -> documentIndexes
  protected val _documents : MutMap[Int, (Map[String, Int], Int)] = MutMap() // documentIndex -> (tfs, size)
	
  protected var _documentCounter = 0
  
  def preprocess(tokens: List[String], classCodes : Set[String])
  {
    _documents += _documentCounter -> (_getTermFreq(tokens), tokens.length)
    
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
}