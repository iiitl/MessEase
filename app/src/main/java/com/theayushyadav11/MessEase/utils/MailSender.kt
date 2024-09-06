package com.theayushyadav11.MessEase.utils

import android.os.AsyncTask
import java.net.URL
import java.util.Properties
import javax.activation.DataHandler
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import org.apache.commons.io.IOUtils
import javax.mail.util.ByteArrayDataSource

class MailSender(private val userEmail: String, private val userPassword: String) {

    fun sendEmailWithAttachment(
        toEmail: String,
        subject: String,
        messageBody: String,
        fileDownloadUrl: String
    ) {
        SendMailTask().execute(toEmail, subject, messageBody, fileDownloadUrl)
    }

    inner class SendMailTask : AsyncTask<String, Void, Void>() {
        override fun doInBackground(vararg params: String): Void? {
            val toEmail = params[0]
            val subject = params[1]
            val messageBody = params[2]
            val fileDownloadUrl = params[3]

            val properties = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(userEmail, userPassword) // Use your app-specific password
                }
            })

            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(userEmail))
                    addRecipient(Message.RecipientType.TO, InternetAddress(toEmail))
                    this.subject = subject

                    // Create the message part
                    val messageBodyPart = MimeBodyPart()
                    messageBodyPart.setText(messageBody)

                    // Create a multipart message for attachment
                    val multipart = MimeMultipart()
                    multipart.addBodyPart(messageBodyPart)

                    // Part two is attachment
                    val attachmentBodyPart = MimeBodyPart()
                    val fileInputStream = URL(fileDownloadUrl).openStream()

                    // Convert InputStream to ByteArrayDataSource
                    val byteArray = IOUtils.toByteArray(fileInputStream)
                    val dataSource = ByteArrayDataSource(byteArray, "application/octet-stream")

                    attachmentBodyPart.dataHandler = DataHandler(dataSource)
                    attachmentBodyPart.fileName = "MessMenu.pdf"
                    multipart.addBodyPart(attachmentBodyPart)

                    // Set the multipart message to the email
                    setContent(multipart)
                }

                Transport.send(message)
                println("Email sent successfully with attachment.")
            } catch (e: MessagingException) {
                e.printStackTrace()
            }

            return null
        }
    }
}
