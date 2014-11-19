package com.tonybeltramelli.desktop.core.classifier

import com.tonybeltramelli.desktop.util.Helper

class NaiveBayes extends AClassifier
{
  override def train(documentName: String, tokens: List[String], classCodes : Set[String])
  {
    val content = Helper.stemTokens(tokens)
    _documents += _documentCounter -> (_getTermFreq(content), content.length)
    
    for(c <- classCodes)
    {
      val cl = _classesToDoc.getOrElseUpdate(c, List[Int]())  
      _classesToDoc.update(c, cl :+ _documentCounter)      
    }
    
    _documentCounter += 1
  }
  
  def getProbClass(className: String) =
  {
    _classesToDoc(className).size / _documents.size.toDouble
  }
  
  def getProbWordClass(word: String, className: String) =
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
  
  override def apply(document: List[String]) =
  {
    var max = Double.MinValue 
    var result = ""
    
    for(classToDoc <- _classesToDoc)
    {
      var prob = Math.log(getProbClass(classToDoc._1))
      
      for(term <- Helper.stemTokens(document))
      {
        prob += Math.log(getProbWordClass(term, classToDoc._1))
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