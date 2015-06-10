package com.tonybeltramelli.desktop.core.classifier.binary

trait ABinaryLinearClassifier
{
  private var _theta: Map[String, Double] = Map()

  def train(documentFeatures: Map[String, Double], isRelated: Boolean)
  {
    _theta ++= _gradient(_theta, documentFeatures, isRelated)
  }

  def getProb(documentFeatures: Map[String, Double]) = _sigmoid(_theta, documentFeatures)
		  
  protected def _gradient(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) : Map[String, Double] =
  {
    //to override
    Map()
  }

  protected def _sigmoid(theta: Map[String, Double], documentFeatures: Map[String, Double]) =
  {
    1.0 / (1.0 + Math.exp(-_scalarProduct(documentFeatures, theta)))
  }

  protected def _scalarProduct(vector1: Map[String, Double], vector2: Map[String, Double]) =
  {
    vector1.map(v => v._2 * vector2.getOrElse(v._1, 0.0)).sum
  }

  protected def _scalarMultiplication(vector: Map[String, Double], scalar: Double) =
  {
    vector.mapValues(v => v * scalar)
  }

  protected def _combine(vector1: Map[String, Double], vector2: Map[String, Double]) =
  {
    vector1.map(v => v._1 -> (vector2.getOrElse(v._1, 0.0) + v._2))
  }
}