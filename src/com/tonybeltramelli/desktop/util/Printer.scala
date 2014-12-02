package com.tonybeltramelli.desktop.util

import java.io.FileWriter
import java.io.BufferedWriter
import java.io.File
import java.io.BufferedReader
import java.io.Reader
import java.io.FileReader
import scala.io.Source

class Printer(path: String, classifierNumber: Int)
{
  private var _isFileClosed = true
  private var _fileWriter: FileWriter = null
  private var _bufferWriter: BufferedWriter = null

  def print(results: String, isLabeled: Boolean = false)
  {
    if (_isFileClosed) {
      _openFile(isLabeled)
    }

    _bufferWriter.write(results)
  }
  
  def save = _closeFile

  private def _openFile(isLabeled: Boolean)
  {
    val file = new File(_getFilePath(isLabeled))
    file.getParentFile.mkdirs
    
    _fileWriter = new FileWriter(file)
    _bufferWriter = new BufferedWriter(_fileWriter)

    _isFileClosed = false
  }
  
  private def _getFilePath(isLabeled: Boolean) = 
  {
    var c = ""
    classifierNumber match {
      case 1 => c = "nb"
      case 2 => c = "lr"
      case 3 => c = "svm"
    }

    path.replace("{M}", if (isLabeled) "l" else "u").replace("{C}", c)
  }

  private def _closeFile
  {
    _bufferWriter.close
    _fileWriter.close

    _isFileClosed = true
  }

  def prepend(data: String, isLabeled: Boolean)
  {
    val lines = Source.fromFile(_getFilePath(isLabeled)).mkString
    
    _openFile(isLabeled)
    _bufferWriter.write(data)
    _bufferWriter.write(lines)
    _closeFile
  }
}