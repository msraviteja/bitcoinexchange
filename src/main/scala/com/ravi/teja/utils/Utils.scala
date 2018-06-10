package com.ravi.teja

import java.text._
import java.util.Date

object Utils{
	
	val dateformatter = new SimpleDateFormat("yyyy-MM-dd")

	def toDouble(x: String): Double = {
		try { 
			x.toDouble
		} catch {
			case e: Exception => 0.0
		}
	}

	def parseDate(d: Date) = {
		dateformatter.format(d)
	}

	def makeErrorJson(body: String) = {
		f"""
		{"error":"$body%s"}
		"""
	}

	def parseInt(x: String) = {
		try { 
			x.toInt
		} catch {
			case e: Exception => -1
		}
	}

	def isValidPeriod(p: Int) = {
		p >= 0
	}

	def daysBetween(to: Date, from: Date) = {
        val difference =  (to.getTime()-from.getTime())/86400000;
        Math.abs(difference).toInt;
    }

    def addDays(d: Date, i: Int) = {
    	new Date((d.getTime()/1000 + (i * 86400))*1000)
    }
}