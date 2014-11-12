package ch.ethz.dal.tinyir.io

import scala.io.Source
import util.Try
import util.Success
import util.Failure
import util.Properties
import io.Codec
import java.nio.charset.Charset
import java.io.InputStream
import java.io.File

// find all valid files in a directory and return them as a stream 
// main method: stream
//
class DirStream (dirpath: String, extension: String = "") 
extends DocStream {

  def stream: Stream[InputStream] = sortedNames.map(fn => DocStream.getStream(fn)).toStream 
  def length = validNames.length    

  private def sortedNames = validNames.sorted(DirStream.FileOrder.orderingByLex) 
  private def validNames = new File(dirpath).listFiles.map(path(_)).filter(valid(_))
  private def valid(fn: String): Boolean = fn.endsWith(extension)
  private def path (f: File): String = Try(f.getAbsolutePath).getOrElse("")  
}

object DirStream {

  object FileOrder {
    val orderingByLex : Ordering[String] = Ordering.by(identity)
    val orderingByNum : Ordering[String] = Ordering.by(e => fname2Int(e))  
    private def fname2Int(n: String) : Long = Try(n.filter(_.isDigit).toLong).getOrElse(0)
  }
  
  def main(args: Array[String]) {
    val path = "/Users/thofmann/Data/Reuters_RCV_1and2/zips/19960821"
    val docs = new DirStream (path, ".xml")
    println("Reading from directory = " + path)
    println("Number of files in directory = " + docs.length)
 }
}