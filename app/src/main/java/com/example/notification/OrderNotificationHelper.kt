package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.model.Language
import com.example.model.OrderStatus

object OrderNotificationHelper {
    const val CHANNEL_ID = "myanmar_food_orders_channel"
    private const val CHANNEL_NAME = "Myanmar Food Orders"
    private const val CHANNEL_DESC = "Real-time updates for your Myanmar food delivery orders"

    fun createNotificationChannel(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = CHANNEL_DESC
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                }
                val notificationManager: NotificationManager? =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.createNotificationChannel(channel)
            }
        } catch (_: Throwable) {
            // Safe fallback
        }
    }

    fun showOrderStatusNotification(
        context: Context,
        orderId: String,
        status: OrderStatus,
        lang: Language,
        itemsSummary: String
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_ORDER_ID", orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            orderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (status) {
            OrderStatus.PLACED -> if (lang == Language.BURMESE) "အော်ဒါ #$orderId လက်ခံရရှိပါပြီ 🎉" else "Order #$orderId Confirmed 🎉"
            OrderStatus.PREPARING -> if (lang == Language.BURMESE) "အော်ဒါ ပြင်ဆင်နေပါပြီ 🍳" else "Order Preparing 🍳"
            OrderStatus.OUT_FOR_DELIVERY -> if (lang == Language.BURMESE) "ပို့ဆောင်သူ ထွက်ခွာလာပါပြီ 🛵" else "Rider is on the way 🛵"
            OrderStatus.DELIVERED -> if (lang == Language.BURMESE) "အစားအစာ ရောက်ရှိပါပြီ! စားသုံးပါ 😋" else "Food Delivered! Enjoy your meal 😋"
            OrderStatus.CANCELLED -> if (lang == Language.BURMESE) "အော်ဒါ #$orderId ပယ်ဖျက်ပြီးပါပြီ" else "Order #$orderId Cancelled"
        }

        val contentText = "${status.description(lang)} • $itemsSummary"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(orderId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Notification permission not granted yet on Android 13+
        }
    }

    fun showPaymentReminderNotification(
        context: Context,
        orderId: String,
        amountMMK: Int,
        lang: Language,
        remainingMinutes: Int,
        accountHolder: String = "",
        contactPhone: String = ""
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_ORDER_ID", orderId)
            putExtra("EXTRA_OPEN_KBZ_PAY", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            ("PAY_" + orderId).hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (lang == Language.BURMESE) {
            "⏰ KBZPay ငွေပေးချေရန် သတိပေးချက် ($remainingMinutes မိနစ် ကျန်)"
        } else {
            "⏰ KBZPay Payment Reminder ($remainingMinutes min remaining)"
        }

        val amountStr = if (lang == Language.BURMESE) "$amountMMK ကျပ်" else "$amountMMK MMK"
        val holderText = if (accountHolder.isNotBlank()) accountHolder else (if (lang == Language.BURMESE) "ဒေါ်ပွင့်စံ" else "Daw Pwint San")
        val phoneText = if (contactPhone.isNotBlank()) " - $contactPhone" else ""
        val contentText = if (lang == Language.BURMESE) {
            "အော်ဒါ #$orderId အတွက် KBZPay ဖြင့် $amountStr ကို လွှဲပေးပါရန်။ အကောင့်: $holderText$phoneText"
        } else {
            "Please complete your KBZPay payment of $amountStr for Order #$orderId. Acc: $holderText$phoneText"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(("PAY_" + orderId).hashCode(), notification)
        } catch (e: SecurityException) {
            // Notification permission not granted yet
        }
    }
}
