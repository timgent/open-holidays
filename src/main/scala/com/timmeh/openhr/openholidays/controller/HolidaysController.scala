package com.timmeh.openhr.openholidays.controller

import com.timmeh.openhr.openholidays.model.{HolidaysDAO, LeaveEntitlementsDAO, HolidaysResponse}
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait HolidaysController {
  this: LeaveEntitlementsDAO with HolidaysDAO =>
  def holsAndCurrentLeaveYearForEmployee(leaveYear: DateTime, employeeId: Int): Future[HolidaysResponse] = {
    for {
      leaveEntitlement <- getEmployeeLeaveEntitlement(employeeId, new DateTime(leaveYear))
      holidays <- leaveEntitlement.map{le =>
        getEmployeeHolidays(employeeId, le.leavePeriodStartDate, le.leavePeriodEndDate)
      }.getOrElse(Future(List()))
    } yield {
      HolidaysResponse(leaveEntitlement, holidays)
    }
  }
}