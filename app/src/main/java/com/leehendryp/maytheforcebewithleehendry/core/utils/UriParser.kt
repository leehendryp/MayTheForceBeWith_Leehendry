package com.leehendryp.maytheforcebewithleehendry.core.utils

import android.net.Uri

object UriParser {
    fun parseToId(url: String): Int? {
        val uri: Uri = try {
            Uri.parse(url)
        } catch (cause: Throwable) {
            throw cause
        }
        return uri.pathSegments.last().toString().toInt()
    }

    fun parseToPageNumber(url: String): Int? {
        val uri: Uri = try {
            Uri.parse(url)
        } catch (cause: Throwable) {
            throw cause
        }
        return uri.query?.replace("page=", "")?.toInt()
    }
}