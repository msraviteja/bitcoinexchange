package com.ravi.teja

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize.{LBFGS, ApproximateGradientFunction}
import scala.math._

/*
	Referred this for Scala implementation of HoltWinters method for Seasonal Exponential Smoothing
	https://gist.github.com/tartakynov/2fb43d1a98c7b706626e
*/
object HoltWinters {
	implicit def ArrayExtension(arr: Array[Double]) = new {
		def mean(): Double = arr.sum / arr.length
	}

	private def rootMeanSquareError(series: Array[Double], seasonalPeriod: Int) = new ApproximateGradientFunction[Int, DenseVector[Double]](
	(theta: DenseVector[Double]) => {
			val smooth = HoltWinters(series, seasonalPeriod, 0, theta(0), theta(1), theta(2))
			val zipped = series.drop(1).zip(smooth)
			sqrt(zipped.map { case (actual, predicted) => pow(actual - predicted, 2)}.mean())
		}
	)

	def apply(series: Array[Double], periodSeasonal: Int, periodForecast: Int): Array[Double] = {
		val initial = DenseVector(0.3, 0.1, 0.1)
		val optimizer = new LBFGS[DenseVector[Double]](tolerance = 1.0E-5)
		val error = rootMeanSquareError(series, periodSeasonal)
		val theta = optimizer.minimize(error, initial)
		HoltWinters(series, periodSeasonal, periodForecast, theta(0), theta(1), theta(2))
	}

	def apply(series: Array[Double], periodSeasonal: Int, periodForecast: Int, alpha: Double, beta: Double, gamma: Double): Array[Double] = {
		var x: Array[Double] = series
		var L: Array[Double] = Array()
		var T: Array[Double] = Array()
		var I: Array[Double] = Array()
		var X: Array[Double] = Array()

		// use the first period of data for initialization
		val (a, b) = if (periodSeasonal > 1) linearRegression(x.take(periodSeasonal)) else (x(0), 0.0)
		for (t <- 0 until periodSeasonal) {
			L :+= a * (t + 1) + b
			T :+= 0.0
			I :+= gamma * (x(t) - L(t))
			X :+= L(t) + T(t) + I(t)
		}

		// start calculation
		for (t <- periodSeasonal until (series.length + periodForecast)) {
			if (t == x.length) {
			x :+= X.last
			}

			L :+= alpha * (x(t) - I(t - periodSeasonal)) + (1 - alpha) * (L(t - 1) + T(t - 1))
			T :+= beta * (L(t) - L(t - 1)) + (1 - beta) * T(t - 1)
			I :+= gamma * (x(t) - L(t)) + (1 - gamma) * I(t - periodSeasonal)
			X :+= L(t) + T(t) + I(t - periodSeasonal + 1)
		}

		X.slice(X.size - periodForecast, X.size)
	}

	private def linearRegression(input: Array[Double]): (Double, Double) = {
		val y = DenseVector(input)
		val x = DenseMatrix.fill[Double](input.length, 2)(1)
		x(::, 0) := DenseVector.rangeD(1, input.length + 1)
		val cov = (DenseMatrix.zeros[Double](x.cols, x.cols) + (x.t * x))
		val scaled = DenseVector.zeros[Double](x.cols) + (x.t * y)
		val theta: DenseVector[Double] = cov \ scaled
		(theta(0), theta(1))
	}
}