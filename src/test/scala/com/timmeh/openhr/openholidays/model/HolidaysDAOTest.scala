package com.timmeh.openhr.openholidays.model

import com.timmeh.openhr.openholidays.utils.TestDBContextWithSchemas
import org.joda.time.DateTime
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class HolidaysDAOTest extends Specification {

  class Context extends HolidaysDAO with TestDBContextWithSchemas

  val hol1 = Holiday(101, 101, new DateTime(2020, 11, 1, 0, 0), "Full", "Annual Leave")
  val hol2 = Holiday(102, 101, new DateTime(2020, 11, 2, 0, 0), "Full", "Annual Leave")
  val hol3 = Holiday(103, 101, new DateTime(2019, 11, 2, 0, 0), "Full", "Annual Leave")

  "Inserting a holiday should work" >> new Context {
    val returnedHolidaysFuture = for {
      _ <- insertHoliday(hol1)
      returnedHolidays <- getEmployeeHolidays(101)
    } yield returnedHolidays

    val returnedHolidays = Await.result(returnedHolidaysFuture, Duration.Inf)

    returnedHolidays must have size 1
  }

  "Inserting holidays should work" >> new Context {
    val returnedHolidaysFuture = for {
      _ <- insertHolidays(Seq(hol1, hol2))
      returnedHolidays <- getEmployeeHolidays(101)
    } yield returnedHolidays

    val returnedHolidays = Await.result(returnedHolidaysFuture, Duration.Inf)

    returnedHolidays must have size 2
  }

  "Getting holidays with a leave year should only return holidays for that leave year" >> new Context {
    val leaveYearStart = new DateTime(2020, 10, 1, 0, 0)
    val leaveYearEnd = leaveYearStart.plusYears(1).minusDays(1)
    val returnedHolidaysFuture = for {
      _ <- insertHolidays(Seq(hol1, hol2, hol3))
      returnedHolidays <- getEmployeeHolidays(101, leaveYearStart, leaveYearEnd)
    } yield returnedHolidays

    val returnedHolidays = Await.result(returnedHolidaysFuture, Duration.Inf)

    returnedHolidays must have size 2
  }
}
