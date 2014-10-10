package ch.ethz.dal.tinyir.indexing

// result returned by index lookups
trait Result[T] extends Any {
  def id : Int
  def matches(that: T) : Int                 
  def isMatch(that: T) = matches(that)==0
  def matched(that: T) : T    
}

abstract class InvertedIndex[Res <% Result[Res]]  {
  def results (term: String) : List[Res] 
  def results (terms: Seq[String]) : List[Res] = {
    val resultLists      = terms.map(term => results(term))
    val shortToLongLists = resultLists.sortWith( _.length < _.length) 
    shortToLongLists.reduceLeft( (l1,l2) => InvertedIndex.sIntersect(l1,l2) )
  }
}

object InvertedIndex {
  // generic list intersection (does not require sorted lists)
  private def unsortedIntersect [A<% Result[A]](l1: List[A], l2: List[A]) = l1.intersect(l2)

  // optimized list intersection for sorted posting lists 
  // uses "matches" and "matched" methods to work for all posting types
  def sIntersect[A <% Result[A]] (l1: List[A], l2: List[A]) : List[A] = {	
    @annotation.tailrec 	
    def iter (l1: List[A], l2: List[A], result: List[A]) : List[A] = {
	  if (l1.isEmpty || l2.isEmpty) 
	    result.reverse
	  else (l1.head matches l2.head) match {
	    case n if n>0 => iter(l1, l2.tail,result)  // advance list l2
	    case n if n<0 => iter(l1.tail, l2,result)  // advance list l1
	    case _        => iter(l1.tail, l2.tail, (l1.head matched l2.head)::result)	      
	  }
	}    
    iter(l1,l2,Nil)      
  }
}
