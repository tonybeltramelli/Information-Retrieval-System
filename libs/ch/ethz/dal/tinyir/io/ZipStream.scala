package ch.ethz.dal.tinyir.io

import util.Try
import util.Failure
import util.Success
import io.Source
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import scala.collection.JavaConversions._

// create a document stream out of all files in a given zip file 
//
class ZipStream (path: String, extension: String = "") 
extends DirStream (path,extension) {

  override def length : Int = openZipFile(path) match {
    case Success(z) =>  z.entries.filter(e => isValid(e.getName)).length
    case _ => 0
  }
    
  override def stream : Stream[InputStream] = openZipFile(path) match {
    case Failure(zip) => Stream[InputStream]() 
    case Success(zip) => {
      val entries = zip.entries.toList
      val filtered = entries.filter(e => isValid(e.getName))
      val sorted  = filtered.sortBy(_.getName).toStream
      sorted.map(zip.getInputStream(_))
    }
  }
  
  private def openZipFile (file: String) = Try(new ZipFile(file)) 
  private def isValid(name: String) = name.endsWith(extension)
  protected def orderBy(fname: String) : String = orderByName(fname)
  protected def orderByName(fname: String) = fname
}

object ZipStream { 
    def main(args: Array[String]) {
      val path = "/Users/thofmann/Data/Reuters_RCV_1and2/zips/19960823.zip"
      val docs = new ZipStream (path)
      println("Reading from zip file = " + path)
      println("Number of documents = " + docs.length)
    }
}
