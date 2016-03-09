package com.timmeh.openhr.openholidays.model

import org.joda.time.DateTime
import slick.driver.MySQLDriver.api._
import DB._

import scala.concurrent.Future

trait HolidaysDAO {
  val db: Database
  val holidaysTable = TableQuery[Holidays]

  def getEmployeeHoliday(employeeId: Int): Future[Seq[Holiday]] = {
    val query = holidaysTable.filter(_.employeeId === employeeId)
    db.run(query.result)
  }

  def insertHoliday(holiday: Holiday): Future[Int] = db.run(holidaysTable += holiday)

}