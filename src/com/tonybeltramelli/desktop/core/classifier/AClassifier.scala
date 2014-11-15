package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}

trait AClassifier
{
  protected var _tfss : MutMap[String, (Map[String, Int], Int)] = MutMap[String, (Map[String, Int], Int)]()
	
  protected var _cfs : MutMap[String, Double] = MutMap() 
  protected var _cfsSum : Double = 0.0
  
  var _documents : Map[String, (Map[String, Int], Set[String])] = Map() //documentName -> (termFreq, classCodes)
	
  def feed(documentName: String, document: List[String])
  {
    val tfs = _getTermFreq(document)
    val tfsSum = tfs.map(v => v._2).sum
    
    _cfsSum += tfsSum
	  
	_tfss += (documentName -> (tfs, tfsSum))
  }
  
  def train(documentName: String, content: List[String], classCodes : Set[String])
  {
    _documents += documentName -> (_getTermFreq(content), classCodes)
    //maybe change to classCodes -> Map[documentName, termFreq]
  }
	
  private def _getTermFreq(doc: List[String]) =
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