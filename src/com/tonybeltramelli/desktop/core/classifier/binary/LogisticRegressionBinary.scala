package com.tonybeltramelli.desktop.core.classifier.binary

class LogisticRegressionBinary extends ABinaryLinearClassifier
{
  protected override def _gradient(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) : Map[String, Double] =
  {
    val lossChain = if(isRelated) 1 - _sigmoid(theta, documentFeatures) else -_sigmoid(theta, documentFeatures)
    
    val grad = _scalarMultiplication(documentFeatures, lossChain)
    
    _combine(grad, theta)
  }
}