package com.ravi.teja

import org.scalatra._
// JSON-related libraries
import org.json4s._
// JSON handling support from Scalatra
import org.scalatra.json._
import java.text._
import java.util.Date
import scala.collection.immutable.Map

class BitcoinController(pricesDataMap: Map[String,Price], forecastedPrices: Map[String,Price]) extends ScalatraServlet with JacksonJsonSupport {
	protected implicit lazy val jsonFormats: Formats = new DefaultFormats{
		override def dateFormatter = Utils.dateformatter
	}.withBigDecimal
	protected override def transformResponseBody(body: JValue): JValue = body.camelizeKeys

	get("/") {
		views.html.hello()
	}

	get("/api/bitcoin/history"){
		contentType = formats("json")

		val paramFrom = params.get("from")
		val paramTo = params.get("to")
		val paramPeriod = params.get("period")

		if(paramFrom.isEmpty && paramTo.isEmpty && paramPeriod.isEmpty){
			halt(400,Utils.makeErrorJson("Need query parameters 'from','to' (OR) 'period'"))
		}

		try {
			var fromDate = new Date()
			var toDate = new Date()
			if(!paramFrom.isEmpty){
				fromDate = Utils.dateformatter.parse(paramFrom.get)
			}
			if(!paramTo.isEmpty){
				toDate = Utils.dateformatter.parse(paramTo.get)
			}

			val period = Utils.parseInt(paramPeriod.getOrElse("-1"))
			if (Utils.isValidPeriod(period)){
				if(!paramFrom.isEmpty){
					BitcoinProcesser.HistoryPrices(Utils.addDays(fromDate,period), period, pricesDataMap)
				}else{
					BitcoinProcesser.HistoryPrices(toDate, period, pricesDataMap)
				}
			} else if(!paramFrom.isEmpty || !paramTo.isEmpty){
				BitcoinProcesser.HistoryPrices(toDate, Utils.daysBetween(toDate,fromDate), pricesDataMap)
			} else{
				halt(400,Utils.makeErrorJson("Invalid query parameters 'from','to' (OR) 'period'"))
			}
		} catch {
			case parseEx: java.text.ParseException => {
				BadRequest(Utils.makeErrorJson("Invalid date format"))
			}
			case e: Exception => {
				// Log the exception somewhere
				InternalServerError(Utils.makeErrorJson("Exception occured while processing"))
			}
		}
	}

	get("/api/bitcoin/movingaverage"){
		contentType = formats("json")

		val paramFrom = params.get("from")
		val paramTo = params.get("to")
		val paramX = params.get("x")

		if(paramFrom.isEmpty || paramTo.isEmpty || paramX.isEmpty){
			halt(400,Utils.makeErrorJson("Need query parameters 'from','to','x'"))
		}

		try {
			val fromDate = Utils.dateformatter.parse(paramFrom.get)
			val toDate = Utils.dateformatter.parse(paramTo.get)
			val x = Utils.parseInt(paramX.get)
			if (x > 0){
				val range = Utils.daysBetween(toDate,fromDate)
				if(range >= x){
					BitcoinProcesser.MovingAverage(toDate, range, x, pricesDataMap)
				} else {
					halt(400,Utils.makeErrorJson("Invalid date ranges"))
				}
			} else{
				halt(400,Utils.makeErrorJson("Invalid parameter 'x'"))
			}
		} catch {
			case parseEx: java.text.ParseException => {
				BadRequest(Utils.makeErrorJson("Invalid date format"))
			}
			case e: Exception => {
				// Log the exception somewhere
				InternalServerError(Utils.makeErrorJson("Exception occured while processing"))
			}
		}
	}

	get("/api/bitcoin/forecast"){
		contentType = formats("json")

		val paramX = params.get("period")

		if(paramX.isEmpty){
			halt(400,Utils.makeErrorJson("Need query parameter 'period'"))
		}

		try {
			val x = Utils.parseInt(paramX.get)
			if (x > 0){
				BitcoinProcesser.ForecastPrices(x, forecastedPrices)
			} else{
				halt(400,Utils.makeErrorJson("Invalid parameter 'period'"))
			}
		} catch {
			case e: Exception => {
				// Log the exception somewhere
				InternalServerError(Utils.makeErrorJson("Exception occured while processing"))
			}
		}
	}
}
