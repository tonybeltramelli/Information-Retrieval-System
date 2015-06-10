package com.tonybeltramelli.desktop.core.classifier

import com.tonybeltramelli.desktop.core.classifier.binary.LogisticRegressionBinary
import com.tonybeltramelli.desktop.core.classifier.multiclass.AMultinomialLinearClassifier

class LogisticRegression extends AMultinomialLinearClassifier
{
  override def train(topic: String)
  {
    _train(topic, new LogisticRegressionBinary)
  }
}