package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import com.tonybeltramelli.desktop.core.classifier.multiclass.AClassifier

class NaiveBayes extends AClassifier
{
  private val _classesProb : MutMap[String, Double] = MutMap() //class name -> probability
  private val _wordClassProb : MutMap[String, Double] = MutMap() //class name - term -> probability
  
  override def train(topic: String)
  {
    /*
    _classesProb += topic -> _getProbClass(topic)
    
    for(docIndex <- _classesToDoc(topic).par)
    {
      val doc = _documents(docIndex)
      
      for(term <- doc._1)
      {
        val word = term._1
        
        _wordClassProb.getOrElseUpdate(_getProbWordKey(word, topic), _getProbWordClass(word, topic))
      }      
    }*/
  }
  
  private def _getProbClass(className: String) = Math.log(_classesToDoc(className).size / _documents.size.toDouble)
  
  private def _getProbWordClass(word: String, className: String) =
  {
    var sumTf = 0
    var sumDocSize = 0
    
    //Laplace smoothing
    val alpha = 1
    
    for(docIndex <- _classesToDoc(className).par)
    {
      val doc = _documents(docIndex)
      
      sumTf += doc._1.getOrElse(word, 0) + alpha
      sumDocSize += doc._2 + (alpha * _vocabularySize)
    }
    
    Math.log(sumTf / sumDocSize.toDouble)
  }
  
  override def apply(tokens: List[String]) =
  {
    var max = Double.MinValue 
    var result = ""
    
    for(classToDoc <- _classesToDoc.par)
    {
      var prob = _getProbClass(classToDoc._1)
      
      for(term <- tokens)
      {
        //prob += _wordClassProb.getOrElse(_getProbWordKey(term, classToDoc._1), 0.0)
        //prob += _getProbWordClass(term, classToDoc._1)
        prob += _inverseFreq.getOrElse(term, 0.0)
      }
      
      if(prob > max)
      {
        result = classToDoc._1
        max = prob
      }
    }
    
    Set(result)
  }
  
  private def _getProbWordKey(word: String, className: String) = className + "-" + word
  
  private var _cacheVocabularySize = 0
  
  private def _vocabularySize = 
  {  
    if(_cacheVocabularySize == 0)
    {
      _cacheVocabularySize = _documents.flatMap(d => d._2._1).map(f => f._1).size
    }
    
    _cacheVocabularySize
  }
}