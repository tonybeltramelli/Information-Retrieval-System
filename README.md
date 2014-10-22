
Information-Retrieval-System
============================

Student project Autumn 2014 - [ETH Zurich](www.ethz.ch/en)

##Usage

```bash
Usage: 
 <root path> <use language model> <only last 10 queries> <document number> <queries>(optional)
 <root path> : String, path to the tipster folder
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