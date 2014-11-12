package ch.ethz.dal.tinyir.indexing

import ch.ethz.dal.tinyir.processing.Document
import ch.ethz.dal.tinyir.processing.StringDocument

case class SimpleResult(val id: Int) extends AnyVal with Result[SimpleResult] {
  def matches(that: SimpleResult) = this.id compare that.id
  def matched(that: SimpleResult) = that
} 

class SimpleIndex (docs: Stream[Document]) 
extends InvertedIndex[SimpleResult] {
  
  case class SimplePosting(val id: Int) extends Ordered[SimplePosting] {
    def compare(that: SimplePosting) = this.id compare that.id
  }
  type PostList = List[SimplePosting]     
  val index : Map[String,PostList] = {
    val groupedTuples = postings(docs).groupBy(_.term)
    groupedTuples.mapValues(_.map(p => SimplePosting(p.doc)).distinct.sorted)
  }
  
  case class IdTuple(term: String, doc: Int) 
  private def postings (s: Stream[Document]): List[IdTuple] = 
    s.flatMap( d => d.tokens.map(token => IdTuple(token,d.ID)) ).toList
   
  def results (term: String) : List[SimpleResult] = 
    index.getOrElse(term,Nil).map( p => SimpleResult(p.id) )       		  
}

object SimpleIndex { 
  def main(args : Array[String]) = {
    val d1 = new StringDocument(1,"mr sherlock holmes who was usually very late")
    val d0 = new StringDocument(0,"i can tell a moriaty when i see one said holmes")  
    val stream : Stream[StringDocument] = List(d1,d0).toStream
    val idx = new SimpleIndex(stream)    
    idx.index.foreach{ case (d,lst) => println(d + ": " + lst.mkString(" "))}     
    val q = List("mr","holmes")
    println(q.mkString(" ") + " = " + idx.results(q).mkString(" "))
  }
}
  