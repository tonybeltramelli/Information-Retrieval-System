package com.tonybeltramelli.desktop.core.classifier

import com.tonybeltramelli.desktop.core.classifier.multiclass.AMultinomialLinearClassifier
import com.tonybeltramelli.desktop.core.classifier.binary.SupportVectorMachinesBinary

class SupportVectorMachines extends AMultinomialLinearClassifier
{
  override def train(topic: String)
  {
    _train(topic, new SupportVectorMachinesBinary)
  }
}