package com.thuypham.ptithcm.mytiki.util

import android.content.Context
import android.os.Handler
import com.thuypham.ptithcm.mytiki.R
import java.text.DecimalFormat
import java.util.regex.Pattern
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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
        else -> 30
    }
}

fun formatNumber(number: Int) =
    if (number.toString().length == 1) "0$number" else number.toString()


fun formatPrice(price: Long?) = DecimalFormat("#,###,###").format(price) + " đ"

fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}


fun Context.sendMail(emailReceive: String, randomStr :String): MimeMessage {
    val from = "congnghephanmemptithcm@gmail.com" //Sender email
    val properties = System.getProperties()

    with(properties) {
        put("mail.smtp.host", "smtp.mail.yahoo.com") //Configure smtp host
        put("mail.smtp.port", "587") //Configure port
        put("mail.smtp.starttls.enable", "true") //Enable TLS
        put("mail.smtp.auth", "true") //Enable authentication
    }

    val auth = object : Authenticator() {
        override fun getPasswordAuthentication() =
            PasswordAuthentication(from, "quankhung123") //Credentials of the sender email
    }

    val session = Session.getDefaultInstance(properties, auth)
    val message = MimeMessage(session)

    with(message) {
        setFrom(InternetAddress(from))
        addRecipient(Message.RecipientType.TO, InternetAddress(emailReceive))
        subject = getString(R.string.confirmOrder) //Email subject
        setContent(
            "<html><body><h1>${getString(R.string.emailContent)}$randomStr</h1></body></html>",
            "text/html; charset=utf-8"
        ) //Sending html message, you may change to send text here.
    }

    return message
}