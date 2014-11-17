package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import com.tonybeltramelli.desktop.util.Helper
import scala.collection.mutable.ListBuffer

trait AClassifier
{
  protected var _tfss : MutMap[String, (Map[String, Int], Int)] = MutMap[String, (Map[String, Int], Int)]()
	
  protected var _cfs : MutMap[String, Double] = MutMap() 
  protected var _cfsSum : Double = 0.0
  
  protected var _classesToDoc : MutMap[String, Set[String]] = MutMap() // className -> documentNames
  protected var _documents : MutMap[String, (Map[String, Int], Int)] = MutMap() // documentName -> (tfs, size)
	
  /*def feed(documentName: String, document: List[String])
  {
    val tfs = _getTermFreq(document)
    val tfsSum = tfs.map(v => v._2).sum
    
    _cfsSum += tfsSum
	  
	_tfss += (documentName -> (tfs, tfsSum))
  }*/
  
  def train(documentName: String, tokens: List[String], classCodes : Set[String])
  {
    for(c <- classCodes)
    {
      val cl = _classesToDoc.getOrElseUpdate(c, Set[String]())  
      _classesToDoc.update(c, cl + documentName)      
    }
    
    val content = Helper.stemTokens(tokens)
    _documents.getOrElseUpdate(documentName, (_getTermFreq(content), content.length))
  }
  
  def countVocabulary
  {  
    println(_documents.flatMap(d => d._2._1).map(f => f._1).size)
  }
  
  def apply(document: List[String]) =
  {
    //to be overridden
    ""
  }
	
  protected def _getTermFreq(doc: List[String]) =
  {
    doc.groupBy(identity).mapValues(l => l.length)
  }
	
  private def _getCollectionFreq(collection: Stream[(String, List[String])]) =
  {
    _getTermFreq(collection.flatMap(d => d._2).toList)
  }
  
  private def _getDocumentFreq(collection: Stream[(String, List[String])]) =
  {
    
  }
	
  def get = _tfss
  def getNames = {_tfss.map(f =>  f._1)}
}