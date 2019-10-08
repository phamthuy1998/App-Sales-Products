package com.thuypham.ptithcm.mytiki.help

import android.os.Handler
import java.util.regex.Pattern

fun after(delay: Long, process: () -> Unit) {
    Handler().postDelayed({
        process()
    }, delay)
}
fun isEmailValid(email: String): Boolean {
    val expression = "^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}\$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
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

