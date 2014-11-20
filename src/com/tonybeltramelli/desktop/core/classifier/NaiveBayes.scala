package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}

class NaiveBayes extends AClassifier
{
  protected val _classesProb : MutMap[String, Double] = MutMap() // className -> documentIndexes
  
  override def train(topic: String)
  {
    _classesProb += topic -> Math.log(_getProbClass(topic))
  }
  
  private def _getProbClass(className: String) = _classesToDoc(className).size / _documents.size.toDouble
  
  private val _probWordClassCache : MutMap[String, Double] = MutMap()
  private val _PROB_WORD_CLASS_MAX_SIZE = 1000
  
  private def _getProbWordClass(word: String, className: String) =
  {
    if(_probWordClassCache.size > _PROB_WORD_CLASS_MAX_SIZE) _probWordClassCache.clear
	  
    _probWordClassCache.getOrElseUpdate(word + "-" + className, _computeProbWordClass(word, className))
  }
  
  private def _computeProbWordClass(word: String, className: String) =
  {
    var sumTf = 0
    var sumDocSize = 0
    
    //Laplace smoothing
    val alpha = 1
    
    for(docIndex <- _classesToDoc(className))
    {
      val doc = _documents(docIndex)
      
      sumTf += doc._1.getOrElse(word, 0) + alpha
      sumDocSize += doc._2 + (alpha * _vocabularySize)
    }
    
    sumTf / sumDocSize.toDouble
  }
  
  override def apply(tokens: List[String]) =
  {
    var max = Double.MinValue 
    var result = ""
    
    for(classToDoc <- _classesToDoc.par)
    {
      var prob = _classesProb(classToDoc._1)
      
      for(term <- tokens)
      {
        prob += Math.log(_getProbWordClass(term, classToDoc._1))
      }
      
      if(prob > max)
      {
        result = classToDoc._1
        max = prob
      }
    }
    
    Set(result)
  }
  
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