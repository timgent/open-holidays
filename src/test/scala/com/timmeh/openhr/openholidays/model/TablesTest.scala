package com.timmeh.openhr.openholidays.model

import org.specs2.mutable.{After, Before, BeforeAfter, Specification}
import slick.driver.H2Driver.api._
import slick.jdbc.meta._

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


class TablesTest extends Specification {

  trait Context extends After {
    val dbName = s"test${util.Random.nextInt}"
    val db = Database.forURL(s"jdbc:h2:mem:$dbName", driver = "org.h2.Driver", keepAliveConnection = true)
    println("Doing setup")

    def after: Any = {
      db.close()
      println("Done. Cleanup")
    }
  }

  val holidays = TableQuery[Holidays]

  def createSchema(db: Database) = {
    db.run(holidays.schema.create)
  }

  def insertHoliday(db: Database): Future[Int] = db.run(holidays += Holiday(101, 101, new java.sql.Date(2020, 11, 1), "Full", "Annual Leave"))

  "Creating a test schema should work" >> new Context {
    val numberOfTables = for {
      _ <- createSchema(db)
      numberOfTables <- db.run(MTable.getTables).map(_.size)
    } yield numberOfTables
    Await.result(numberOfTables, Duration.Inf) mustEqual (1)
  }

  "Inserting a holiday works" >> new Context {
    val insertCount = for {
      _ <- db.run(holidays.schema.create)
      insertCount <- insertHoliday(db)
    } yield insertCount

    Await.result(insertCount, Duration.Inf) must beEqualTo(1)
  }

    "Querying holidays table works" >> new Context {
      val resultsFuture = for {
        _ <- db.run(holidays.schema.create)
        _ <- insertHoliday(db)
        res <- db.run(holidays.result)
      } yield res

      val results = Await.result(resultsFuture, Duration.Inf)
      results.size must beEqualTo(1)
      results.head.id must beEqualTo(101)
    }
}
