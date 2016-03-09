package com.timmeh.openhr.openholidays

import akka.serialization.Serialization
import com.timmeh.openhr.openholidays.model.{HolidaysDAO, TestDB, Holiday}
import org.specs2.mutable.{BeforeAfter, After, Before, Specification}
import spray.http.HttpMethods._
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.json._
import com.timmeh.openhr.openholidays.model.HolidaysJsonProtocol._
import org.joda.time.DateTime
import slick.driver.MySQLDriver.api._
import ContentTypes._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class MyServiceSpec extends Specification with Specs2RouteTest {

  trait Context extends BeforeAfter with MyService {
    override val db = TestDB.db
    def actorRefFactory = system

    def before = {
      Await.result(db.run(holidaysTable.schema.create), Duration.Inf)
    }

    def after = {
      Await.result(db.run(holidaysTable.schema.drop), Duration.Inf)
    }
  }

    "Annual leave get endpoint" should {
      "return all the holidays for the given employee" in new Context {
        val hol = new Holiday(1, 1, new DateTime(2016, 11, 1, 0, 0), "PM", "Annual Leave")
        Await.result(insertHoliday(hol), Duration.Inf)

        Get("/holidays/employees/1") ~> holidays ~> check {
          val returnedHolidays = responseAs[String].parseJson.convertTo[Seq[Holiday]]
          returnedHolidays.head mustEqual hol
        }
      }
    }

  "Annual leave post endpoint" should {
    "add the inputted holiday for the given employee" in new Context {
      val holiday = new Holiday(2, 2, new DateTime(2016, 11, 1, 0, 0), "PM", "Annual Leave")
      val holidayJson = holiday.toJson.toString
      val postHoliday = HttpRequest(method = POST, uri = "/holidays", entity = HttpEntity(`application/json`, holidayJson))
      postHoliday ~> holidays ~> check {
        val response = responseAs[String]
        response mustEqual "Holiday successfully inserted"
        val employeeHolidays = Await.result(getEmployeeHoliday(2), Duration.Inf)
        employeeHolidays.head mustEqual holiday
      }
    }
  }
}
