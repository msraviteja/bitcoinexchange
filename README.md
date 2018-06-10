# BitcoinExchange #

## Build & Run ##

```sh
$ cd bitcoinexchange
$ sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

## Code Structure ##

```
build.sbt               <= dependencies and project config

project
|_build.properties      <= version of sbt to use
|_plugins.sbt           <= sbt plugins

src
|  |_ scala
|  |  |   |_ScalatraBootstrap.scala     <= mount servlets
|  |  |_org
|  |      |_ com/ravi/teja
|  |         |_ controllers
|  |            |_ BitcoinController.scala    <= controls api routes
|  |         |_ logic
|  |            |_ BitcoinProcesser.scala     <= all processing logic
|  |            |_ HoltWinters.scala          <= implementation of HolWinter's method for for Seasonal Exponential Smoothing
|  |         |_ models
|  |            |_ Models.scala               <= data model classes
|  |         |_ utils
|  |            |_ Utils.scala                <= utility functions for usage across the application
|  |_ webapp
|     |_ WEB-INF
|        |_ web.xml
```

## API Docs ##
  The application uses Bitcoin historical prices data from coinbase API ([https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=year](https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=year)) and exposes mainly 3 features:
  
 **Note**: All Dates in the format `yyyy-MM-dd`
  
  1. `/api/bitcoin/history`

      *Params*: Query String parameters
        - `from`: Data is returned from this date till `to` date or for the given `period`
        - `to` : Data is returned till this date from `from` date or for the given `period`
        - `period`: Number of days of data to return. If neither `from` or `to` are given, then this parameter is used to return last x days data

      *Example response*:
      
      ```
      [
        {
          "time": "2018-05-15",
          "price": 8642.02
        },
        {
          "time": "2018-05-14",
          "price": 8577
        },
        {
          "time": "2018-05-13",
          "price": 8551.23
        }
      ]
      ```
  2. `/api/bitcoin/movingaverage`

      *Params*: Query String parameters
        - `from`: Data is returned from this date till `to` date
        - `to` : Data is returned till this date from `from` date
        - `window`: Days of rolling / moving average window

      *Example response*:
      
      ```
      [
          {
              "time": "2018-05-10",
              "price": 9225.865
          },
          {
              "time": "2018-05-09",
              "price": 9223.41
          },
          {
              "time": "2018-05-08",
              "price": 9299.7
          },
          {
              "time": "2018-05-07",
              "price": 9473.95
          }
      ]
      ```
  3. `/api/bitcoin/forecast`

      *Params*: Query String parameters
        - `period`: Number of future days to forecast from the current day

      *Example response*:
      
      ```
      [
          {
              "time": "2018-06-11",
              "price": 7448.767897957955
          },
          {
              "time": "2018-06-12",
              "price": 7387.977594312539
          },
          {
              "time": "2018-06-13",
              "price": 7358.876881268027
          }
      ]
      ```
## References ##
  - http://scalatra.org/guides/2.6/
  - https://www.analyticsvidhya.com/blog/2018/02/time-series-forecasting-methods/
  - https://gist.github.com/tartakynov/2fb43d1a98c7b706626e