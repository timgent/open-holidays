package com.timmeh.openhr.openholidays

import akka.actor.Actor
import com.timmeh.openhr.openholidays.model.{DB, HolidaysDAO, Holiday}
import spray.http.StatusCodes.Success
import spray.routing._
import spray.http._
import MediaTypes._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._
import com.timmeh.openhr.openholidays.model.HolidaysJsonProtocol._

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
trait MyService extends HttpService with HolidaysDAO {
  val db = DB.db

  val holidays =
    path("holidays" / "employees" / IntNumber) { employeeId =>
      get {
        respondWithMediaType(`application/json`) {
          onSuccess(getEmployeeHoliday(employeeId)) { employeeHols =>
            complete(employeeHols.toJson.toString)
          }
        }
      }
    } ~
      path("holidays") {
        post {
          respondWithStatus(StatusCodes.OK) {
            entity(as[Holiday]) { newHol =>
              onSuccess(insertHoliday(newHol)) {numInserted =>
                complete("Holiday successfully inserted")
              }
            }
          }
        }
      }
}