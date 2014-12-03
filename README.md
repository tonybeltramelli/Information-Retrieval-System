
Machine Learning Classifier
============================

Student project Autumn 2014 - [ETH Zurich](www.ethz.ch/en)

Document classifiers implemented :

- Naive Bayes
- Logistic Regression
- Support Vector Machines

##Usage

```bash
Usage: 
 <root path> <classifier> <document number>
 <root path> : String, path to the root folder
 <classifier> : Int, 1 => Naive Bayes / 2 => Logistic Regression / 3 => Support Vector Machines
 <document number> : Int, number of document to process, -1 for all
```

##Classifiers

Package com.tonybeltramelli.desktop.core.classifier
- binary.ABinaryLinearClassifier : trait to encapsulate behavior of
binary linear classifiers such as training method, vector operations and
sigmoid function which are common to both Logistic Regression and
Support Vector Machines.
- binary.LogisticRegressionBinary : extends ABinaryLinearClassifier
and override the gradient method.
- binary.SupportVectorMachinesBinary : extends ABinaryLinearClassifier
and override the gradient method with the Pegasos algorithm.
- multiclass.AClassifier : trait to encapsulate data preprocessing (compute
tf-idf, map classes to documents), interface to main class (train
and apply methods), result sorting and pruning.
- multiclass.AMultinomialLinearClassifier : extends AClassifier and
add features to support multiclass classification for binary linear classifiers implementing ABinaryLinearClassifier. The document features
are the term frequency-inverse document frequency of the most important
terms of the documents. Please refer to the Optimizations section
for how such important terms are selected.
- LogisticRegression : extends AMultinomialLinearClassifier and
pass LogisticRegressionBinary instances to its super class.
- NaiveBayes : extends AClassifier because the implementation directly
support multiclass classification (implementation inspired by Manning
et al. "Introduction to Information Retrieval" multinomial Naive Bayes pseudocode). Naive Bayes implementation using term frequency and Laplace smoothing to improve classification.
- SupportVectorMachines : extends AMultinomialLinearClassifier
and pass SupportVectorMachinesBinary instances to its super class.

##Optimizations

- Stemming using a cache system to increase performance and space consumption and remove stop words from document before stemming to eliminate semantically non-selective words : StopWordStemmer.
- Only the n most important terms of each document are kept to minimize memory usage. That is, the terms with the highest term frequency _getTermFreq in AClassifier.
- Use of 2 linked mutable Map data structure classesToDoc and documents
in AClassifier for fast class to document mapping, fast tf tf-idf computation
(_computeTermFreqInverseDocumentFreq) and fast document feature retrieving. Int keys are used instead of String to save space.
- The binary classifiers are trained with a subset of documents in order to attenuate the effect of imbalanced classes and speeding up the running time. Method getRandomDocuments in AMultinomialLinearClassifier, the documents are chosen as follows :
    * All the positive documents of the classifier's class.
    * The n randomly selected negative documents with n = 3 x (number
of positive documents for the class) 3 times was found to give
good results by training the classifiers with a reasonable amount
of negative documents.
- The results are normalized and pruned to improve the precision and f1
score by _getNormalizedAndPrunedResults in AClassifier.
- In order to improve performaces, the results are written to a file using
a buffer writer in Printer class.


