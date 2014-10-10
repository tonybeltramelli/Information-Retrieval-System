package ch.ethz.dal.tinyir.indexing

import ch.ethz.dal.tinyir.processing.{Document,StringDocument}

case class FreqResult(val id: Int, val tf: List[Int]) extends Result[FreqResult] {
  def matches(that: FreqResult) = this.id compare that.id
  def matched(that: FreqResult) = FreqResult(id, this.tf ::: that.tf)
} 

class FreqIndex (docs: Stream[Document]) extends InvertedIndex[FreqResult] {

  
  case class FreqPosting(val id: Int, val freq: Int) extends Ordered[FreqPosting] {
    def compare(that: FreqPosting) = this.id compare that.id
  }
  type PostList = List[FreqPosting] 
  val index : Map[String,PostList] = {
    val groupedTuples = postings(docs).groupBy(_.term)
    groupedTuples.mapValues(_.map(tfT => FreqPosting(tfT.doc, tfT.count)).sorted)
  }

  case class TfTuple(term: String, doc: Int, count: Int) 
  private def postings (s: Stream[Document]): List[TfTuple] =
    s.flatMap( d => d.tokens.groupBy(identity)
        .map{ case (tk,lst) => TfTuple(tk, d.ID, lst.length) } ).toList
  
  override def results (term: String) : List[FreqResult] = 
    index.getOrElse(term,Nil).map(p => FreqResult(p.id, List(p.freq)))
}

object FreqIndex { 
  def main(args : Array[String]) = {
    val d1 = new StringDocument(1,"mr sherlock holmes who was usually very late")
    val d0 = new StringDocument(0,"i can tell a moriaty when i see one said holmes")  
    val stream : Stream[StringDocument] = List(d1,d0).toStream
    val idx = new FreqIndex(stream)    
    idx.index.foreach{ case (d,lst) => println(d + ": " + lst.mkString(" "))}     
    val q = List("a","i")
    println(q.mkString(" ") + " = " + idx.results(q).mkString(" "))
  }
}
