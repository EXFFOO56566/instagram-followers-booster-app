package com.ohayo.core.helper

object RegexHelpers {

    const val REGEX_HASH_TAG = "(#[aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬbBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆ\n" +
            "fFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTu\n" +
            "UùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ0123456789_]+)"

    const val REGEX_HYPERLINK = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})"

    @JvmStatic
    fun extractHashTags(introText: String?): List<String>? {
        if (introText.isNullOrEmpty()) {
            return null
        }

        val regex = Regex(REGEX_HASH_TAG)
        val hashTags = regex.findAll(introText).map { it.value }.distinct().toList()
        return hashTags.ifEmpty {
            return null
        }
    }

    fun extractHyperlink(introText: String?): List<String>? {
        if (introText.isNullOrEmpty()) {
            return null
        }

        val regex = Regex(REGEX_HYPERLINK)
        val urls = regex.findAll(introText).map { it.value }.toList()
        return urls.ifEmpty {
            return null
        }
    }
}