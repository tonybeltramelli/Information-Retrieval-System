package com.tonybeltramelli.desktop.core.classifier

import scala.collection.mutable.{Map => MutMap}
import com.tonybeltramelli.desktop.util.Helper
import scala.collection.mutable.ListBuffer

trait AClassifier
{
  protected var _tfss : MutMap[String, (Map[String, Int], Int)] = MutMap[String, (Map[String, Int], Int)]()
	
  protected var _cfs : MutMap[String, Double] = MutMap() 
  protected var _cfsSum : Double = 0.0
  
  protected val _classesToDoc : MutMap[String, List[Int]] = MutMap() // className -> documentIndexes
  protected val _documents : MutMap[Int, (Map[String, Int], Int)] = MutMap() // documentIndex -> (tfs, size)
	
  protected var _documentCounter = 0
  
  /*def feed(documentName: String, document: List[String])
  {
    val tfs = _getTermFreq(document)
    val tfsSum = tfs.map(v => v._2).sum
    
    _cfsSum += tfsSum
	  
	_tfss += (documentName -> (tfs, tfsSum))
  }*/
  
  def train(documentName: String, tokens: List[String], classCodes : Set[String])
  {
    //to be overridden
  }
  
  def apply(document: List[String]) =
  {
    //to be overridden
    Set("")
  }
	
  protected def _getTermFreq(doc: List[String]) =
  {
    doc.groupBy(identity).mapValues(l => l.length)
  }
	
  private def _getCollectionFreq(collection: Stream[(String, List[String])]) =
  {
    _getTermFreq(collection.flatMap(d => d._2).toList)
  }
	
  def get = _tfss
  def getNames = {_tfss.map(f =>  f._1)}
}