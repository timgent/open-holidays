package com.timmeh.openhr.openholidays

import akka.actor.Actor
import com.timmeh.openhr.openholidays.controller.HolidaysController
import com.timmeh.openhr.openholidays.model._
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
class HolidaysServiceActor extends Actor {
  val holidaysService = new HolidaysService with HolidaysController with HolidaysDAO with LeaveEntitlementsDAO {
    val db = DB.db
    def actorRefFactory = context
  }

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = holidaysService.runRoute(holidaysService.holidays)
}


// this trait defines our service behavior independently from the service actor
// TODO: Check out dependency injection options so I don't create this monster trait that contains the whole world
trait HolidaysService extends HttpService {
  this: HolidaysDAO with LeaveEntitlementsDAO with HolidaysController =>

//  TODO: These are getting messy, extract the detail out into Controllers
  val holidays =
    path("holidays" / "employees" / IntNumber) { employeeId =>
      get {
        parameters("leave-year") { leaveYear =>
          respondWithMediaType(`application/json`) {
            onSuccess(holsAndCurrentLeaveYearForEmployee(new DateTime(leaveYear), employeeId)) {
              case res@HolidaysResponse(Some(leaveEntitlement), _) => complete(res)
              case HolidaysResponse(None, _) => complete("No leave year data for this period")
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