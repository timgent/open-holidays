package com.timmeh.openhr.openholidays.model

import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport
import spray.json._

object HolidaysJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object JodaDateTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(dateTime: DateTime) = JsString(dateTime.toString)

    def read(dateTime: JsValue) = dateTime match {
      case JsString(dt) => new DateTime(dt)
      case _ => throw new DeserializationException("DateTime expected")
    }
  }

  implicit val holidaysJsonFormat = jsonFormat5(Holiday)
  implicit val leaveEntitlementsFormat = jsonFormat5(LeaveEntitlement)
  implicit val holidaysResponseFormat = jsonFormat2(HolidaysResponse)
}
