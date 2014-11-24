package com.tonybeltramelli.desktop.core.classifier

import com.tonybeltramelli.desktop.core.classifier.binary.SupportVectorMachineBinary
import com.tonybeltramelli.desktop.core.classifier.multiclass.AMultinomialLinearClassifier

class SupportVectorMachines extends AMultinomialLinearClassifier
{
  override def train(topic: String)
  {
    _train(topic, new SupportVectorMachineBinary)
  }
}