package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import com.tonybeltramelli.desktop.util.Helper

class LogisticRegression extends AClassifier
{
  private val _documentFreq : MutMap[String, Int] = MutMap()
  
  override def train(documentName: String, tokens: List[String], classCodes : Set[String])
  {
    val content = Helper.stemTokens(tokens)
    
    _documentCounter += 1
  }
  
  private def _getInverseDocumentFreq(df: Map[String, Int], documentNumber: Int) =
  {
    df.mapValues(Math.log(documentNumber) - Math.log(_))
  }
  
  private def _updateDocumentFreq(document: List[String]) =
  {
    _documentFreq ++= document.distinct.map(t => t -> (1 + _documentFreq.getOrElse(t, 0)))
  }
}