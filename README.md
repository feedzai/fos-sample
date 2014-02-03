FOS Sample code

=========


This project contains FOS sample training and scoring samples.

All the following samples assume a locally running fos server with the default configuration. Please checkout [fos-core] readme on how to create and start a fos server bundle

# Training a WEKA classifier

You can check the full code @ [TrainingSample]

The first step is to create a FOS manager. The manager provides an API to manage and train models. 
You can create a manager by calling the `create` method on the `FOSManagerAdapter`. The method has two parameters:

1. FOS server host. Since we're assuming a locally running server `localhost` is fine.
2. FOS RMI port

```java
    FOSManagerAdapter manager = FOSManagerAdapter.create("localhost", 5959);
```

The second step is to create a model configuration. This is done by suppling a list of model attributes:

```java
   List<Attribute> attributes = ImmutableList.of(
            new NumericAttribute("sepalLength"),
            new NumericAttribute("sepalWidth"),
            new NumericAttribute("petalLength"),
            new NumericAttribute("petalWidth"),
            new CategoricalAttribute("class",
                                     ImmutableList.of("Iris-setosa",
                                                      "Iris-versicolor",
                                                      "Iris-virginica")));

```

And setting a couple  configuration options:

1. Classifier attributes index -  Property `WekaModelConfig.CLASS_INDEX`
1. Weka implementation to use - Property `WekaModelConfig.CLASSIFIER_IMPL`

```java
    Map<String, String> properties = ImmutableMap.of(
                                          WekaModelConfig.CLASS_INDEX, "4",
                                          WekaModelConfig.CLASSIFIER_IMPL, J48.class.getName());
```

Now we're ready to create a model configuration:

```java
    ModelConfig modelConfig = new ModelConfig(attributes, properties);
```

Train and create a model. The `trainAndAddFile` receives a `ModelConfig` and a `CSV` file with the training instances.

```java
        File trainFile = new File("iris.data");

        UUID uuid = manager.trainAndAddFile(modelConfig, trainFile.getAbsolutePath());

        System.out.println("Trained model UUID = " + uuid);
```

Running [TrainingSample.java] should display the generated model identifier.

```
Trained model UUID = f80e7881-e267-4d2b-9262-d58c8adcdae2
```

Keep note of this model UUID. You'll need it for the next sample.

So, behind the scenes, FOS trained and persisted a weka model with the specified configuration. If you're curious you may now check the `fos-server/models` folder. It should contain `.model` file with the trained model and a `.header` file with the model configuration:

```json
{
   "storeModel" : true,
   "attributes" : [
      {
         "name" : "sepalLength",
         "@type" : ".NumericAttribute"
      },
      {
         "name" : "sepalWidth",
         "@type" : ".NumericAttribute"
      },
      {
         "name" : "petalLength",
         "@type" : ".NumericAttribute"
      },
      {
         "name" : "petalWidth",
         "@type" : ".NumericAttribute"
      },
      {
         "categoricalInstances" : [
            "Iris-setosa",
            "Iris-versicolor",
            "Iris-virginica"
         ],
         "unknownReplacement" : "__UNKOWN__",
         "name" : "class",
         "@type" : ".CategoricalAttribute"
      }
   ],
   "properties" : {
      "classifierimpl" : "weka.classifiers.trees.J48",
      "model" : "/home/miguel/fos-server/models/f80e7881-e267-4d2b-9262-d58c8adcdae22854550933383138387.model",
      "id" : "f80e7881-e267-4d2b-9262-d58c8adcdae2",
      "classIndex" : "4"
   }
}
```

# Scoring with a Weka classifier

This sample assumes that you've sucessfully trained a model as specified in the previous step.

Now that we have a trained model, we can move on to the [ScoringSample]. 

First we have to pass the previously trained model UUID: 

```java
public static void main( String[] args ) throws RemoteException, NotBoundException, FOSException {
        if (args.length != 1) {
            System.err.println("Please supply the model UUID to score");
            return;
        }
        UUID modelId = UUID.fromString(args[0]);
```

Then we obtain a reference to the manager:

```java
        FOSManagerAdapter manager = FOSManagerAdapter.create("localhost", 5959);
```

Add a couple sample scoring instances. These were randomly from the [iris] dataset - one for each flower type:

```java
        List<Object[]> scorables = Arrays.asList(new Object[][] {
                {5.8,4.0,1.2,0.2, null}, // 1: Iris-setosa
                {6.9,3.1,4.9,1.5, null}, // 2: Iris-versicolor
                {6.0,2.2,5.0,1.5, null}  // 3: Iris-virginica
        });
```

The next step is to obtain a reference to the `Scorer` instance that will performing the scoring.

```java
        Scorer scorer = manager.getScorer();

```

Now we can score the sample instances and print the predicted score for each instance:

```java
        System.out.println("Probability for ");
        System.out.println("Instance ID |  setosa  |versicolor| virginica");

        int instanceid = 1;
        for(double[] score : scorer.score(modelId, scorables)) {
            System.out.println(String.format("Instance %1$d  | %2$f | %3$f | %4$f",
                                             instanceid++,
                                             score[0],
                                             score[1],
                                             score[2]));
        }
```        

Running the sample should produce the following output

```
Probability for 
Instance ID |  setosa  |versicolor| virginica
Instance 1  | 1.000000 | 0.000000 | 0.000000
Instance 2  | 0.000000 | 0.979167 | 0.020833
Instance 3  | 0.000000 | 0.000000 | 1.000000
```

As we can see, the algorithm predicted the flower type with a high decree of certainty. After all we're scoring the same instances that were part of the original training :smirk:



[TrainingSample](https://github.com/feedzai/fos-sample/blob/master/src/main/java/com/feedzai/fos/samples/weka/WekaTraining.java)
[ScoringSample](https://github.com/feedzai/fos-sample/blob/master/src/main/java/com/feedzai/fos/samples/weka/WekaScoring.java)
[iris](http://en.wikipedia.org/wiki/Iris_flower_data_set)

[fos-core](https://github.com/feedzai/fos-core/blob/master/README.md)
