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
import com.feedzai.fos.server.remote.api.FOSManagerAdapter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * FOS Scoring sample code.
 *
 * This sample assumes that a model has been previously trained by {@see com.feedzai.fos.samples.WekaTraining}
 *
 * This code trains a sample model using the @see <a href="http://en.wikipedia.org/wiki/Iris_flower_data_set">Iris flower dataset</a>
 *
 * @author Miguel Duarte (miguel.duarte@feedzai.com)
 */
public class WekaScoring
{
    public static void main( String[] args ) throws RemoteException, NotBoundException, FOSException {
        if (args.length != 1) {
            System.err.println("Please supply the model UUID to score");
            return;
        }
        FOSManagerAdapter manager = FOSManagerAdapter.create("localhost", 5959);

        UUID modelId = UUID.fromString(args[0]);

        List<Object[]> scorables = Arrays.asList(new Object[][] {
                {5.8,4.0,1.2,0.2, null}, // 1: Iris-setosa
                {6.9,3.1,4.9,1.5, null}, // 2: Iris-versicolor
                {6.0,2.2,5.0,1.5, null}  // 3: Iris-virginica
        });

        Scorer scorer = manager.getScorer();

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
    }
}
