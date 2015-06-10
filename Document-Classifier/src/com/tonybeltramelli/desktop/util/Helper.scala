package com.tonybeltramelli.desktop.util

object Helper {
  val ZIP_PATH = "/data"
  val TEST_WITH_LABELS = "/test-with-labels"
  val TEST_WITHOUT_LABELS = "/test-without-labels"
  val TRAIN = "/train"
    
  val OUTPUT_FILE = "/output/classify-tony-beltramelli-{M}-{C}.run"
  
  val RESULT_NUMBER = 100

  val IS_DEBUG_MODE: Boolean = false

  private var _rootPath = ""
  
  private var _i = 0
  private var _time : Long = System.nanoTime()
  
  def getResource(r: String ) : String = {
    _rootPath + ZIP_PATH + r
  }
  
  def setRootPath(r: String) {
    _rootPath = r
  }

  def debug(s: Any) {
    if (!IS_DEBUG_MODE) return
    println(s)
  }
  
  def time(message: String)
  {
    println("time "+_i+" : " + (System.nanoTime() - _time) / 1000000000.0 + " seconds")
    println(message)
    _i += 1
  }
}