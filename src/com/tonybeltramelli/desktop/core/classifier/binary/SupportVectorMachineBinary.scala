package com.tonybeltramelli.desktop.core.classifier.binary

class SupportVectorMachineBinary extends ABinaryLinearClassifier
{
  private var _step = 0.0
  
  protected override def _gradient(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) : Map[String, Double] =
  {
    _step += 1.0
    
    val lambda = 0.01
    val direction = if(isRelated) 1.0 else -1.0
    
    val thetaShrink = _scalarMultiplication(theta, 1 - 1.0 / _step)
    val margin = 1.0 - (direction * _scalarProduct(documentFeatures, theta))
    
    if(margin <= 0)
    {
      thetaShrink
    }else{
      _combine(thetaShrink, _scalarMultiplication(documentFeatures, (1.0 / (lambda * _step)) * direction))
    }
  }
}