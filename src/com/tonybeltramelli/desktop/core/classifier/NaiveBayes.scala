package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import com.tonybeltramelli.desktop.core.classifier.multiclass.AClassifier
import com.tonybeltramelli.desktop.util.UMath

class NaiveBayes extends AClassifier
{
  private val _termDocPerClassProb : MutMap[String, (Map[String, Int], Int)] = MutMap() //class name -> ((term -> tfs sum), sizes sum)

  override def train(topic: String)
  {
    val docTfSum = _classesToDoc(topic).flatMap(di => _documents(di)._1).groupBy(identity).map(f => f._1._1 -> f._2.map(_._2).sum)
    val docSizeSum = _classesToDoc(topic).map(di => _documents(di)._2).sum

    _termDocPerClassProb += topic -> ((docTfSum, docSizeSum))
  }
  
  private def _getProbClass(className: String) = UMath.log2(_classesToDoc(className).size / _documents.size.toDouble)
  
  private def _getProbWordClass(word: String, className: String) =
  {
    val termProb = _termDocPerClassProb(className)
    
    //Laplace smoothing
    val alpha = 1.0
    
    val sumTf = termProb._1.getOrElse(word, 0) + alpha
    val sumDocSize = termProb._2 + (alpha * _vocabularySize)
    
    UMath.log2(sumTf / sumDocSize.toDouble)
  }
  
  override def apply(tokens: List[String]) =
  {
    val terms = _getTermFreq(tokens).map(_._1)
    var probs : MutMap[String, Double] = MutMap()
    
    for(classToDoc <- _classesToDoc)
    {
      var prob = _getProbClass(classToDoc._1)
      
      for(term <- terms) prob += _getProbWordClass(term, classToDoc._1)
      
      probs += classToDoc._1 -> prob
    }
    
    _getNormalizedAndPrunedResults(probs, 0.95, 5)
  }
   
  private var _cacheVocabularySize = 0
  
  private def _vocabularySize = 
  {  
    if(_cacheVocabularySize == 0)
    {
      _cacheVocabularySize = _documents.flatMap(d => d._2._1).map(f => f._1).size
    }
    
    _cacheVocabularySize
  }
}