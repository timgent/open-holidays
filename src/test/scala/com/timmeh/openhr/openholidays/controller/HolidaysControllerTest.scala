package com.timmeh.openhr.openholidays.controller

import com.timmeh.openhr.openholidays.model.{LeaveEntitlement, LeaveEntitlementsDAO, Holiday, HolidaysDAO}
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import com.timmeh.openhr.openholidays.utils.TestDBContextWithSchemas
class Context extends HolidaysDAO with TestDBContextWithSchemas
import scala.concurrent.Await
import scala.concurrent.duration.Duration


class HolidaysControllerTest extends Specification {

  class Context extends HolidaysDAO with LeaveEntitlementsDAO with HolidaysController with TestDBContextWithSchemas

  val hol1 = Holiday(101, 101, new DateTime(2020, 11, 1, 0, 0), "Full", "Annual Leave")
  val hol2 = Holiday(102, 101, new DateTime(2020, 11, 2, 0, 0), "Full", "Annual Leave")
  val hol3 = Holiday(103, 101, new DateTime(2019, 11, 2, 0, 0), "Full", "Annual Leave")
  val leaveYearStart = new DateTime(2020, 10, 1, 0, 0)
  val leaveYearEnd = leaveYearStart.plusYears(1).minusDays(1)
  val leaveEntitlement = LeaveEntitlement(1, 101, leaveYearStart, leaveYearEnd, 25)

  "Getting holidays with a leave year should only return holidays for that leave year including the leave year " +
    "details" >> new Context {

    val responseFuture = for {
      _ <- insertHolidays(Seq(hol1, hol2, hol3))
      _ <- insertLeaveEntitlement(leaveEntitlement)
      response <- holsAndCurrentLeaveYearForEmployee(new DateTime(2020, 11, 1, 0, 0), 101)
    } yield response

    val response = Await.result(responseFuture, Duration.Inf)
    val returnedHolidays = response.holidays
    val Some(returnedLeaveEntitlement) = response.leaveEntitlement

    returnedHolidays must have size 2
    returnedLeaveEntitlement mustEqual leaveEntitlement
  }

  "Getting holidays when there are no leave entitlements should produce an empty response" >> new Context {
    val responseFuture = for {
      _ <- insertHolidays(Seq(hol1, hol2, hol3))
      response <- holsAndCurrentLeaveYearForEmployee(new DateTime(2020, 11, 1, 0, 0), 101)
    } yield response

    val response = Await.result(responseFuture, Duration.Inf)

    response.holidays must have size 0
    response.leaveEntitlement mustEqual None
  }

  "Getting holidays when there is a leave entitlement but no holidays should just return the leave entitlement" >> new Context {
    val responseFuture = for {
      _ <- insertLeaveEntitlement(leaveEntitlement)
      response <- holsAndCurrentLeaveYearForEmployee(new DateTime(2020, 11, 1, 0, 0), 101)
    } yield response

    val response = Await.result(responseFuture, Duration.Inf)

    response.holidays must have size 0
    response.leaveEntitlement mustEqual Some(leaveEntitlement)
  }
}
