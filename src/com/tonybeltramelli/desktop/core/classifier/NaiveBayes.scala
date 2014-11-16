package com.tonybeltramelli.desktop.core.classifier

import com.tonybeltramelli.desktop.util.Helper

class NaiveBayes extends AClassifier
{  
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
    
    for(docName <- _classesToDoc(className))
    {
      sumTf += _documents(docName)._1.getOrElse(word, 0) + alpha
      sumDocSize += _documents(docName)._2 + (alpha * _documents(docName)._1.size)
    }
    
    sumTf / sumDocSize.toDouble
  }
  
  override def apply(document: List[String]) =
  {
    var max = 0.0
    var result = ""
    
    for(classToDoc <- _classesToDoc)
    {
      var prob = getProbClass(classToDoc._1)
      
      for(term <- Helper.stemTokens(document))
      {
        prob += getProbWordClass(term, classToDoc._1)
      }
      
      if(prob > max)
      {
        result = classToDoc._1
        max = prob
      }
    }
    
    result
  }
}