package com.timmeh.openhr.openholidays.model

import org.joda.time.DateTime
import slick.driver.MySQLDriver.api._
import com.github.tototoshi.slick.H2JodaSupport._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait LeaveEntitlementsDAO {
  val db: Database
  val leaveEntitlementsTable = TableQuery[LeaveEntitlements]

  def getEmployeeLeaveEntitlements(employeeId: Int): Future[Seq[LeaveEntitlement]] = {
    val query = leaveEntitlementsTable.filter(_.employeeId === employeeId)
    db.run(query.result)
  }

  def insertLeaveEntitlement(leaveEntitlement: LeaveEntitlement): Future[Int] = db.run(leaveEntitlementsTable += leaveEntitlement)

  def insertLeaveEntitlements(leaveEntitlements: Seq[LeaveEntitlement]): Future[Option[Int]] = {
    db.run(leaveEntitlementsTable ++= leaveEntitlements)
  }

  def getEmployeeLeaveEntitlement(employeeId: Int, leaveYearDate: DateTime): Future[Option[LeaveEntitlement]] = {
    val query = leaveEntitlementsTable.filter(entitlement =>
      entitlement.employeeId === employeeId &&
      entitlement.leavePeriodStartDate < leaveYearDate &&
      entitlement.leavePeriodEndDate > leaveYearDate)
    db.run(query.result).map(_.headOption)
  }
}
