package com.timmeh.openhr.openholidays.model

import org.joda.time.DateTime
import slick.driver.H2Driver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}
import com.github.tototoshi.slick.H2JodaSupport._
import spray.json._


case class Holiday(id: Int, employeeId: Int, holDate: DateTime, holDayType: String, holType: String)

class Holidays(tag: Tag)
  extends Table[Holiday](tag, "HOLIDAYS") {
  def id = column[Int]("HOL_ID", O.PrimaryKey)
  def employeeId = column[Int]("EMP_ID")
  def holDate = column[DateTime]("HOL_DATE")
  def holDayType = column[String]("HOL_DAY_TYPE")
  def holType = column[String]("HOL_TYPE")

  def * = (id, employeeId, holDate, holDayType, holType) <> (Holiday.tupled, Holiday.unapply)
  *
}

case class LeaveEntitlement(id: Int, employeeId: Int, leavePeriodStartDate: DateTime, leavePeriodEndDate: DateTime, leaveAllowance: Int)

class LeaveEntitlements(tag: Tag)
  extends Table[LeaveEntitlement](tag, "ENTITLEMENTS") {
  def id = column[Int]("ID", O.PrimaryKey)
  def employeeId = column[Int]("EMP_ID")
  def leavePeriodStartDate = column[DateTime]("PERIOD_START_DATE")
  def leavePeriodEndDate = column[DateTime]("PERIOD_END_DATE")
  def leaveAllowance = column[Int]("ALLOWANCE")

  def * = (id, employeeId, leavePeriodStartDate, leavePeriodEndDate, leaveAllowance) <> (LeaveEntitlement.tupled, LeaveEntitlement.unapply)
  *
}