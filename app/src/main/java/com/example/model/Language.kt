package com.example.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    BURMESE("my", "Burmese", "မြန်မာ"),
    ENGLISH("en", "English", "English")
}

object Strings {
    fun appTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "မြန်မာအစားအစာ အော်ဒါ"
        Language.ENGLISH -> "Myanmar Food Order"
    }

    fun appSubtitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အရသာရှိသော အစားအစာများကို လျင်မြန်စွာ မှာယူလိုက်ပါ"
        Language.ENGLISH -> "Delicious Myanmar bites delivered fast to your door"
    }

    fun searchPlaceholder(lang: Language): String = when (lang) {
        Language.BURMESE -> "အစားအစာ သို့မဟုတ် ဟင်းလျာ ရှာဖွေပါ..."
        Language.ENGLISH -> "Search food, noodles, curries..."
    }

    fun categoryAll(lang: Language): String = when (lang) {
        Language.BURMESE -> "အားလုံး"
        Language.ENGLISH -> "All"
    }

    fun categoryNoodles(lang: Language): String = when (lang) {
        Language.BURMESE -> "ခေါက်ဆွဲ/မုန့်ဟင်းခါး"
        Language.ENGLISH -> "Noodles & Soups"
    }

    fun categoryRice(lang: Language): String = when (lang) {
        Language.BURMESE -> "ထမင်းနှင့်ဟင်း"
        Language.ENGLISH -> "Rice & Curries"
    }

    fun categorySalads(lang: Language): String = when (lang) {
        Language.BURMESE -> "အသုပ်နှင့်အကြော်"
        Language.ENGLISH -> "Salads & Snacks"
    }

    fun categoryDrinks(lang: Language): String = when (lang) {
        Language.BURMESE -> "အချိုရည်နှင့်လက်ဖက်ရည်"
        Language.ENGLISH -> "Drinks & Tea"
    }

    fun popularDishes(lang: Language): String = when (lang) {
        Language.BURMESE -> "လူကြိုက်အများဆုံး အစားအစာများ"
        Language.ENGLISH -> "Popular Myanmar Favorites"
    }

    fun addToCart(lang: Language): String = when (lang) {
        Language.BURMESE -> "ခြင်းတောင်းထဲထည့်မည်"
        Language.ENGLISH -> "Add to Cart"
    }

    fun proceedToCheckout(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါဆက်လက်တင်မည်"
        Language.ENGLISH -> "Proceed to Checkout"
    }

    fun proceed(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဆက်လက်လုပ်ဆောင်မည်"
        Language.ENGLISH -> "Proceed"
    }

    fun proceedArrow(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဆက်သွားမည် →"
        Language.ENGLISH -> "Proceed →"
    }

    fun cart(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါခြင်းတောင်း"
        Language.ENGLISH -> "My Cart"
    }

    fun emptyCartTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "ခြင်းတောင်းထဲတွင် အစားအစာမရှိသေးပါ"
        Language.ENGLISH -> "Your cart is empty"
    }

    fun emptyCartSubtitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အရသာရှိသော မြန်မာအစားအစာများကို ရွေးချယ်မှာယူပါ"
        Language.ENGLISH -> "Explore our menu and add some delicious food!"
    }

    fun orderNow(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါတင်မည်"
        Language.ENGLISH -> "Place Order Now"
    }

    fun quickCustomerInfo(lang: Language): String = when (lang) {
        Language.BURMESE -> "မှာယူသူ အချက်အလက် (အကောင့်ဖွင့်ရန်မလိုပါ)"
        Language.ENGLISH -> "Customer Info (No Registration Needed)"
    }

    fun customerName(lang: Language): String = when (lang) {
        Language.BURMESE -> "မှာယူသူအမည်"
        Language.ENGLISH -> "Your Name"
    }

    fun customerPhone(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဖုန်းနံပါတ် (ဥပမာ 09xxxxxxxxx)"
        Language.ENGLISH -> "Phone Number (e.g. 09xxxxxxxxx)"
    }

    fun selectTownship(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်မည့် မြို့နယ်ရွေးချယ်ပါ"
        Language.ENGLISH -> "Select Delivery Township"
    }

    fun deliveryAddressNote(lang: Language): String = when (lang) {
        Language.BURMESE -> "လမ်း/တိုက်/အခန်း အမှတ် (ရွေးချယ်ရန်)"
        Language.ENGLISH -> "Street / Building / Unit (Optional)"
    }

    fun paymentMethod(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေချေစနစ်"
        Language.ENGLISH -> "Payment Method"
    }

    fun cod(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပစ္စည်းရောက်မှ ငွေချေမည် (COD)"
        Language.ENGLISH -> "Cash on Delivery (COD)"
    }

    fun kpay(lang: Language): String = when (lang) {
        Language.BURMESE -> "KBZPay (ကေဘီဇက်ပေး)"
        Language.ENGLISH -> "KBZPay (KPay)"
    }

    fun wavepay(lang: Language): String = when (lang) {
        Language.BURMESE -> "WavePay (ဝေ့ဗ်ပလပ်စ်)"
        Language.ENGLISH -> "WavePay"
    }

    fun subtotal(lang: Language): String = when (lang) {
        Language.BURMESE -> "အစားအစာ ကျသင့်ငွေ"
        Language.ENGLISH -> "Food Subtotal"
    }

    fun deliveryFee(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်ခ"
        Language.ENGLISH -> "Delivery Fee"
    }

    fun grandTotal(lang: Language): String = when (lang) {
        Language.BURMESE -> "စုစုပေါင်း ကျသင့်ငွေ"
        Language.ENGLISH -> "Grand Total"
    }

    fun mmkCurrency(lang: Language, amount: Int): String = when (lang) {
        Language.BURMESE -> "$amount ကျပ်"
        Language.ENGLISH -> "$amount MMK"
    }

    fun orderTrackingTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါ အခြေအနေ စောင့်ကြည့်ရန်"
        Language.ENGLISH -> "Live Order Tracking"
    }

    fun orderHistoryTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "မှာယူခဲ့သော မှတ်တမ်း"
        Language.ENGLISH -> "Order History"
    }

    fun noOrdersYet(lang: Language): String = when (lang) {
        Language.BURMESE -> "မှာယူထားသော အော်ဒါမရှိသေးပါ"
        Language.ENGLISH -> "No orders yet"
    }

    fun orderAgain(lang: Language): String = when (lang) {
        Language.BURMESE -> "ထပ်မံမှာယူမည်"
        Language.ENGLISH -> "Order Again"
    }

    fun trackOrder(lang: Language): String = when (lang) {
        Language.BURMESE -> "လမ်းကြောင်းကြည့်မည်"
        Language.ENGLISH -> "Track Live"
    }

    fun cancelOrder(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါ ပယ်ဖျက်မည်"
        Language.ENGLISH -> "Cancel Order"
    }

    fun orderPlacedSuccess(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါ အောင်မြင်စွာ တင်ပြီးပါပြီ!"
        Language.ENGLISH -> "Order Placed Successfully!"
    }

    fun orderPlacedDesc(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဆိုင်မှ သင့်အော်ဒါကို စတင်ပြင်ဆင်နေပါပြီ။ အခြေအနေများကို အချိန်နှင့်တပြေးညီ အသိပေးပါမည်။"
        Language.ENGLISH -> "The kitchen is preparing your meal. You'll receive real-time push notifications."
    }

    fun simulateStatusUpdate(lang: Language): String = when (lang) {
        Language.BURMESE -> "နောက်အဆင့်သို့ အခြေအနေ အတုပြောင်းကြည့်ရန်"
        Language.ENGLISH -> "Simulate Next Status Update"
    }

    fun autoTrackingActive(lang: Language): String = when (lang) {
        Language.BURMESE -> "အလိုအလျောက် အခြေအနေစောင့်ကြည့်နေသည်"
        Language.ENGLISH -> "Auto-tracking live status updates..."
    }

    fun callRider(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်သူထံ ဖုန်းခေါ်မည်"
        Language.ENGLISH -> "Call Delivery Rider"
    }

    fun homeTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပင်မစာမျက်နှာ"
        Language.ENGLISH -> "Menu"
    }

    fun trackingTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါစောင့်ကြည့်"
        Language.ENGLISH -> "Tracking"
    }

    fun historyTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "မှတ်တမ်း"
        Language.ENGLISH -> "History"
    }

    fun profileTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "အချက်အလက်"
        Language.ENGLISH -> "My Info"
    }

    fun spicyLevel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အစပ်အရသာ ရွေးရန်"
        Language.ENGLISH -> "Spiciness Level"
    }

    fun spicyMild(lang: Language): String = when (lang) {
        Language.BURMESE -> "မစပ် (ပုံမှန်)"
        Language.ENGLISH -> "Mild / Normal"
    }

    fun spicyMedium(lang: Language): String = when (lang) {
        Language.BURMESE -> "အသင့်အတင့်စပ်"
        Language.ENGLISH -> "Medium Spicy"
    }

    fun spicyHot(lang: Language): String = when (lang) {
        Language.BURMESE -> "အစပ်ကြိုက်သူများအတွက်"
        Language.ENGLISH -> "Extra Spicy 🔥"
    }

    fun addOns(lang: Language): String = when (lang) {
        Language.BURMESE -> "ထပ်ဆောင်း တွဲဖက်စာများ"
        Language.ENGLISH -> "Add-ons & Extras"
    }

    fun specialInstructions(lang: Language): String = when (lang) {
        Language.BURMESE -> "အထူးမှာကြားချက် (ဥပမာ- နံနံပင်မထည့်ပါနှင့်)"
        Language.ENGLISH -> "Special Instructions (e.g., no coriander)"
    }

    fun requiredFieldsPrompt(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကျေးဇူးပြု၍ အမည်နှင့် ဖုန်းနံပါတ် ဖြည့်ပေးပါ"
        Language.ENGLISH -> "Please enter your Name and Phone Number"
    }

    fun selectTownshipPrompt(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကျေးဇူးပြု၍ ပို့ဆောင်မည့် မြို့နယ် ရွေးချယ်ပါ"
        Language.ENGLISH -> "Please select a delivery township"
    }

    fun notificationsEnabled(lang: Language): String = when (lang) {
        Language.BURMESE -> "အသိပေးချက်များ ဖွင့်ထားပါသည်"
        Language.ENGLISH -> "Push Notifications Active"
    }

    fun kbzPayTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "KBZPay ဖြင့် ငွေလွှဲရန်"
        Language.ENGLISH -> "Pay with KBZPay"
    }

    fun scanKbzPayQr(lang: Language): String = when (lang) {
        Language.BURMESE -> "KBZPay အက်ပ်ဖြင့် QR စကင်ဖတ်၍ ငွေချေပါ"
        Language.ENGLISH -> "Scan QR with KBZPay App to Pay"
    }

    fun kbzPayNumberLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "KBZPay ဖုန်းနံပါတ်"
        Language.ENGLISH -> "KBZPay Number"
    }

    fun kbzAccNameLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်အမည်"
        Language.ENGLISH -> "Account Name"
    }

    fun kbzAccountName(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဒေါ်ပွင့်စံ (မြန်မာ့ရိုးရာ မီးဖိုဆောင်)"
        Language.ENGLISH -> "Daw Pwint San (Myanmar Traditional Kitchen)"
    }

    fun kbzAccountNumber(): String = "09-789 456 123"

    fun kbzTransferNoteLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေလွှဲမှတ်ချက် (Note)"
        Language.ENGLISH -> "Transfer Note / Reference"
    }

    fun paymentTimerRemaining(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေလွှဲရန် ကျန်ရှိချိန်"
        Language.ENGLISH -> "Payment Countdown"
    }

    fun tenMinuteWarning(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါမပျက်ပြယ်စေရန် ၁၀ မိနစ်အတွင်း ငွေလွှဲပေးပါရန်။ သတ်မှတ်ချိန်ပြည့်ပါက အသိပေးပါမည်။"
        Language.ENGLISH -> "Please complete payment within 10 minutes. A reminder will be sent if unpaid."
    }

    fun iHavePaid(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေလွှဲပြီးပါပြီ (Confirm Payment)"
        Language.ENGLISH -> "I Have Paid (Confirm)"
    }

    fun copiedToClipboard(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကူးယူပြီးပါပြီ!"
        Language.ENGLISH -> "Copied to clipboard!"
    }

    fun viewKbzQr(lang: Language): String = when (lang) {
        Language.BURMESE -> "KBZPay QR နှင့် အကောင့်ကြည့်ရန်"
        Language.ENGLISH -> "View KBZPay QR & Account"
    }
}
