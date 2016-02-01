package com.timmeh.openhr.openholidays.model

import slick.driver.H2Driver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

case class Holiday(id: Int, employeeId: Int, holDate: java.sql.Date, holDayType: String, holType: String)

class Holidays(tag: Tag)
  extends Table[Holiday](tag, "HOLIDAYS") {
  def id = column[Int]("HOL_ID", O.PrimaryKey)
  def employeeId = column[Int]("EMP_ID")
  def holDate = column[java.sql.Date]("HOL_DATE")
  def holDayType = column[String]("HOL_DAY_TYPE")
  def holType = column[String]("HOL_TYPE")

  def * = (id, employeeId, holDate, holDayType, holType) <> (Holiday.tupled, Holiday.unapply)
  *
}
