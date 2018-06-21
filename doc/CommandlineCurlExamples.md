# Commandline Examples with Curl

## Create a model

### POST a local arff file to an algorithm (example)
* POST the data to the NaiveBayes algorithm
    ``` 
    curl -X POST "https://jguweka.prod.openrisknet.org/algorithm/NaiveBayes" -H  "accept: text/uri-list"  -H  "Content-Type: multipart/form-data" -F "file=@weather.numeric.arff" -F "batchSize=100" -F "useKernelEstimator=0" -F "useSupervisedDiscretization=0" -F "validation=CrossValidation" -F "validationNum=10"
    ```
    will return a task:
    ``` 
    https://jguweka.prod.openrisknet.org/task/5b28b61d1a2bd3000121a183
    ```
* check the task status:
    ```
    curl -X GET https://jguweka.prod.openrisknet.org/task/5b28b61d1a2bd3000121a183
    ```
    returns:
    ```
    {
      "date" : "2018-06-19T07:51:56.000Z",
      "errorReport" : null,
      "uri" : "https://jguweka.prod.openrisknet.org/task/5b28b61d1a2bd3000121a183",
      "status" : "COMPLETED",
      "taskID" : "5b28b61d1a2bd3000121a183",
      "URI" : "https://jguweka.prod.openrisknet.org/task/5b28b61d1a2bd3000121a183",
      "resultURI" : "https://jguweka.prod.openrisknet.org/model/5b28b61e1a2bd3000121a189",
      "creator" : "NaiveBayes",
      "step" : "SAVED",
      "title" : "NaiveBayes algorithm",
      "hasStatus" : "COMPLETED",
      "description" : "Training data on NaiveBayes algorithm.",
      "percentageCompleted" : 100.0
    }
    ```
    status is COMPLETED and the resultURI of the model is ***https://jguweka.prod.openrisknet.org/model/5b28b61e1a2bd3000121a189***

* retrieve the model as plain text representation  
    ```
    curl -H "accept: text/plain"  https://jguweka.prod.openrisknet.org/model/5b28b61e1a2bd3000121a189
    ```
    
    will return:
    ```
    Naive Bayes Classifier
    
                     Class
    Attribute          yes      no
                    (0.63)  (0.38)
    ===============================
    outlook
      sunny             3.0     4.0
      overcast          5.0     1.0
      rainy             4.0     3.0
      [total]          12.0     8.0
    
    temperature
      mean          72.9697 74.8364
      std. dev.      5.2304   7.384
      weight sum          9       5
      precision      1.9091  1.9091
    
    humidity
      mean          78.8395 86.1111
      std. dev.      9.8023  9.2424
      weight sum          9       5
      precision      3.4444  3.4444
    
    windy
      TRUE              4.0     4.0
      FALSE             7.0     3.0
      [total]          11.0     7.0
    
    
    
    === Crossvalidation Results ===
    
    Correctly Classified Instances           9               64.2857 %
    Incorrectly Classified Instances         5               35.7143 %
    Kappa statistic                          0.1026
    Mean absolute error                      0.4649
    Root mean squared error                  0.543 
    Relative absolute error                 97.6254 %
    Root relative squared error            110.051  %
    Total Number of Instances               14     
    
    === Detailed Accuracy By Class ===
    
                     TP Rate  FP Rate  Precision  Recall   F-Measure  MCC      ROC Area  PRC Area  Class
                     0.889    0.800    0.667      0.889    0.762      0.122    0.444     0.633     yes
                     0.200    0.111    0.500      0.200    0.286      0.122    0.444     0.397     no
    Weighted Avg.    0.643    0.554    0.607      0.643    0.592      0.122    0.444     0.548     
    
    
    === Confusion Matrix ===
    
     a b   <-- classified as
     8 1 | a = yes
     4 1 | b = no
    
    
    Model build options:
    weka.classifiers.bayes.NaiveBayes
    ```