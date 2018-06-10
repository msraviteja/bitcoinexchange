package com.ravi.teja

import org.scalatra.test.scalatest._

class BitcoinControllerTests extends ScalatraFunSuite {

  addServlet(classOf[BitcoinController], "/*")

  test("GET / on BitcoinController should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
