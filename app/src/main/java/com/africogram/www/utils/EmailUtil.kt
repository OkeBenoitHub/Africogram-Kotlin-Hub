package com.africogram.www.utils

/**
 * Get email html template with data
 * @param toEmailArrays :: email to senders arrays
 * @param headLineTitle :: head line email title
 * @param htmlTemp :: html content
 * @param subject :: email subject
 */
fun getEmailHtmlTplWithData(toEmailArrays: ArrayList<String>,headLineTitle: String, htmlTemp: String, subject: String): HashMap<String, Any> {
    val emailMessageData = hashMapOf(
        "html" to getEmailHtmlTpl(headLineTitle,htmlTemp),
        "subject" to subject
    )
    return hashMapOf(
        "to" to toEmailArrays,
        "message" to emailMessageData
    )
}
/**
 * Get email html template
 * @param headLineTitle :: head line email title
 * @param htmlTemp :: html content
 */
private fun getEmailHtmlTpl(headLineTitle: String, htmlTemp: String): String {
    return "<div>"+
            "<table role='presentation' border='0' cellpadding='0' cellspacing='0' width='100%'>"+
            "<tr>"+
            "<td style='padding: 20px 0 30px 0;'>"+
            "<table align='center' border='0' cellpadding='0' cellspacing='0' width='100%' style='border-collapse: collapse; border: 1px solid #087f23;'>"+
            "<tr>"+
            "<td align='left' bgcolor='#087f23' style='padding-left:15px;'>"+
            "<h2 style='color:#ffffff;'>Africogram</h2>"+
            "</td>"+
            "</tr>"+
            "<tr>"+
            "<td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>"+
            "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='border-collapse: collapse;'>"+
            "<tr>"+
            "<td style='color: #153643; font-family: Arial, sans-serif;'>"+
            "<h4 style='font-size: 17px; margin: 0;'>"+headLineTitle+"</h4>"+
            "</td>"+
            "</tr>"+
            "<tr>"+
            "<td style='color: #153643; font-family: Arial, sans-serif; font-size: 16px; line-height: 24px; padding: 20px 0 30px 0;'>"+
            "<p style='margin: 0;font-size:14px;'>"+htmlTemp+"</p>"+
            "</td>"+
            "</tr>"+
            "</table>"+
            "</td>"+
            "</tr>"+
            "<tr>"+
            "<td bgcolor='#4caf50' style='padding: 15px 15px;'>"+
            "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='border-collapse: collapse;'>"+
            "<tr>"+
            "<td style='color: #fff; font-family: Arial, sans-serif; font-size: 14px;'>"+
            "<p style='margin: 0;'>&reg; Africogram&copy;2021<br/>"+
            "<a href='https://www.africogram.com' style='color: #fff;'>africogram.com</a>"+
            "<br/><br/>"+
            "<span style='position:relative;top:-3px;color:#fff;'>"+
            "<a href='https://www.africogram.com/about/terms' style='color: #fff;'>Terms</a>"+
            "&nbsp;&nbsp;|&nbsp;&nbsp;"+
            "<a href='https://www.africogram.com/about/privacy' style='color: #fff;'>Privacy</a>"+
            "</span>"+
            "</p>"+
            "</td>"+
            "</tr>"+
            "</table>"+
            "</td>"+
            "</tr>"+
            "</table>"+
            "</td>"+
            "</tr>" +
    "</div>"
}