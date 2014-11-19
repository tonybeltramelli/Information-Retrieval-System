package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.Helper
import com.tonybeltramelli.desktop.core.parser.Parser
import com.tonybeltramelli.desktop.core.classifier.AClassifier
import com.tonybeltramelli.desktop.core.classifier.LogisticRegression
import com.tonybeltramelli.desktop.core.classifier.NaiveBayes
import com.tonybeltramelli.desktop.core.classifier.SupportVectorMachines
import scala.collection.mutable.ListBuffer
import com.tonybeltramelli.desktop.core.perf.Relevance

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
  private val _relevance : Relevance = new Relevance
  private var _results : String = ""
  
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
    println("parse labelled testing set...")

    _relevance.reset
    
    _parser.parse(Helper.TEST_WITH_LABELS, labelledTest)
        
    Helper.printToFile(_results, classifierNumber, true)
    
    println("total relevance : " + _relevance.totalPrecision + " " + _relevance.totalRecall + " " + _relevance.totalF1Score)
    
    Helper.time
    println("parse unlabelled testing set...")
    
    _results = ""
    _parser.parse(Helper.TEST_WITHOUT_LABELS , unlabelledTest)
        
    Helper.printToFile(_results, classifierNumber, false)
    
    println("script done")
	Helper.time
  }
  
  def train
  {
    _classifier.train(_parser.doc.name, _parser.doc.tokens, _parser.doc.topics)
  }
  
  def labelledTest
  {
    val retrieved = _classifier.apply(_parser.doc.tokens)
    val expected = _parser.doc.topics
    
    val relevance = _relevance.assess(retrieved, expected)
    
    val res = relevance._1 + " " + relevance._2 + " " + relevance._3 + "\n" + _parser.doc.name + " " + retrieved.mkString(" ") + "\n"
    _results += res
  }
  
  def unlabelledTest
  {
    _results += _parser.doc.name + " " + _classifier.apply(_parser.doc.tokens).mkString(" ") + "\n"
  }
}