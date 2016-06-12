package com.timmeh.openhr.openholidays

import akka.serialization.Serialization
import com.timmeh.openhr.openholidays.model._
import com.timmeh.openhr.openholidays.utils.TestDBContextWithSchemas
import org.specs2.mutable.{BeforeAfter, After, Before, Specification}
import slick.driver.MySQLDriver
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

  trait Context extends BeforeAfter {
    val testDB = TestDB.db

    val holidaysService = new HolidaysService with HolidaysDAO with LeaveEntitlementsDAO {
      val db = testDB
      def actorRefFactory = system
    }

    val testDAOsForSetup = new HolidaysDAO with LeaveEntitlementsDAO {
      val db = testDB
    }

    def before = {
      Await.result(testDB.run(testDAOsForSetup.holidaysTable.schema.create), Duration.Inf)
      Await.result(testDB.run(testDAOsForSetup.leaveEntitlementsTable.schema.create), Duration.Inf)
    }

    def after = {
      Await.result(testDB.run(testDAOsForSetup.holidaysTable.schema.drop), Duration.Inf)
      Await.result(testDB.run(testDAOsForSetup.leaveEntitlementsTable.schema.drop), Duration.Inf)
    }
  }

  "Annual leave get endpoint" should {
    "return all the holidays for the given employee" in new Context {
      val hol = new Holiday(1, 1, new DateTime(2016, 11, 1, 0, 0), "PM", "Annual Leave")
      Await.result(testDAOsForSetup.insertHoliday(hol), Duration.Inf)

      Get("/holidays/employees/1") ~> holidaysService.holidays ~> check {
        val returnedHolidays = responseAs[String].parseJson.convertTo[Seq[Holiday]]
        returnedHolidays.head mustEqual hol
      }
    }
  }

  "Annual leave get endpoint" should {
    "return all the holidays for the given employee in the given leave year (leave year the date is in should be used)" in new Context {
      //      Given I have the following holidays in the database
      val hol1 = new Holiday(1, 1, new DateTime(2016, 11, 1, 0, 0), "PM", "Annual Leave")
      val hol2 = new Holiday(2, 1, new DateTime(2016, 11, 2, 0, 0), "PM", "Annual Leave")
      val hol3_other_employee = new Holiday(3, 2, new DateTime(2016, 11, 3, 0, 0), "PM", "Annual Leave")
      val hol4_wrong_leave_year = new Holiday(4, 1, new DateTime(2017, 11, 3, 0, 0), "PM", "Annual Leave")
      Await.result(testDAOsForSetup.insertHolidays(Seq(hol1, hol2, hol3_other_employee, hol4_wrong_leave_year)), Duration.Inf)

      //      And there are matching employees in the database
      val startOfPeriod = new DateTime(2016, 4, 1, 0, 0)
      val endOfPeriod = startOfPeriod.plusYears(1).minusDays(1)
      val entitlement1 = LeaveEntitlement(1, 1, startOfPeriod, endOfPeriod, 25)
      val entitlement2 = LeaveEntitlement(2, 1, startOfPeriod.minusYears(1), endOfPeriod.minusYears(1), 25)
      Await.result(testDAOsForSetup.insertLeaveEntitlements(Seq(entitlement1, entitlement2)), Duration.Inf)

      //      Then when I look for holidays for that employee for that leave year, only the correct results are returned
      Get("/holidays/employees/1?leave-year=2016-10-01") ~> holidaysService.holidays ~> check {
        val returnedHolidays = responseAs[String].parseJson.convertTo[Seq[Holiday]]
        returnedHolidays.size mustEqual 2
        returnedHolidays.filter(_.id == 1).head mustEqual hol1
        returnedHolidays.filter(_.id == 2).head mustEqual hol2
      }
    }
  }

  "Annual leave post endpoint" should {
    "add the inputted holiday for the given employee" in new Context {
      val holiday = new Holiday(2, 2, new DateTime(2016, 11, 1, 0, 0), "PM", "Annual Leave")
      val holidayJson = holiday.toJson.toString
      val postHoliday = HttpRequest(method = POST, uri = "/holidays", entity = HttpEntity(`application/json`, holidayJson))
      postHoliday ~> holidaysService.holidays ~> check {
        val response = responseAs[String]
        response mustEqual "Holiday successfully inserted"
        val employeeHolidays = Await.result(testDAOsForSetup.getEmployeeHolidays(2), Duration.Inf)
        employeeHolidays.head mustEqual holiday
      }
    }
  }
}
