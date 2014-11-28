package com.tonybeltramelli.desktop.core.classifier.binary

class SupportVectorMachineBinary extends ABinaryLinearClassifier
{
  private var _step = 0.0
  
  protected override def _gradient(theta: Map[String, Double], documentFeatures: Map[String, Double], isRelated: Boolean) : Map[String, Double] =
  {
    _step += 1.0
    
    val lambda = 1.0
    val direction = if(isRelated) 1 else -1
    
    /*
    val thetaShrink = _scalarMultiplication(theta, 1.0 - 1.0 / _step)
    val margin = 1.0 - direction * _scalarProduct(documentFeatures, theta)
    
    if(margin <= 0)
    {
      thetaShrink
    }else{
      _combine(_scalarMultiplication(documentFeatures, (1.0 / (lambda * _step)) * direction), theta ++ thetaShrink)
    }*/
    
    val thetaShrink = theta.mapValues(v => v * (1.0 - 1.0 / _step))
    val margin = 1.0 - direction * documentFeatures.map(v => v._2 * theta.getOrElse(v._1, 0.0)).sum
    
    if(margin <= 0)
    {
      thetaShrink
    }else{
      _theta = theta ++ thetaShrink
      documentFeatures.map(f => f._1 -> (_theta.getOrElse(f._1, 0.0) + f._2 * ((1.0 / (lambda * _step)) * direction)))
    }
  }
}