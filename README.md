
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

##Miscellaneous

Optimizations :
- Stemming using a cache system to increase performance and space consumption
- Only take the n term with the higher document frequency to minimize memory use
- Remove stop words from document before stemming to eliminate semantically non-selective words
- Use of 2 linked mutable Map data structure _classesToDoc and _documents (in AClassifier) for fast class to document mapping, fast tf-idf computation and fast document feature retrieving
- In order to deal with imbalanced classes and to speed up the running time, the binary classifiers are trained with a subset of documents selected as follows :
    * All the positive documents of the classifier class
    * The n negative randomly selected documents with n = 2 x (number of positive documents for the class)

Run with VM arguments :  
-Xss200m  
-Xmx2048m 


