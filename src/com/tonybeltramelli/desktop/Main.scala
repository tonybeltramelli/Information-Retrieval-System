package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.Helper
import com.tonybeltramelli.desktop.core.parser.Parser
import com.tonybeltramelli.desktop.core.classifier.AClassifier
import com.tonybeltramelli.desktop.core.classifier.LogisticRegression
import com.tonybeltramelli.desktop.core.classifier.NaiveBayes
import com.tonybeltramelli.desktop.core.classifier.SupportVectorMachines
import scala.collection.mutable.ListBuffer
import com.tonybeltramelli.desktop.core.perf.Relevance
import com.tonybeltramelli.desktop.util.Printer

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
  private var _printer : Printer = null
  
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

    _printer = new Printer(Helper.getResource(Helper.OUTPUT_FILE), classifierNumber)

    _relevance.reset
    _parser.parse(Helper.TEST_WITH_LABELS, labelledTest)
        
    _printer.save
    _printer.prepend(_relevance.totalAverageRelevance._1 + " " + _relevance.totalAverageRelevance._2 + " " + _relevance.totalAverageRelevance._3 + "\n", true)
    
    println("total relevance : " + _relevance.totalAverageRelevance._1 + " " + _relevance.totalAverageRelevance._2 + " " + _relevance.totalAverageRelevance._3)
    
    Helper.time
    println("parse unlabelled testing set...")
    
    _parser.parse(Helper.TEST_WITHOUT_LABELS , unlabelledTest)
    
    _printer.save
        
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
    
    //println(_parser.doc.name + " : " + relevance._1 + " " + relevance._2 + " " + relevance._3)
    
    val results = _parser.doc.name + " " + retrieved.mkString(" ") + "\n"
    _printer.print(results, true)
  }
  
  def unlabelledTest
  {
    _printer.print(_parser.doc.name + " " + _classifier.apply(_parser.doc.tokens).mkString(" ") + "\n", false)
  }
}