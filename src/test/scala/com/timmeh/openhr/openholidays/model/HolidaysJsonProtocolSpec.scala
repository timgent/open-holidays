package com.timmeh.openhr.openholidays.model

import org.joda.time.DateTime
import org.specs2.mutable.Specification
import spray.json._

class HolidaysJsonProtocolSpec extends Specification {

  "HolidaysJsonProtocol" should {
    import com.timmeh.openhr.openholidays.model.HolidaysJsonProtocol._
    "Correctly convert from a holiday to json" in {
      val holiday = Holiday(1, 1, new DateTime(2016, 11, 1, 0, 0), "PM", "Annual Leave")
      val json = holiday.toJson.toString
      json mustEqual """{"holType":"Annual Leave","id":1,"holDayType":"PM","employeeId":1,"holDate":"2016-11-01T00:00:00.000Z"}"""
    }

    "Correctly convert from json to a holiday" in {
      val json = """{"holType":"Annual Leave","id":1,"holDayType":"PM","employeeId":1,"holDate":"2016-11-01T00:00:00.000Z"}"""
      val holiday = json.parseJson.convertTo[Holiday]
      holiday mustEqual Holiday(1, 1, new DateTime(2016, 11, 1, 0, 0), "PM", "Annual Leave")
    }
  }
}
