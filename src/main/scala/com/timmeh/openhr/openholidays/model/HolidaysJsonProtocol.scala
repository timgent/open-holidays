package com.timmeh.openhr.openholidays.model

import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport
import spray.json._

object HolidaysJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object ColorJsonFormat extends RootJsonFormat[Holiday] {
    def write(hol: Holiday) = JsObject(
      "id" -> JsNumber(hol.id),
      "employeeId" -> JsNumber(hol.employeeId),
      "holDate" -> JsString(hol.holDate.toString),
      "holDayType" -> JsString(hol.holDayType),
      "holType" -> JsString(hol.holType)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "employeeId", "holDate", "holDayType", "holType") match {
        case Seq(JsNumber(id), JsNumber(employeeId), JsString(holDate), JsString(holDayType), JsString(holType)) =>
          Holiday(id.toInt, employeeId.toInt, new DateTime(holDate), holDayType, holType)
        case _ => throw new DeserializationException("Color expected")
      }
    }
  }
}
