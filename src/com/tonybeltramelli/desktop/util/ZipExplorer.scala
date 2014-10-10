package com.tonybeltramelli.desktop.util

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import scala.collection.mutable.ListBuffer
import scala.util.Try
import java.util.zip.ZipInputStream

class ZipExplorer(path: String) {
  
  val all = _getFilesNames(path).filter(_.endsWith(".zip")).map { case (x) => _getFilesFromZip(x) }.flatten
  
  private def _getFilesFromZip(path: String): List[String] =
  {
    val entries = new ZipFile(path).entries
    val a = ListBuffer[String]()

    while (entries.hasMoreElements()) {
      val entry: ZipEntry = entries.nextElement()
      a += entry.getName()
    }

    a.toList
  }

  private def _getFilesNames(path: String) = new File(path).listFiles.map(_getPath(_))
  
  private def _getPath(f: File): String = Try(f.getPath()).getOrElse("")
}