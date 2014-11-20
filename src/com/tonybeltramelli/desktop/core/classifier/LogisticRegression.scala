package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import scala.collection.mutable.ListBuffer

class LogisticRegression extends AClassifier
{
  private val _classifiers: MutMap[String, BinaryClassifierLR] = MutMap() //class name -> binary classifier
  private val _documentFreq : MutMap[String, Int] = MutMap()
  
  override def train(topic: String)
  {
    val bc = new BinaryClassifierLR
    
    for(docIndex <- _classesToDoc(topic))
    {
      val doc = _documents(docIndex)
      
      bc.feed(doc._1.map(f => f._2.toDouble).toArray)
    }
   
    _classifiers += topic -> bc
  }
  
  def trains(documentName: String, tokens: List[String], classCodes : Set[String])
  {
    //_updateDocumentFreq(tokens)
    
    val documentFeatures = _getDocumentFeatures(tokens)
    
    _documentCounter += 1
  }
  
  override def apply(tokens: List[String]) =
  {
    var results : Set[String] = Set()
    val documentFeatures = _getDocumentFeatures(tokens)
    
    for(bc <- _classifiers)
    {
      val p = bc._2.getProb(documentFeatures)
      
      if(p >= 0.5) results = results + bc._1
    }
    
    results
  }
  
  private def _getDocumentFeatures(document: List[String]) = _getTermFreq(document).map(f => f._2.toDouble).toArray
  
  private def _getInverseDocumentFreq(df: Map[String, Int], documentNumber: Int) =
  {
    df.mapValues(Math.log(documentNumber) - Math.log(_))
  }
  
  private def _updateDocumentFreq(document: List[String]) =
  {
    _documentFreq ++= document.distinct.map(t => t -> (1 + _documentFreq.getOrElse(t, 0)))
  }
  
  class BinaryClassifierLR
  {
    private var _theta: Array[Double] = Array(0.0)
    
    def feed(documentFeatures: Array[Double])
    {
      _theta = _theta ++ _update(_theta, documentFeatures, true)
    }
    
    def getProb(documentFeatures: Array[Double]) =
    {
      _logistic(_theta, documentFeatures)
    }
  
    private def _update(theta: Array[Double], documentFeatures: Array[Double], isRelated: Boolean) =
    {
      _vectorAddition(_gradientTheta(theta, documentFeatures, isRelated), theta)
    }
  
    private def _gradientTheta(theta: Array[Double], documentFeatures: Array[Double], isRelated: Boolean) =
    {
      var chain = 0.0
    
      isRelated match
      {
        case true => chain = 1 - _logistic(theta, documentFeatures)
        case false => chain = - _logistic(theta, documentFeatures)
      }
    
      _scalarMultiplication(documentFeatures, chain)
    }
  
    private def _logistic(theta: Array[Double], documentFeatures: Array[Double]) =
    {
      1.0 / (1.0 + Math.exp(- _scalarProduct(documentFeatures, theta)))
    }
  
    private def _scalarProduct(vector1: Array[Double], vector2: Array[Double]) =
    {
      vector1.zip(vector2).map(v => v._1 * v._2).reduce(_ + _)
      //vector1.map(v => v._2 * vector2.getOrElse(v._1, 0.0)).sum
    }
  
    private def _scalarMultiplication(vector: Array[Double], scalar: Double) =
    {
      vector.map(v => v * scalar)
    }
  
    private def _vectorAddition(vector1: Array[Double], vector2: Array[Double]) =
    {
      vector1.zip(vector2).map(v => v._1 + v._2)
    }
  }
}