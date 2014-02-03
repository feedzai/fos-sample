/*
 * $#
 * FOS Weka Sammple app
 *  
 * Copyright (C) 2013 Feedzai SA
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #$
 */

package com.feedzai.fos.samples.weka;

import com.feedzai.fos.api.*;
import com.feedzai.fos.impl.weka.config.WekaModelConfig;
import com.feedzai.fos.server.remote.api.FOSManagerAdapter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import weka.classifiers.trees.J48;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * FOS Training sample code.
 *
 * This code trains a sample model using the @see <a href="http://en.wikipedia.org/wiki/Iris_flower_data_set">Iris flower dataset</a>
 *
 * @author Miguel Duarte (miguel.duarte@feedzai.com)
 */
public class WekaTraining
{
    public static void main( String[] args ) throws RemoteException, NotBoundException, FOSException {
        FOSManagerAdapter manager = FOSManagerAdapter.create("localhost", 5959);

        List<Attribute> attributes = ImmutableList.of(
            new NumericAttribute("sepalLength"),
            new NumericAttribute("sepalWidth"),
            new NumericAttribute("petalLength"),
            new NumericAttribute("petalWidth"),
            new CategoricalAttribute("class",
                                     ImmutableList.of("Iris-setosa",
                                                      "Iris-versicolor",
                                                      "Iris-virginica")));


        Map<String, String> properties = ImmutableMap.of(WekaModelConfig.CLASS_INDEX, "4",
                                                         WekaModelConfig.CLASSIFIER_IMPL, J48.class.getName());

        ModelConfig modelConfig = new ModelConfig(attributes, properties);

        File trainFile = new File("iris.data");

        UUID uuid = manager.trainAndAddFile(modelConfig, trainFile.getAbsolutePath());

        System.out.println("Trained model UUID = " + uuid);
    }
}
