package com.timmeh.openhr.openholidays.utils

import com.timmeh.openhr.openholidays.model.{LeaveEntitlements, Holidays}
import org.specs2.mutable.{Before, After}
import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait TestDBContext extends After {
  val dbName = s"test${util.Random.nextInt}"
  val db = Database.forURL(s"jdbc:h2:mem:$dbName", driver = "org.h2.Driver", keepAliveConnection = true)

  def after = {
    db.close()
  }
}

trait TestDBContextWithSchemas extends TestDBContext with Before {
  val holidays = TableQuery[Holidays]
  val leaveEntitlements = TableQuery[LeaveEntitlements]

  def before = {
    val schemas = holidays.schema ++ leaveEntitlements.schema
    Await.result(db.run(schemas.create), Duration.Inf)
  }
}