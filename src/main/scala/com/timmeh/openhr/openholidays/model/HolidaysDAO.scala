package com.timmeh.openhr.openholidays.model

import org.joda.time.DateTime
import slick.driver.MySQLDriver.api._
import DB._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import com.github.tototoshi.slick.H2JodaSupport._


trait HolidaysDAO {
  def db: Database
  val holidaysTable = TableQuery[Holidays]

  def getEmployeeHolidays(employeeId: Int): Future[List[Holiday]] = {
    val query = holidaysTable.filter(_.employeeId === employeeId)
    db.run(query.result).map(_.toList)
  }

  def getEmployeeHolidays(employeeId: Int, leavePeriodStartDate: DateTime, leavePeriodEndDate: DateTime): Future[List[Holiday]] = {
    val query = holidaysTable.filter(holiday =>
      holiday.employeeId === employeeId &&
      holiday.holDate > leavePeriodStartDate &&
      holiday.holDate < leavePeriodEndDate)
    db.run(query.result).map(_.toList)
  }

  def insertHoliday(holiday: Holiday): Future[Int] = db.run(holidaysTable += holiday)

  def insertHolidays(holidays: Seq[Holiday]): Future[Option[Int]] = db.run(holidaysTable ++= holidays)
}