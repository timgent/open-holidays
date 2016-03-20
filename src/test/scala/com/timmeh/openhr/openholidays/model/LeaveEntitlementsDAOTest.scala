package com.timmeh.openhr.openholidays.model

import com.timmeh.openhr.openholidays.utils.TestDBContextWithSchemas
import org.joda.time.DateTime
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
class LeaveEntitlementsDAOTest extends Specification {

  class Context extends LeaveEntitlementsDAO with TestDBContextWithSchemas

  val startOfPeriod = new DateTime(2016, 4, 1, 0, 0)
  val endOfPeriod = startOfPeriod.plusYears(1).minusDays(1)
  val entitlement1 = LeaveEntitlement(1, 1, startOfPeriod, endOfPeriod, 25)
  val entitlement2 = LeaveEntitlement(2, 1, startOfPeriod.minusYears(1), endOfPeriod.minusYears(1), 25)

  "Inserting a leave entitlement should work" >> new Context {
    val returnedLeaveEntitlementsFuture = for {
      _ <- insertLeaveEntitlement(entitlement1)
      returnedLeaveEntitlements <- getEmployeeLeaveEntitlements(1)
    } yield returnedLeaveEntitlements

    val returnedLeaveEntitlements = Await.result(returnedLeaveEntitlementsFuture, Duration.Inf)

    returnedLeaveEntitlements must have size 1
  }

  "Inserting leave entitlements should work" >> new Context {
    val returnedLeaveEntitlementsFuture = for {
      _ <- insertLeaveEntitlements(Seq(entitlement1, entitlement2))
      returnedLeaveEntitlements <- getEmployeeLeaveEntitlements(1)
    } yield returnedLeaveEntitlements

    val returnedLeaveEntitlements = Await.result(returnedLeaveEntitlementsFuture, Duration.Inf)

    returnedLeaveEntitlements must have size 2
  }

  "Getting an employee leave entitlement for a particular date should work" >> new Context {
    val returnedLeaveEntitlementsFuture = for {
      _ <- insertLeaveEntitlements(Seq(entitlement1, entitlement2))
      returnedLeaveEntitlements <- getEmployeeLeaveEntitlement(1, new DateTime(2016, 5, 1, 0, 0))
    } yield returnedLeaveEntitlements

    val returnedLeaveEntitlement = Await.result(returnedLeaveEntitlementsFuture, Duration.Inf).get

    returnedLeaveEntitlement mustEqual(entitlement1)
  }
}
