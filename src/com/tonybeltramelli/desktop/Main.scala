package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.Helper
import com.tonybeltramelli.desktop.core.parser.Parser
import com.tonybeltramelli.desktop.core.classifier.AClassifier
import com.tonybeltramelli.desktop.core.classifier.LogisticRegression
import com.tonybeltramelli.desktop.core.classifier.NaiveBayes
import com.tonybeltramelli.desktop.core.classifier.SupportVectorMachines
import scala.collection.mutable.ListBuffer

object Main
{
  def main(args: Array[String])
  {
    if (args.length != 3)
    {
      println("Usage: \n")
      println(" <root path> <classifier> <document number>\n")
      println(" <root path> : String, path to the data folder\n")
      println(" <classifier> : Int, 1 => Logistic Regression / 2 => Naive Bayes / 3 => Support Vector Machines\n")
      println(" <document number> : Int, number of document to process, -1 for all\n")
      	
      System.exit(1)
    }

    new Main(args(0).toString, args(1).toInt, args(2).toInt)
  }
}

class Main
{
  private var _classifier : AClassifier = null
  private val _parser : Parser = new Parser
  
  def this(rootPath: String, classifierNumber: Int, documentNumber: Int)
  {
    this
    
    Helper.setRootPath(rootPath)
    
    classifierNumber match
    {
      case 1 => _classifier = new LogisticRegression
      case 2 => _classifier = new NaiveBayes
      case 3 => _classifier = new SupportVectorMachines
    }
    
    _parser.documentNumber = if(documentNumber != -1) documentNumber else Int.MaxValue
    
    Helper.time
    println("parse training set...")

    _parser.parse(Helper.TRAIN, train)
    
    Helper.time
    println("parse testing set...")

    _parser.parse(Helper.TEST_WITH_LABELS, test)
    
    println("script done")
	Helper.time
  }
  
  val vocabulary : ListBuffer[String] = ListBuffer[String]()
  val cache : ListBuffer[String] = ListBuffer[String]()
  val CACHE_SIZE = 100
  
  def countVoc
  {
    for(term <- Helper.stemTokens(_parser.doc.tokens))
    {
      if(cache.size > CACHE_SIZE) cache.clear
      
      if(!cache.contains(term))
      {
        cache += term
        vocabulary += term
      }
    }
  }
  
  def train
  {
    _classifier.train(_parser.doc.name, _parser.doc.tokens, _parser.doc.topics)
  }
  
  def test
  {
    //println(_parser.doc.name + " -> " + _classifier.apply(_parser.doc.tokens) + " " + _parser.doc.topics)
  }
}