package com.ravi.teja

import java.util.Date
import scala.collection.immutable.Map
import scala.collection.mutable.ListBuffer

object BitcoinProcesser {

	def HistoryPrices(toDate: Date, range: Int, pricesDataMap: Map[String,Price]) = {
		val result = ListBuffer[Price]()
		for (i <- 0 to range){
			val next = Utils.addDays(toDate, -i)

			// We can use a database to store & fetch the prices history data
			val price = pricesDataMap.get(Utils.parseDate(next))

			if (!price.isEmpty){
				result += price.get
			}
		}
		result.toList
	}

	def MovingAverage(toDate: Date, range: Int, window: Int, pricesDataMap: Map[String,Price]) = {
		val result = ListBuffer[Price]()
		var windowSum = 0.0

		for (i <- 0 to window-1){
			val next = Utils.addDays(toDate, -i)

			// We can use a database to store & fetch the prices history data
			val data = pricesDataMap.get(Utils.parseDate(next))

			if (!data.isEmpty){
				windowSum += data.get.price
			}
		}
		result += Price(toDate, windowSum/window)

		for (i <- 1 to range-window+1){
			val removeDate = Utils.addDays(toDate, -i+1)
			val addDate = Utils.addDays(toDate, -i+1-window)

			val removeData = pricesDataMap.get(Utils.parseDate(removeDate)).get.price
			val addData = pricesDataMap.get(Utils.parseDate(addDate)).get.price

			windowSum = windowSum + addData - removeData
			result += Price(Utils.addDays(toDate, -i), windowSum/window)
		}

		result.toList
	}

	def ForecastPrices(period: Int, pricesDataMap: Map[String,Price]) = {
		val result = ListBuffer[Price]()
		val today = new Date()
		for (i <- 1 to period){
			val next = Utils.addDays(today, i)

			// We can use a database to store & fetch the forecasted prices data
			val price = pricesDataMap.get(Utils.parseDate(next))

			if (!price.isEmpty){
				result += price.get
			}
		}
		result.toList
	}
}