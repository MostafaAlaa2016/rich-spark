/*
 * Copyright (c) 2016 Mashin (http://mashin.io). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.mllib.regression

import io.mashin.rich.spark.GradientDescentDataGen._
import io.mashin.rich.spark.RichSparkTestSuite
import org.apache.spark.mllib.evaluation.RegressionMetrics

class LinearRegressionWithParallelSGDSuite extends RichSparkTestSuite {

  sparkTest("LinearRegressionWithParallelSGD VS LinearRegressionWithSGD") {sc =>
    val data = generate(sc).cache()
    data.count()

    var model1: LinearRegressionModel = null
    val t1 = time {
      model1 = LinearRegressionWithParallelSGD.train(data, numIterations,
        numIterations2, stepSize, miniBatchFraction, w0)
    }

    var model2: LinearRegressionModel = null
    val t2 = time {
      model2 = LinearRegressionWithSGD.train(data,
        numIterations * numIterations2, stepSize, miniBatchFraction, w0)
    }

    val metrics1 = new RegressionMetrics(predictionAndObservations(data, model1))
    val metrics2 = new RegressionMetrics(predictionAndObservations(data, model2))

    t1 should be < t2
    metrics1.meanAbsoluteError should be < metrics2.meanAbsoluteError
    metrics1.rootMeanSquaredError should be < metrics2.rootMeanSquaredError

    println(s"LinearRegressionWithParallelSGD (${formatDuration(t1)}) " +
      s"is ${t2.toDouble/t1.toDouble}X" +
      s" faster than LinearRegressionWithSGD (${formatDuration(t2)})")
    println(s"meanAbsoluteError: LinearRegressionWithParallelSGD " +
      s"is ${metrics2.meanAbsoluteError/metrics1.meanAbsoluteError}X" +
      s" more accurate than LinearRegressionWithSGD")
    println(s"rootMeanSquaredError: LinearRegressionWithParallelSGD " +
      s"is ${metrics2.rootMeanSquaredError/metrics1.rootMeanSquaredError}X" +
      s" more accurate than LinearRegressionWithSGD")
  }

}
