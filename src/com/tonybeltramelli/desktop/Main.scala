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
  private var _classifier : AClassifier = null
  private val _parser : Parser = new Parser
  
  def this(rootPath: String, classifierNumber: Int)
  {
    this
    
    Helper.setRootPath(rootPath)
    
    classifierNumber match
    {
      case 1 => _classifier = new LogisticRegression
      case 2 => _classifier = new NaiveBayes
      case 3 => _classifier = new SupportVectorMachines
    }

    Helper.time
    println("parse training set...")

    _parser.parse(Helper.TRAIN, train)
    
    Helper.time
    println("parse testing set...")

    _parser.parse(Helper.TEST_WITH_LABELS, test)
    
    println("script done")
	Helper.time
  }
  
  def train
  {
    _classifier.train(_parser.doc.name, _parser.doc.tokens, _parser.doc.topics)
  }
  
  def test
  {
    println(_parser.doc.name + " -> " + _classifier.apply(_parser.doc.tokens) + " " + _parser.doc.topics)
  }
}