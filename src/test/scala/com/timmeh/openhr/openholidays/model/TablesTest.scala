package com.timmeh.openhr.openholidays.model

import com.timmeh.openhr.openholidays.utils.TestDBContext
import org.joda.time.DateTime
import org.specs2.mutable.{After, Before, BeforeAfter, Specification}
import slick.driver.H2Driver.api._
import slick.jdbc.meta._

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Await, Future}
import scala.util.{Failure, Success}

class TablesTest extends Specification {

  val holidays = TableQuery[Holidays]
  val leaveEntitlements = TableQuery[LeaveEntitlements]

  def createSchema(db: Database) = {
    val schemas = holidays.schema ++ leaveEntitlements.schema
    db.run(schemas.create)
  }

  def insertHoliday(db: Database): Future[Int] = db.run(holidays += Holiday(101, 101, new DateTime(2020, 11, 1, 0, 0), "Full", "Annual Leave"))

  def insertLeaveEntitlement(db: Database): Future[Int] = {
    val startDate: DateTime = new DateTime(2016, 11, 1, 0, 0)
    val endDate = startDate.plusYears(1).minusDays(1)
    db.run(leaveEntitlements += LeaveEntitlement(11, 1, startDate, endDate, 25))
  }

  "Creating a test schema should work" >> new TestDBContext {
    val numberOfTables = for {
      _ <- createSchema(db)
      numberOfTables <- db.run(MTable.getTables).map(_.size)
    } yield numberOfTables
    Await.result(numberOfTables, Duration.Inf) mustEqual 2
  }

  "Inserting a holiday works" >> new TestDBContext {
    val insertCount = for {
      _ <- db.run(holidays.schema.create)
      insertCount <- insertHoliday(db)
    } yield insertCount

    Await.result(insertCount, Duration.Inf) mustEqual 1
  }

    "Querying holidays table works" >> new TestDBContext {
      val resultsFuture = for {
        _ <- db.run(holidays.schema.create)
        _ <- insertHoliday(db)
        res <- db.run(holidays.result)
      } yield res

      val results = Await.result(resultsFuture, Duration.Inf)
      results.size must beEqualTo(1)
      results.head.id mustEqual 101
    }

    "Inserting a leave entitlement works" >> new TestDBContext {
    val insertCount = for {
      _ <- db.run(leaveEntitlements.schema.create)
      insertCount <- insertLeaveEntitlement(db)
    } yield insertCount

    Await.result(insertCount, Duration.Inf) mustEqual 1
  }

    "Querying the leave entitlements table works" >> new TestDBContext {
      val resultsFuture = for {
        _ <- db.run(leaveEntitlements.schema.create)
        _ <- insertLeaveEntitlement(db)
        res <- db.run(leaveEntitlements.result)
      } yield res

      val results = Await.result(resultsFuture, Duration.Inf)
      results.size must beEqualTo(1)
      results.head.id mustEqual 11
    }


}
