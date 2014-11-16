package com.tonybeltramelli.desktop.core.classifier

class NaiveBayes extends AClassifier {
  
  def getProbClass(className: String) =
  {
    _classToDoc(className).size / _documents.size.toDouble
  }
  
  def getProbWordClass(word: String, className: String) =
  {
    var sumTf = 0
    var sumDocSize = 0
    
    //Laplace smoothing
    val alpha = 1
    
    for(docName <- _classToDoc(className))
    {
      sumTf += _documents(docName)._1.getOrElse(word, alpha)
      sumDocSize += _documents(docName)._2 + (alpha * _documents(docName)._1.size)
    }
    
    sumTf / sumDocSize.toDouble
  }
}