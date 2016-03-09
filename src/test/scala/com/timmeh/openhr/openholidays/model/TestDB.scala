package com.timmeh.openhr.openholidays.model

import slick.driver.MySQLDriver.api._

object TestDB {
  def dbName = s"test${util.Random.nextInt}"
  def db = Database.forURL(s"jdbc:h2:mem:$dbName", driver = "org.h2.Driver", keepAliveConnection = true)
}
