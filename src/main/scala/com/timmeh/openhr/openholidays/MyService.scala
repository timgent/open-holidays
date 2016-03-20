package com.timmeh.openhr.openholidays

import akka.actor.Actor
import com.timmeh.openhr.openholidays.model.{LeaveEntitlementsDAO, DB, HolidaysDAO, Holiday}
import spray.http.StatusCodes.Success
import spray.routing._
import spray.http._
import MediaTypes._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._
import com.timmeh.openhr.openholidays.model.HolidaysJsonProtocol._
import org.joda.time.DateTime
import scala.concurrent.duration.Duration

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(holidays)
}


// this trait defines our service behavior independently from the service actor
// TODO: Check out dependency injection options so I don't create this monster trait that contains the whole world
trait MyService extends HttpService with HolidaysDAO with LeaveEntitlementsDAO {
  val db = DB.db

//  TODO: These are getting messy, extract the detail out into Controllers
  val holidays =
    path("holidays" / "employees" / IntNumber) { employeeId =>
      get {
        parameters("leave-year") { leaveYear =>
          respondWithMediaType(`application/json`) {
            onSuccess(getEmployeeLeaveEntitlement(employeeId, new DateTime(leaveYear))) {
              case None => complete("No employee data found for the given year")
              case Some(leaveEntitlement) => {
                onSuccess(getEmployeeHolidays(employeeId, leaveEntitlement.leavePeriodStartDate, leaveEntitlement.leavePeriodEndDate)) { employeeHols =>
                  complete(employeeHols.toJson.toString)
                }
              }
            }
          }
        } ~
        respondWithMediaType(`application/json`) {
          onSuccess(getEmployeeHolidays(employeeId)) { employeeHols =>
            complete(employeeHols.toJson.toString)
          }
        }
      }
    } ~
      path("holidays") {
        post {
          respondWithStatus(StatusCodes.OK) {
            entity(as[Holiday]) { newHol =>
              onSuccess(insertHoliday(newHol)) { numInserted =>
                complete("Holiday successfully inserted")
              }
            }
          }
        }
      }
}