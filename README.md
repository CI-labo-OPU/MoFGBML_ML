# MoFGBML_ML

---

## Reference

Y. Omozaki, N. Masuyama, Y. Nojima, and H. Ishibuchi, "Multiobjective Fuzzy Genetics-Based Machine Learning for Multi-Label Classification," in _Proceedings of IEEE WCCI2020 (FUZZ-IEEE2020)_.

---


This project is a source-code, which is a fuzzy rule-based classification system for Multi-Label Classification.
We implemented a multiobjective fuzzy genetics-based machine learning for multi-label classification.
The source-codes is placed in "src" directory.

We also placed runnable JAR files in "JAR" directory.
You can run MoFGBML_ML experiments with the JAR files.

The main MoFGBML_ML project is implemented in "MoFGBMLML.jar".
You can run the JAR file as follows:

```
$ java -jar MoFGBMLML.jar CFmean dataset/emotions/a0_0_emotions-10tra.dat dataset/emotions/a0_0_emotions-10tst.dat trial00 1
```

 + 1st argument is the name of properties file. You don't need to attach ".properties" at the end.
 + 2nd argument is the path of the training dataset file. In this argument, the current directory is at the same place with a JAR file.
 + 3rd argument is the path of the test dataset file.
   - The training/test datasets must be normalized and formatted.
   - The first row is the details of the dataset, #of instances, #of dimension, #of classes.
   - Each attribute is separated by Comma (like a CSV file).
 + 4th argument is the name of result directory.
 + 5th argument is the types of MOP (formulation of a multiobjective optimization problem).
   - 1: Maximizing Subset Accuracy and Minimizing Number of rules.
   - 2: Minimizing Hamming Loss and Minimizing Number of rules.
   - 3: Maximizing F-Measure and Minimizing Number of rules.

You can also run the experiments on 3 times 10-fold cross-validation with .bat files.
If you performs 10CV, please prepare divided dataset files based on cross-validation.
The experiments are automatically performed 30 runs.

```
$> MoFGBMLML.bat CFmean emotions 1
```

## Data Transformation Approaches

We also implemented two data transformation algorithms, "Binary Relevance" and "Label Power Set" applying to MoFGBML.
They are placed in this project.

It can be performed in the same way of MoFGBMLML.

We show examples of how to run them.

```
$ java -jar BinaryRelevance.jar "properties file" "Training data" "Test data" "result directory"

$> BR.bat "properties file" "dataset name"
```

```
$ java -jar LabelPowerSet.jar "properties file" "Training data" "Test data" "result directory"

$> LP.bat "properties file" "dataset name"
```
