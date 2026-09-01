package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FoodRepository
import com.example.data.OrderDao
import com.example.data.OrderEntity
import com.example.data.UserProfileEntity
import com.example.model.CartItem
import com.example.model.FoodAddOn
import com.example.model.FoodCategory
import com.example.model.Language
import com.example.model.MyanmarTownshipsData
import com.example.model.OrderStatus
import com.example.model.Strings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    assertEquals("Myanmar Food", appName)
  }

  @Test
  fun `verify myanmar townships dataset`() {
    val townships = MyanmarTownshipsData.allTownships
    assertTrue("Should have more than 10 townships in Yangon/Mandalay", townships.size >= 10)
    val bahan = townships.find { it.id == "ygn_bahan" }
    assertNotNull(bahan)
    assertEquals(1500, bahan?.deliveryFeeMMK)
  }

  @Test
  fun `verify cart pricing and add-ons calculation`() {
    val repo = FoodRepository(createMockOrderDao())
    val mohinga = repo.menuItems.first { it.id == "food_mohinga" }
    val eggAddon = FoodAddOn("addon_egg", "Boiled Duck Egg", "ဘဲဥပြုတ်", 500)
    
    val cartItem = CartItem(
      foodItem = mohinga,
      quantity = 2,
      selectedAddOns = listOf(eggAddon)
    )
    
    // Single item = 3500 + 500 = 4000. For qty 2 = 8000 MMK
    assertEquals(4000, cartItem.unitPriceWithAddOns)
    assertEquals(8000, cartItem.totalPrice)
  }

  @Test
  fun `verify bilingual currency and status strings`() {
    val priceTextBurmese = Strings.mmkCurrency(Language.BURMESE, 3500)
    val priceTextEnglish = Strings.mmkCurrency(Language.ENGLISH, 3500)
    
    assertTrue(priceTextBurmese.contains("ကျပ်"))
    assertTrue(priceTextEnglish.contains("MMK"))
    
    assertEquals("မီးဖိုချောင်တွင် ပြင်ဆင်နေပါသည်", OrderStatus.PREPARING.title(Language.BURMESE))
    assertEquals("Kitchen Preparing", OrderStatus.PREPARING.title(Language.ENGLISH))
    assertEquals(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.PREPARING.nextStatus())
  }

  @Test
  fun `verify menu filtering by category`() {
    val repo = FoodRepository(createMockOrderDao())
    val allItems = repo.menuItems
    assertTrue(allItems.isNotEmpty())
    
    val noodles = allItems.filter { it.category == FoodCategory.NOODLES }
    assertTrue("Should contain noodle items", noodles.isNotEmpty())
    assertTrue(noodles.all { it.category == FoodCategory.NOODLES })
  }

  @Test
  fun `verify kbzpay account details and localized strings`() {
    val kbzNumber = Strings.kbzAccountNumber()
    assertTrue(kbzNumber.isNotEmpty())
    assertTrue(kbzNumber.contains("09-789"))

    val accNameBurmese = Strings.kbzAccountName(Language.BURMESE)
    val accNameEnglish = Strings.kbzAccountName(Language.ENGLISH)
    assertTrue(accNameBurmese.contains("ဒေါ်ပွင့်စံ"))
    assertTrue(accNameEnglish.contains("Daw Pwint San"))

    val titleMm = Strings.kbzPayTitle(Language.BURMESE)
    val titleEn = Strings.kbzPayTitle(Language.ENGLISH)
    assertTrue(titleMm.contains("KBZPay"))
    assertTrue(titleEn.contains("KBZPay"))
  }

  private fun createMockOrderDao(): OrderDao {
    return object : OrderDao {
      override fun getAllOrders(): Flow<List<OrderEntity>> = flowOf(emptyList())
      override fun getOrderById(orderId: String): Flow<OrderEntity?> = flowOf(null)
      override fun getLatestActiveOrder(): Flow<OrderEntity?> = flowOf(null)
      override suspend fun insertOrder(order: OrderEntity) {}
      override suspend fun updateOrder(order: OrderEntity) {}
      override suspend fun updateOrderStatus(orderId: String, status: String, updatedAt: Long) {}
      override suspend fun deleteOrder(orderId: String) {}
      override fun getUserProfile(): Flow<UserProfileEntity?> = flowOf(null)
      override suspend fun saveUserProfile(profile: UserProfileEntity) {}
    }
  }
}

