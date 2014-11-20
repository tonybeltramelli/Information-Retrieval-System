package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import com.tonybeltramelli.desktop.util.Helper
import scala.collection.mutable.ListBuffer

class LogisticRegression extends AClassifier
{
  private val _documentFreq : MutMap[String, Int] = MutMap()
  private var _theta: Array[Double] = Array(0.0)
  
  override def train(documentName: String, tokens: List[String], classCodes : Set[String])
  {
    val content = Helper.stemTokens(tokens)
    
    //_updateDocumentFreq(content)
    
    val documentFeatures = _getDocumentFeatures(content)
    
    _theta = _theta ++ _update(_theta, documentFeatures, true)
    
    _documentCounter += 1
  }
  
  override def apply(document: List[String]) =
  {
    _logistic(_theta, _getDocumentFeatures(Helper.stemTokens(document)))
    Set("")
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