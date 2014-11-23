package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import scala.collection.mutable.ListBuffer
import scala.util.Random

class LogisticRegression extends AClassifier
{
  private val _classifiers: MutMap[String, BinaryLinearClassifier] = MutMap() //class name -> binary classifier
  private val _THRESHOLD = 0.5
  
  override def train(topic: String)
  {
    val bc = new BinaryLinearClassifier
    
    /*
    for(cl <- _getRandomTopics(topic).par)
    {
      val isRelated = topic == cl._1
        
      for(docIndex <- _classesToDoc(cl._1).par)
      {
        val doc = _documents(docIndex)
      
        bc.train(doc._1.map(f => f._1 -> _inverseFreq(f._1)), isRelated)
      }
    }*/
    
    for(docIndex <- _getRandomDocuments(topic).par)
    {
      val doc = _documents(docIndex)
      val isRelated = doc._3.contains(topic)
      
      bc.train(doc._1.map(f => f._1 -> _inverseFreq(f._1)), isRelated)
    }
   
    _classifiers += topic -> bc
  }
  
  override def apply(tokens: List[String]) =
  {
    val documentFeatures = _getTermFreq(tokens).map(f => f._1 -> (f._2.toDouble + _inverseFreq.getOrElse(f._1, 0.0)))
      
    val results = _classifiers.map(bc => (bc._1, bc._2.getProb(documentFeatures))).filter(_._2 >= _THRESHOLD).toSeq.sortWith(_._2 > _._2)
    
    results.map(_._1).toSet
  }
  
  private def _getRandomDocuments(trueTopic: String) =
  {
    val random = new Random
    var documents = _classesToDoc(trueTopic)
    
    var i = documents.size * 3
    i = if(i > _documentCounter) _documentCounter else i
    
    while(i > 0)
    {
      documents = documents + random.nextInt(_documents.size)
      i -= 1
    }
    
    documents
  }
  
  private val _TOPIC_LIMIT = 5
  
  private def _getRandomTopics(trueTopic: String) =
  {
    val random = new Random
    val falseTopics = _classesToDoc.filter(_._1 != trueTopic).zipWithIndex.map(m => m._2 -> m._1)
    var result = MutMap(trueTopic -> _classesToDoc(trueTopic))
    
    var i = _TOPIC_LIMIT
    
    while(i > 0)
    {
      val pos = random.nextInt(falseTopics.size)
      
      if(!result.contains(falseTopics(pos)._1))
      {
        result += falseTopics(pos)
        i -= 1
      }
    }
    
    result
  }
}