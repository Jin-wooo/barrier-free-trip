package com.triply.barrierfreetrip.util

fun String.toUIString(): String {
    return this
        .replace("_무장애 편의시설", "")
        .replace("&nbsp;", "\n")
        .replace("<div>", "")
        .replace("</div>", "")
        .replace("<br>", " ")
        .replace("</br>", " ")
        .replace("<br/>", " ")
}

fun convertHomepageToURL(homepage: String): String {
    val firstCloseIndex = homepage.indexOfFirst { c: Char -> c == '>' }
    val lastOpenIndex = homepage.indexOfLast { c: Char -> c == '<' }

    if (firstCloseIndex >= lastOpenIndex) return ""
    return homepage.substring(firstCloseIndex+1, lastOpenIndex)
}