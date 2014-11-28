package com.tonybeltramelli.desktop.util

object UMath
{
  def log2(x: Double) = Math.log10(x) / Math.log10(2.0)
  
  def normalize(rangeMin: Double, rangeMax: Double, x: Double, xMin: Double, xMax: Double) =
  {
    rangeMin + (((x - xMin) * (rangeMax - rangeMin)) / (xMax - xMin))
  }
}