package com.tonybeltramelli.desktop.core.classifier

class BinaryLinearClassifier
{
  private var _theta: Map[String, Double] = Map()

  def train(documentFeatures: Map[String, Double], isRelated: Boolean)
  {
    _theta = _theta ++ _update(_theta, documentFeatures, isRelated)
  }

  def getProb(documentFeatures: Map[String, Double]) = _sigmoid(_theta, documentFeatures)
		  
  private def _update(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) =
  {
    _combine(_gradientTheta(theta, documentFeatures, isRelated), theta)
  }

  private def _gradientTheta(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) =
  {
    val chain = if(isRelated) 1 - _sigmoid(theta, documentFeatures) else -_sigmoid(theta, documentFeatures)

    _scalarMultiplication(documentFeatures, chain)
  }

  private def _sigmoid(theta: Map[String, Double], documentFeatures: Map[String, Double]) =
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

  private def _combine(vector1: Map[String, Double], vector2: Map[String, Double]) =
  {
    var result: Map[String, Double] = Map()
    
    for(key <- (vector1.keySet))
    {
      result = result + (key -> (vector2.getOrElse(key, 0.0) + vector1(key)))
    }
    
    result
  }
}