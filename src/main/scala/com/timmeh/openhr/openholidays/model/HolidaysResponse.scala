package com.timmeh.openhr.openholidays.model

case class HolidaysResponse(leaveEntitlement: Option[LeaveEntitlement], holidays: Seq[Holiday])
