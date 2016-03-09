package com.timmeh.openhr.openholidays.model

import slick.driver.MySQLDriver.api._

object DB {
  val db = Database.forConfig("holidaysdb")
}
