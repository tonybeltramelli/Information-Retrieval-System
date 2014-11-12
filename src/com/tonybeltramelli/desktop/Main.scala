package com.tonybeltramelli.desktop

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator

object Main
{
  def main(args: Array[String])
  {
    if (args.length != 1) {
      System.exit(1)
    }

    new Main(args(0).toString)
  }
}

class Main
{
  def this(path: String)
  {
    this

    val iter = new ReutersCorpusIterator(path)

    val topicCounts = scala.collection.mutable.Map[String, Int]()
    var count = 0;
    while (iter.hasNext) {
      val doc = iter.next
      topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))
      count += 1
    }

    for ((t, c) <- topicCounts)
      println(t + ": " + c + " documents")

    println(count + " docs in corpus")
  }
}