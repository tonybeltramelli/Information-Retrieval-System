package com.tonybeltramelli.desktop.core.classifier

class BinaryLinearClassifier
{
  private var _theta: Map[String, Double] = Map()

  def train(documentFeatures: Map[String, Double], isRelated: Boolean)
  {
    //_theta = _theta ++ _update(_theta, documentFeatures, isRelated)
    _theta = _theta ++ _gradientTheta(_theta, documentFeatures, isRelated)
  }

  def getProb(documentFeatures: Map[String, Double]) = _logistic(_theta, documentFeatures)
		  
  /*
  private def _update(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) =
  {
    _vectorAddition(_gradientTheta(theta, documentFeatures, isRelated), theta)
  }*/

  private def _gradientTheta(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) =
  {
    var chain = 0.0

    isRelated match {
      case true => chain = 1 - _logistic(theta, documentFeatures)
      case false => chain = -_logistic(theta, documentFeatures)
    }

    _scalarMultiplication(documentFeatures, chain)
  }

  private def _logistic(theta: Map[String, Double], documentFeatures: Map[String, Double]) =
  {
    1.0 / (1.0 + Math.exp(-_scalarProduct(documentFeatures, theta)))
  }

  private def _scalarProduct(vector1: Map[String, Double], vector2: Map[String, Double]) =
  {
    vector1.map(v => v._2 * vector2.getOrElse(v._1, 0.0)).sum
  }

  private def _scalarMultiplication(vector: Map[String, Double], scalar: Double) =
  {
    vector.mapValues(v => v * scalar)
  }

  private def _vectorAddition(vector1: Map[String, Double], vector2: Map[String, Double]) =
  {
    /*
    vector1.zip(vector2).map(v => v._1 + v._2)
    
    for(key <- (vector1.keySet)){
      vector2(key) = vector2.getOrElseUpdate(key, 0.0) + vector1(key)
    }*/
  }
}