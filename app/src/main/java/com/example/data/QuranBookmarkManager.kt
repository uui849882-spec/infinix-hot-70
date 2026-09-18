package com.example.data

import android.content.Context
import android.content.SharedPreferences

object QuranBookmarkManager {

    private const val PREFS_NAME = "quran_bookmark_prefs"
    private const val KEY_BOOKMARK_PAGE = "bookmark_page"
    private const val KEY_LAST_PAGE = "last_page"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getBookmarkPage(context: Context): Int {
        return getPrefs(context).getInt(KEY_BOOKMARK_PAGE, 1)
    }

    fun setBookmarkPage(context: Context, page: Int) {
        getPrefs(context).edit().putInt(KEY_BOOKMARK_PAGE, page).apply()
    }

    fun isPageBookmarked(context: Context, page: Int): Boolean {
        return getBookmarkPage(context) == page
    }

    fun getLastReadPage(context: Context): Int {
        return getPrefs(context).getInt(KEY_LAST_PAGE, 1)
    }

    fun setLastReadPage(context: Context, page: Int) {
        getPrefs(context).edit().putInt(KEY_LAST_PAGE, page).apply()
    }
}
