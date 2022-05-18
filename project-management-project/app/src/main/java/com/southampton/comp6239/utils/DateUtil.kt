package com.southampton.comp6239.utils


import java.text.SimpleDateFormat
import java.util.*


class DateUtil {

    companion object{

        val  YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss"
        val  YEAR_MONTH_DAY= "yyyy-MM-dd"
        val  HOUR_MINUTE_SECOND= "HH:mm:ss"

        fun parseToDate(year : Int, month : Int, day : Int) : Date{
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            return cal.getTime()
        }

        fun getStringDate(date :Date, format : String):String{
            val formatter = SimpleDateFormat(format)
            return formatter.format(date)
        }

    }

}