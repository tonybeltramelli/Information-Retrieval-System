
Information-Retrieval-System
============================

Student project Autumn 2014 - [ETH Zurich](http://www.ethz.ch/en)

The project contain two main parts respectively detailed in this file:
* Query engine
* Document classifier

---
#Query Engine

##Usage

```bash
Usage: 
 <root path> <use language model> <only last 10 queries> <document number> <queries>(optional)
 <root path> : String, path to the tipster data folder
 <use language model> : Boolean, true = language-based model / false = term-based model
 <only last 10 queries> : Boolean, true = only use the last test queries / false = use all the provided topics queries
 <document number> : Int, number of document in the stream to process, -1 for all
 <queries> : String, possibility to input custom queries such as "query1" "query2" ... "queryN" / if not defined the topics queries are used as input
```

##Architecture

```
com.tonybeltramelli.desktop.
    core.
        perf.
            PerformanceAssessor.scala : wrapper used to assess precision, recall and mean average precision
            PrecisionRecallAssessor.scala : extends ch.ethz.dal.tinyir.lectures.PrecisionRecall, used by PerformanceAssessor to assess performance
        scoring.
            AScoring.scala : trait use to encapsulate scoring logic
            LanguageBasedScoring.scala : extends AScoring, use of maximum likelihood estimation and Jelinek-Mercer smoothing to score
            TermBasedScoring.scala : extends AScoring, use of square root linear transformation on term frequencies
    util.
        Helper.scala : contains constants value and utils functions
    Main.scala : entry point, algorithm flow and topic loading
```

##Miscellaneous

Optimizations :
- Stemming using a cache system to increase performance and space consumption
- Compute collection frequency, collection frequencies sums, term frequency and term frequencies sums for all documents first (method "feed" in AScoring.scala) and compute score for specific query on demand afterwards (overriden method "getScore" in LanguageBasedScoring.scala and TermBasedScoring.scala)
- For term frequency computation, logarithmic transformation and augmented term frequency have also been experimented (see TermBasedScoring.scala) but square root linear transformation have been prefered for performance / precision reasons
- For Jelinek-Mercer smoothing, a lambda value of 0.1 have been chosen according to Chengxiang Zhai and John Lafferty, A Study of Smoothing Methods for Language Models Applied to Information Retrieval. (2004) ACM Transactions on Information Systems (TOIS)

Run with VM arguments :  
-Xss200m  
-Xmx2048m  

##Dependencies

Implemented and tested for Scala 2.11.2, JavaSE-1.7, jvm-1.6

Library TinyIR.jar :
```
ch.ethz.dal.tinyir.
            processing.
                StringDocument.scala
                Document.scala
                Tokenizer.scala
                XMLDocument.scala
                SaxParsing.scala
                StopWords.scala
                TipsterParse.scala
            io.
                ZipDirStream.scala
                DirStream.scala
                ParsedXMLStream.scala
                TipsterStream.scala
                ZipStream.scala
                DocStream.scala
            lectures.
                PrecisionRecall.scala
                TipsterGroundTruth.scala
            alerts.
                AlertsTipster.scala
                Alerts.scala
                Query.scala
            util.
                StopWatch.scala
            indexing.
                FreqIndex.scala
                SimpleIndex.scala
                InvertedIndex.scala
```

Stemmer class :
```
com.github.aztek.porterstemmer.PorterStemmer.scala
```

---
#Document Classifier

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
