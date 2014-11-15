package com.tonybeltramelli.desktop

import com.tonybeltramelli.desktop.util.Helper
import com.tonybeltramelli.desktop.core.parser.Parser
import com.tonybeltramelli.desktop.core.classifier.AClassifier
import com.tonybeltramelli.desktop.core.classifier.LogisticRegression
import com.tonybeltramelli.desktop.core.classifier.NaiveBayes
import com.tonybeltramelli.desktop.core.classifier.SupportVectorMachines

object Main
{
  def main(args: Array[String])
  {
    if (args.length != 2) {
      System.exit(1)
    }

    new Main(args(0).toString, args(1).toInt)
  }
}

class Main
{
  def this(rootPath: String, classifierNumber: Int)
  {
    this
    
    Helper.setRootPath(rootPath)
    
    var classifier : AClassifier = null
    
    classifierNumber match
    {
      case 1 => classifier = new LogisticRegression
      case 2 => classifier = new NaiveBayes
      case 3 => classifier = new SupportVectorMachines
    }
    
    val parser : Parser = new Parser

    Helper.time
    println("parse training set...")
    
    parser.parseTrainingSet(classifier)
    
    println("script done")
	Helper.time
  }
}