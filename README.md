FosSample
=========

This project contains FOS sample training and scoring samples.

All the following samples assume a locally running fos server with the default configuration. Please checkout [fos-core] readme on how to create and start a fos server bundle

# Training a WEKA classifier

You can check the full code at [TrainingSample.java]

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

Running [TrainingSample.java] should display the generated model identifier

```
Trained model UUID = f80e7881-e267-4d2b-9262-d58c8adcdae2
```

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


[TrainingSample.java](https://github.com/feedzai/FosSample/blob/master/src/main/java/FosSample/TrainingSample.java)
[fos-core](https://github.com/feedzai/fos-core/blob/master/README.md)
