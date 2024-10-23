package com.badrun.my259firdaus.model

import java.util.Calendar

data class calendarModel(
    var date: Int,
    var month: Int,
    var year : Int,
    var calendarCompare : Calendar,
    var status : String?
)
