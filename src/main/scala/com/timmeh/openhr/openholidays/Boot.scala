package com.timmeh.openhr.openholidays

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import slick.driver.H2Driver.api._

object Boot extends App {

  // DB setup
  val db = Database.forConfig("holidaysdb")

  try {
    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("on-spray-can")

    // create and start our service actor
    val service = system.actorOf(Props[HolidaysServiceActor], "holidays-service")

    implicit val timeout = Timeout(5.seconds)
    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

  } finally db.close
}
