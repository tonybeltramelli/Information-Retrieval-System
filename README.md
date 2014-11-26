
Machine Learning Classifier
============================

Student project Autumn 2014 - [ETH Zurich](www.ethz.ch/en)

Document classifiers implemented :

- Naive Bayes
- Logistic Regression
- Support Vector Machines

##Miscellaneous

Optimizations :
- Stemming using a cache system to increase performance and space consumption
- Only take the n term with the highter document frequency to minimize memory use
- Remove stop words from document before stemming to eliminate semantically nonselective words
- Use of 2 linked mutable Map data structure _classesToDoc and _documents (in AClassifier) for fast class to document mapping, fast tf-idf computation and fast document feature retrieving
- In order to deal with imbalanced classes and to speed up the running time, the binary classifiers are trained with a subset of documents selected as follows :
* All the positive documents of the classifier class
* N negative randomely selected documents with N = 2 x (number of positive documents for the class)



