package com.tonybeltramelli.desktop.core.classifier.multiclass

import scala.collection.mutable.{Map => MutMap}
import scala.util.Random
import com.tonybeltramelli.desktop.core.classifier.binary.ABinaryLinearClassifier

trait AMultinomialLinearClassifier extends AClassifier
{
  private val _classifiers: MutMap[String, ABinaryLinearClassifier] = MutMap() //class name -> binary classifier
  private val _THRESHOLD = 0.5
  
  protected def _train(topic: String, bc: ABinaryLinearClassifier)
  {
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
    val documentFeatures = tokens.map(f => f -> _inverseFreq.getOrElse(f, 0.0)).filter(_._2 > 0.0).toMap
    
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
}