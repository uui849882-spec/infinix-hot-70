package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AzkarDataProvider
import com.example.data.MushafPagesDataProvider
import com.example.data.QuranBookmarkManager
import com.example.notifications.AzkarNotificationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("المصحف الشريف", appName)
  }

  @Test
  fun `mushaf pages provider has 604 pages`() {
    assertEquals(604, MushafPagesDataProvider.TOTAL_PAGES)
    val page1 = MushafPagesDataProvider.getPage(1)
    assertEquals("الفاتحة", page1.surahName)
    assertEquals(1, page1.juzNumber)

    val page604 = MushafPagesDataProvider.getPage(604)
    assertEquals(604, page604.pageNumber)
    assertTrue(page604.verses.isNotEmpty())
  }

  @Test
  fun `azkar data provider has 132 authentic chapters`() {
    assertTrue(AzkarDataProvider.chapters.size >= 132)
    val morningAzkar = AzkarDataProvider.chapters.find { it.title.contains("الصباح") }
    assertTrue(morningAzkar != null && morningAzkar.items.isNotEmpty())
  }

  @Test
  fun `azkar notification manager has Salawat on Prophet`() {
    val hasSalawat = AzkarNotificationManager.REMINDER_LIST.any { 
        it.second.contains("اللَّهُمَّ صَلِّ")
    }
    assertTrue(hasSalawat)
  }

  @Test
  fun `bookmark persistence works correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    QuranBookmarkManager.setBookmarkPage(context, 42)
    assertEquals(42, QuranBookmarkManager.getBookmarkPage(context))
    assertTrue(QuranBookmarkManager.isPageBookmarked(context, 42))
  }
}

