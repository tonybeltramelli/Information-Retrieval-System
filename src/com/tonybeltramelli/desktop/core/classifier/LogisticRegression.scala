package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import scala.collection.mutable.ListBuffer

class LogisticRegression extends AClassifier
{
  private val _classifiers: MutMap[String, BinaryClassifierLR] = MutMap() //class name -> binary classifier
  private val _THRESHOLD = 0.9
  
  override def train(topic: String)
  {
    val bc = new BinaryClassifierLR
    
    for(docIndex <- _classesToDoc(topic))
    {
      val doc = _documents(docIndex)
      
      bc.feed(doc._1.map(f => _inverseFreq(f._1)).toArray)
    }
   
    _classifiers += topic -> bc
  }
  
  override def apply(tokens: List[String]) =
  {
    val documentFeatures = _getTermFreq(tokens).map(f => f._2.toDouble).toArray
    
    val results = _classifiers.map(bc => (bc._1, bc._2.getProb(documentFeatures))).filter(_._2 >= _THRESHOLD).toSeq.sortWith(_._2 > _._2)
    
    results.map(_._1).toSet
  }
  
  class BinaryClassifierLR // binary linear classifier
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