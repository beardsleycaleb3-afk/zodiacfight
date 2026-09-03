package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ChineseAnimal
import com.example.model.GuardianFusion
import com.example.model.MonthZodiac
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    assertEquals("Zodiac Fusion Arena", appName)
  }

  @Test
  fun `test zodiac month mapping`() {
    assertEquals(MonthZodiac.CAPRICORN, MonthZodiac.fromMonth(1))
    assertEquals(MonthZodiac.AQUARIUS, MonthZodiac.fromMonth(2))
    assertEquals(MonthZodiac.PISCES, MonthZodiac.fromMonth(3))
    assertEquals(MonthZodiac.ARIES, MonthZodiac.fromMonth(4))
    assertEquals(MonthZodiac.TAURUS, MonthZodiac.fromMonth(5))
    assertEquals(MonthZodiac.GEMINI, MonthZodiac.fromMonth(6))
    assertEquals(MonthZodiac.CANCER, MonthZodiac.fromMonth(7))
    assertEquals(MonthZodiac.LEO, MonthZodiac.fromMonth(8))
    assertEquals(MonthZodiac.VIRGO, MonthZodiac.fromMonth(9))
    assertEquals(MonthZodiac.LIBRA, MonthZodiac.fromMonth(10))
    assertEquals(MonthZodiac.SCORPIO, MonthZodiac.fromMonth(11))
    assertEquals(MonthZodiac.SAGITTARIUS, MonthZodiac.fromMonth(12))
  }

  @Test
  fun `test chinese animal year mapping anchored to 2020 as rat`() {
    assertEquals(ChineseAnimal.RAT, ChineseAnimal.fromYear(2020))
    assertEquals(ChineseAnimal.OX, ChineseAnimal.fromYear(2021))
    assertEquals(ChineseAnimal.TIGER, ChineseAnimal.fromYear(2022))
    assertEquals(ChineseAnimal.RABBIT, ChineseAnimal.fromYear(2023))
    assertEquals(ChineseAnimal.DRAGON, ChineseAnimal.fromYear(2024))
    assertEquals(ChineseAnimal.SNAKE, ChineseAnimal.fromYear(2025))
    assertEquals(ChineseAnimal.PIG, ChineseAnimal.fromYear(2019))
    assertEquals(ChineseAnimal.DRAGON, ChineseAnimal.fromYear(2000))
  }

  @Test
  fun `test fusion guardian title`() {
    val fusion = GuardianFusion(
      zodiac = MonthZodiac.LEO,
      animal = ChineseAnimal.DRAGON,
      birthMonth = 8,
      birthYear = 2000
    )
    assertEquals("Leo–Dragon Guardian", fusion.title)
    assertEquals("Leo-Dragon", fusion.comboTitle)
  }
}
