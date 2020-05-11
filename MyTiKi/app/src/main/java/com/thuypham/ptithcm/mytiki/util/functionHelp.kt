package com.thuypham.ptithcm.mytiki.util

import android.os.Handler
import java.util.regex.Pattern

fun after(delay: Long, process: () -> Unit) {
    Handler().postDelayed({
        process()
    }, delay)
}

fun isEmailValid(email: String): Boolean {
    val expression = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    val pattern = Pattern.compile(expression)
    val matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isPasswordValid(password: String): Boolean {
    val expression = "^(?=.*[a-z])(?!.* )(?=.*[0-9]).{6,}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(password)
    return matcher.matches()
}

fun isPhoneValid(phone: String): Boolean {
    val expression = "(09|01|02|03|04|05|06|07|08)+([0-9]{7,11})\\b"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(phone)
    return matcher.matches()
}

fun getDayInMonth(month: Int, year: Int) = run {
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) 29 else 28
        else -> null
    }
}

