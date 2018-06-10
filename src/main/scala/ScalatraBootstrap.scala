import com.ravi.teja._
import org.scalatra._
import javax.servlet.ServletContext
import scalaj.http.Http
import net.liftweb.json._
import java.text._
import java.util.Date

class ScalatraBootstrap extends LifeCycle {
	override def init(context: ServletContext) {
		implicit val formats: Formats = new DefaultFormats{
			override def dateFormatter = Utils.dateformatter
		}

		val response = Http("https://www.coinbase.com/api/v2/prices/BTC-USD/historic").param("period", "year").asString

		val json = parse(response.body)

		val data = json \ "data" \ "prices"

		val pricesData = data.extract[List[PriceJson]]

		val pricesDataMap = pricesData.map(p => (Utils.parseDate(p.time), Price(p.time,Utils.toDouble(p.price)))).toMap

		// Forecast prices using HoltWinters method
		val today = new Date()
		val pricesArray = pricesData.reverse.map(p => Utils.toDouble(p.price)).toArray
		val forecastedPrices = HoltWinters.apply(pricesArray, 7, 15).zipWithIndex.map{
			case (x,i) => Price(Utils.addDays(today, i+1), x)
		}.map(p => (Utils.parseDate(p.time), p)).toMap

		context.mount(new BitcoinController(pricesDataMap, forecastedPrices), "/*")
	}
}
