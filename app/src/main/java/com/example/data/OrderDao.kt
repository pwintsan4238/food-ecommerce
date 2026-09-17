package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE status != 'DELIVERED' AND status != 'CANCELLED' ORDER BY timestamp DESC LIMIT 1")
    fun getLatestActiveOrder(): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, statusUpdatedAt = :updatedAt WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET paymentStatus = :paymentStatus WHERE orderId = :orderId")
    suspend fun updatePaymentStatus(orderId: String, paymentStatus: String)

    @Query("UPDATE orders SET trackingNumber = :trackingNumber, deliveryPartnerName = :partnerName WHERE orderId = :orderId")
    suspend fun updateTrackingInfo(orderId: String, trackingNumber: String, partnerName: String)

    @Query("UPDATE orders SET status = 'CANCELLED', cancellationReason = :reason, cancelledAt = :cancelledAt, statusUpdatedAt = :cancelledAt WHERE orderId = :orderId")
    suspend fun cancelOrderWithReason(orderId: String, reason: String, cancelledAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET returnStatus = 'REQUESTED', returnReason = :returnReason, returnRequestedAt = :requestedAt WHERE orderId = :orderId")
    suspend fun requestOrderReturn(orderId: String, returnReason: String, requestedAt: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET returnStatus = :returnStatus, paymentStatus = :paymentStatus WHERE orderId = :orderId")
    suspend fun updateReturnStatus(orderId: String, returnStatus: String, paymentStatus: String)

    @Query("UPDATE orders SET deliveredAt = :deliveredAt WHERE orderId = :orderId")
    suspend fun updateDeliveredAt(orderId: String, deliveredAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: String)

    // User Profile Queries
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile WHERE id = 1")
    suspend fun clearUserProfile()

    // Products (Seafood Menu Catalog)
    @Query("SELECT * FROM products ORDER BY nameEn ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)

    @Query("UPDATE products SET isArchived = :isArchived WHERE id = :id")
    suspend fun setProductArchived(id: String, isArchived: Boolean)

    @Query("UPDATE products SET stockQuantity = :quantity WHERE id = :id")
    suspend fun updateProductStock(id: String, quantity: Int)

    @Query("UPDATE products SET priceMMK = :price WHERE id = :id")
    suspend fun updateProductPrice(id: String, price: Int)

    // Business & Brand Settings
    @Query("SELECT * FROM business_settings WHERE id = 1 LIMIT 1")
    fun getBusinessSettings(): Flow<BusinessSettingsEntity?>

    @Query("SELECT COUNT(*) FROM business_settings")
    suspend fun getBusinessSettingsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBusinessSettings(settings: BusinessSettingsEntity)

    // Categories
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, nameEn ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("UPDATE categories SET isArchived = :isArchived WHERE id = :id")
    suspend fun setCategoryArchived(id: String, isArchived: Boolean)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: String)

    // Brands
    @Query("SELECT * FROM brands ORDER BY nameEn ASC")
    fun getAllBrands(): Flow<List<BrandEntity>>

    @Query("SELECT COUNT(*) FROM brands")
    suspend fun getBrandCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrands(brands: List<BrandEntity>)

    @Update
    suspend fun updateBrand(brand: BrandEntity)

    @Query("UPDATE brands SET isArchived = :isArchived WHERE id = :id")
    suspend fun setBrandArchived(id: String, isArchived: Boolean)

    @Query("DELETE FROM brands WHERE id = :id")
    suspend fun deleteBrand(id: String)

    // User Accounts
    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    fun getAllUserAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserAccountCount(): Int

    @Query("SELECT * FROM user_accounts WHERE phone = :phone LIMIT 1")
    suspend fun getUserAccountByPhone(phone: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccounts(users: List<UserAccountEntity>)

    @Query("UPDATE user_accounts SET name = :name, email = :email, preferredTownshipId = :preferredTownshipId, defaultAddressNote = :defaultAddressNote, adminNotes = :adminNotes WHERE phone = :phone")
    suspend fun updateUserAccountDetails(phone: String, name: String, email: String, preferredTownshipId: String, defaultAddressNote: String, adminNotes: String)

    @Query("DELETE FROM user_accounts WHERE phone = :phone")
    suspend fun deleteUserAccount(phone: String)

    // Customer Reviews & Ratings
    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY timestamp DESC")
    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Query("UPDATE reviews SET adminReply = :reply WHERE id = :reviewId")
    suspend fun replyToReview(reviewId: String, reply: String)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: String)

    // Customer Messages & Contact Requests
    @Query("SELECT * FROM customer_messages ORDER BY timestamp DESC")
    fun getAllCustomerMessages(): Flow<List<CustomerMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerMessage(message: CustomerMessageEntity)

    @Update
    suspend fun updateCustomerMessage(message: CustomerMessageEntity)

    @Query("UPDATE customer_messages SET status = :status, adminReply = :adminReply, repliedAt = :repliedAt WHERE id = :id")
    suspend fun replyToCustomerMessage(id: String, adminReply: String, status: String, repliedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM customer_messages WHERE id = :id")
    suspend fun deleteCustomerMessage(id: String)
}
