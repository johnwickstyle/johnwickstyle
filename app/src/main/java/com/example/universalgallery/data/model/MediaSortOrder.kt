package com.example.universalgallery.data.model

enum class MediaSortOrder(val sql: String) {
    DATE_DESC("datetaken DESC, date_modified DESC, _id DESC"),
    DATE_ASC("datetaken ASC, date_modified ASC, _id ASC"),
    NAME_ASC("_display_name COLLATE NOCASE ASC"),
    NAME_DESC("_display_name COLLATE NOCASE DESC"),
    SIZE_DESC("_size DESC"),
}
