package com.example.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    BURMESE("my", "Burmese", "မြန်မာ"),
    ENGLISH("en", "English", "English")
}

object Strings {
    fun appTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "တိမ်တမန်"
        Language.ENGLISH -> "Taim Ta Man"
    }

    fun appSubtitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "ရခိုင်အစားအစာနှင့်ပင်လယ်စာ အိမ်အရောက်ပို့ဆောင်ရေး"
        Language.ENGLISH -> "Rakhine Food & Seafood Delivery Service"
    }

    fun searchPlaceholder(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပင်လယ်စာများ (ငါး၊ ပုစွန်၊ ကဏန်း၊ ရေဘဝဲ) ရှာဖွေပါ..."
        Language.ENGLISH -> "Search seafood (fish, prawn, crab, lobster, squid)..."
    }

    fun browseBySeafoodType(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပင်လယ်စာအမျိုးအစားအလိုက် ဝယ်ယူရန်"
        Language.ENGLISH -> "Shop by Seafood Type"
    }

    fun browseByPrepStyle(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပြင်ဆင်ပြီးပုံစံအလိုက် ဝယ်ယူရန်"
        Language.ENGLISH -> "Shop by Preparation Style"
    }

    fun browseByOccasion(lang: Language): String = when (lang) {
        Language.BURMESE -> "စားသုံးမည့်အချိန်အခါအလိုက်"
        Language.ENGLISH -> "Shop by Occasion"
    }

    fun browseByPrice(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဈေးနှုန်းအလိုက် ဝယ်ယူရန်"
        Language.ENGLISH -> "Shop by Price Range"
    }

    fun popularDishes(lang: Language): String = when (lang) {
        Language.BURMESE -> "လတ်ဆတ်သော ပင်လယ်စာများ"
        Language.ENGLISH -> "Fresh Coastal Seafood"
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
        Language.ENGLISH -> "Order preparing. You'll receive real-time push notifications."
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
        Language.ENGLISH -> "Home"
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
        Language.BURMESE -> "ဒေါ်ပွင့်စံ (တိမ်တမန် ရခိုင်အစားအစာ)"
        Language.ENGLISH -> "Daw Pwint San (Taim Ta Man Rakhine Food)"
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

    fun menuDrawer(lang: Language): String = when (lang) {
        Language.BURMESE -> "မီနူးဖွင့်ရန်"
        Language.ENGLISH -> "Navigation Menu"
    }

    fun accountTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့် / ပရိုဖိုင်"
        Language.ENGLISH -> "Account & Profile"
    }

    fun settingsTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဆက်တင်များ"
        Language.ENGLISH -> "Settings"
    }

    fun logout(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်မှထွက်မည်"
        Language.ENGLISH -> "Log Out"
    }

    fun switchAccount(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်ပြောင်းမည်"
        Language.ENGLISH -> "Switch Account"
    }

    fun switchAccountDesc(lang: Language): String = when (lang) {
        Language.BURMESE -> "အခြားအမည် သို့မဟုတ် ဖုန်းနံပါတ်ဖြင့် မှာယူရန်"
        Language.ENGLISH -> "Order under another name or phone number"
    }

    fun logoutConfirmTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်မှထွက်ရန် သေချာပါသလား?"
        Language.ENGLISH -> "Log out of current profile?"
    }

    fun logoutConfirmDesc(lang: Language): String = when (lang) {
        Language.BURMESE -> "လက်ရှိဖြည့်သွင်းထားသော အမည်နှင့် ဖုန်းနံပါတ် အချက်အလက်များကို ရှင်းလင်းပေးပါမည်။"
        Language.ENGLISH -> "This will clear your locally saved name and delivery contact info."
    }

    fun cancel(lang: Language): String = when (lang) {
        Language.BURMESE -> "မလုပ်တော့ပါ"
        Language.ENGLISH -> "Cancel"
    }

    fun confirm(lang: Language): String = when (lang) {
        Language.BURMESE -> "သေချာပါသည်"
        Language.ENGLISH -> "Confirm"
    }

    fun customerSupport(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဖောက်သည် ဝန်ဆောင်မှု"
        Language.ENGLISH -> "Customer Support"
    }

    fun hotlinePhone(): String = "09-789 456 123"

    fun deliveryAreas(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်ပေးသော မြို့နယ်များ"
        Language.ENGLISH -> "Delivery Areas & Fees"
    }

    fun appVersionLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဗားရှင်း"
        Language.ENGLISH -> "Version"
    }

    fun loginOrSignUp(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်ဝင်ရန် / အကောင့်ဖွင့်ရန်"
        Language.ENGLISH -> "Log In or Sign Up"
    }

    fun loginTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်ဝင်မည်"
        Language.ENGLISH -> "Log In"
    }

    fun signUpTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်သစ်ဖွင့်မည်"
        Language.ENGLISH -> "Create Account"
    }

    fun continueAsGuest(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဧည့်သည်အဖြစ် ဆက်လက်ကြည့်ရှုမည်"
        Language.ENGLISH -> "Continue as Guest"
    }

    fun guestUser(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဧည့်သည်တော်"
        Language.ENGLISH -> "Guest User"
    }

    fun guestUserDesc(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်မဝင်ဘဲ ပင်လယ်စာဟင်းပွဲများကို လွတ်လပ်စွာ ကြည့်ရှုမှာယူနိုင်ပါသည်"
        Language.ENGLISH -> "Browse seafood dishes and menus freely without signing in"
    }

    fun authPromptSubtitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "မှာယူမှုမှတ်တမ်းများနှင့် အမြန်မှာယူနိုင်ရန် အကောင့်ဝင်ပါ သို့မဟုတ် ဧည့်သည်အဖြစ် ဆက်လက်ဝင်ရောက်ပါ"
        Language.ENGLISH -> "Sign in to save order history, or continue as guest to browse the menu freely"
    }

    fun enterPhone(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဖုန်းနံပါတ် ဖြည့်သွင်းပါ"
        Language.ENGLISH -> "Enter Phone Number"
    }

    fun enterName(lang: Language): String = when (lang) {
        Language.BURMESE -> "အမည် အပြည့်အစုံ"
        Language.ENGLISH -> "Full Name"
    }

    fun optionalPassword(lang: Language): String = when (lang) {
        Language.BURMESE -> "လျှို့ဝှက်နံပါတ် (စိတ်ကြိုက်)"
        Language.ENGLISH -> "Password (Optional)"
    }

    fun welcomeBackUser(name: String, lang: Language): String = when (lang) {
        Language.BURMESE -> "ကြိုဆိုပါသည် $name"
        Language.ENGLISH -> "Welcome back, $name!"
    }

    fun guestModeActive(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဧည့်သည်အဖြစ် ကြည့်ရှုနေပါသည်"
        Language.ENGLISH -> "Browsing as Guest"
    }

    // Admin Strings
    fun adminTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "စီမံခန့်ခွဲသူ"
        Language.ENGLISH -> "Admin"
    }

    fun adminPortal(lang: Language): String = when (lang) {
        Language.BURMESE -> "စီမံခန့်ခွဲသူ ဒက်ရှ်ဘုတ်"
        Language.ENGLISH -> "Admin Console"
    }

    fun adminLoginTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "စီမံခန့်ခွဲသူ ဝင်ရောက်ရန်"
        Language.ENGLISH -> "Admin Sign In"
    }

    fun adminBadge(lang: Language): String = when (lang) {
        Language.BURMESE -> "စီမံခန့်ခွဲသူ (Admin)"
        Language.ENGLISH -> "Administrator"
    }

    fun customerBadge(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဝယ်ယူသူ (Customer)"
        Language.ENGLISH -> "Customer"
    }

    fun adminProductsTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကုန်ပစ္စည်းနှင့် ဈေးနှုန်း"
        Language.ENGLISH -> "Products & Pricing"
    }

    fun adminUsersTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "အသုံးပြုသူ စာရင်း"
        Language.ENGLISH -> "User Accounts"
    }

    fun adminOrdersTab(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါများ စစ်ဆေးခြင်း"
        Language.ENGLISH -> "Check Orders"
    }

    fun addProduct(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကုန်ပစ္စည်း အသစ်ထည့်ရန်"
        Language.ENGLISH -> "Add New Product"
    }

    fun editProduct(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကုန်ပစ္စည်း ပြင်ဆင်ရန်"
        Language.ENGLISH -> "Edit Product"
    }

    fun deleteProduct(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကုန်ပစ္စည်း ဖျက်မည်"
        Language.ENGLISH -> "Delete Product"
    }

    fun productNameEn(lang: Language): String = when (lang) {
        Language.BURMESE -> "အင်္ဂလိပ် အမည်"
        Language.ENGLISH -> "Product Name (English)"
    }

    fun productNameMy(lang: Language): String = when (lang) {
        Language.BURMESE -> "မြန်မာ အမည်"
        Language.ENGLISH -> "Product Name (Myanmar)"
    }

    fun priceMMKLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဈေးနှုန်း (ကျပ်)"
        Language.ENGLISH -> "Price (MMK)"
    }

    fun unitLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ယူနစ် (ဥပမာ- ၁ ကီလို / ၅၀၀ ဂရမ်)"
        Language.ENGLISH -> "Unit (e.g. per 1 kg)"
    }

    fun categoryLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အမျိုးအစား"
        Language.ENGLISH -> "Category"
    }

    fun prepStyleLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပြင်ဆင်မှု ပုံစံ"
        Language.ENGLISH -> "Prep Style"
    }

    fun occasionLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "စားသုံးမည့် အခါ"
        Language.ENGLISH -> "Dining Occasion"
    }

    fun descriptionEnLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အင်္ဂလိပ် အသေးစိတ် ဖော်ပြချက်"
        Language.ENGLISH -> "Description (English)"
    }

    fun descriptionMyLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "မြန်မာ အသေးစိတ် ဖော်ပြချက်"
        Language.ENGLISH -> "Description (Myanmar)"
    }

    fun prepTimeMinLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပြင်ဆင်ချိန် (မိနစ်)"
        Language.ENGLISH -> "Prep Time (minutes)"
    }

    fun imageUrlLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကုန်ပစ္စည်း ဓာတ်ပုံ (Product Photo)"
        Language.ENGLISH -> "Product Photo"
    }

    fun uploadProductPhoto(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဖုန်းပြခန်းမှ ဓာတ်ပုံ တင်မည် (Upload Photo)"
        Language.ENGLISH -> "Upload Photo from Gallery"
    }

    fun choosePhotoFromGallery(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဖုန်းပြခန်းမှ ဓာတ်ပုံ ရွေးချယ်ပါ"
        Language.ENGLISH -> "Choose Photo from Gallery"
    }

    fun changePhoto(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဓာတ်ပုံ ပြောင်းမည်"
        Language.ENGLISH -> "Change Photo"
    }

    fun removePhoto(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဓာတ်ပုံ ပယ်ဖျက်မည်"
        Language.ENGLISH -> "Remove Photo"
    }

    fun photoUploaded(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဓာတ်ပုံ တင်ထားပြီးပါပြီ"
        Language.ENGLISH -> "Photo Uploaded"
    }

    fun uploadPaymentSlip(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေလွှဲပြေစာ ဓာတ်ပုံ တင်ရန် (Upload Slip)"
        Language.ENGLISH -> "Upload Payment Slip Screenshot"
    }

    fun chooseSlipFromGallery(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေလွှဲပြေစာ စခရင်ရှော့ ရွေးချယ်ပါ"
        Language.ENGLISH -> "Choose Slip Screenshot from Gallery"
    }

    fun paymentSlipAttached(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေလွှဲပြေစာ ပူးတွဲပြီးပါပြီ ✓"
        Language.ENGLISH -> "Payment Slip Attached ✓"
    }

    fun isPopularLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "လူကြိုက်များသော ပစ္စည်း"
        Language.ENGLISH -> "Popular Item"
    }

    fun isSpicyLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အစပ်အရသာ ပါဝင်သည်"
        Language.ENGLISH -> "Spicy"
    }

    fun isPremiumLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အဆင့်မြင့် အထူးပစ္စည်း"
        Language.ENGLISH -> "Premium Item"
    }

    fun saveProduct(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကုန်ပစ္စည်း သိမ်းဆည်းမည်"
        Language.ENGLISH -> "Save Product"
    }

    fun createUserAccount(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်သစ် ဖွင့်ပေးရန်"
        Language.ENGLISH -> "Create User Account"
    }

    fun roleLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့်အဆင့် / အခန်းကဏ္ဍ"
        Language.ENGLISH -> "Account Role"
    }

    fun loginAsThisUser(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဤအကောင့်ဖြင့် ဝင်မည်"
        Language.ENGLISH -> "Log In as User"
    }

    fun deleteAccount(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကောင့် ဖျက်မည်"
        Language.ENGLISH -> "Delete Account"
    }

    fun updateOrderStatusTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "အော်ဒါ အခြေအနေ ပြောင်းလဲရန်"
        Language.ENGLISH -> "Update Order Status"
    }

    fun quickDemoAdminSignIn(lang: Language): String = when (lang) {
        Language.BURMESE -> "Admin စမ်းသပ်ချက် အမြန်ဝင်ရန်"
        Language.ENGLISH -> "One-Tap Demo Admin Sign-In"
    }

    fun demoAdminDesc(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကုန်ပစ္စည်းများ၊ ဈေးနှုန်းများနှင့် အော်ဒါများကို တိုက်ရိုက် စီမံခန့်ခွဲနိုင်ပါသည်"
        Language.ENGLISH -> "Full access to edit products, prices, user accounts and customer orders"
    }

    fun customerView(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဝယ်ယူသူ စာမျက်နှာသို့ သွားမည်"
        Language.ENGLISH -> "Customer Storefront"
    }

    fun totalOrdersLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "စုစုပေါင်း အော်ဒါများ"
        Language.ENGLISH -> "Total Orders"
    }

    fun totalRevenueLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "စုစုပေါင်း ရောင်းရငွေ"
        Language.ENGLISH -> "Total Revenue"
    }

    fun uploadQrImage(lang: Language): String = when (lang) {
        Language.BURMESE -> "QR ကုဒ် ဓာတ်ပုံ တင်ရန်"
        Language.ENGLISH -> "Upload QR Code Image"
    }

    fun chooseQrFromGallery(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဖုန်းထဲမှ QR ပုံ ရွေးချယ်တင်မည်"
        Language.ENGLISH -> "Upload QR from Gallery"
    }

    fun qrImageAttached(lang: Language): String = when (lang) {
        Language.BURMESE -> "QR ဓာတ်ပုံ ထည့်သွင်းပြီးပါပြီ"
        Language.ENGLISH -> "QR Image Attached"
    }

    fun changeQrImage(lang: Language): String = when (lang) {
        Language.BURMESE -> "QR ပုံ အသစ်လဲမည်"
        Language.ENGLISH -> "Change QR Image"
    }

    fun removeQrImage(lang: Language): String = when (lang) {
        Language.BURMESE -> "QR ပုံ ဖျက်မည်"
        Language.ENGLISH -> "Remove QR Image"
    }

    fun paymentBudgetThresholds(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေပေးချေမှု ကန့်သတ်ချက် သတ်မှတ်ချက်များ"
        Language.ENGLISH -> "Payment Budget Thresholds"
    }

    fun codThresholdTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "COD ပစ္စည်းရောက်ငွေချေ ကန့်သတ်ချက်"
        Language.ENGLISH -> "Cash on Delivery (COD) Budget Threshold"
    }

    fun codThresholdSubtitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဝယ်ယူသည့် ငွေပမာဏ ကန့်သတ်ချက်များ သတ်မှတ်နိုင်ပါသည်"
        Language.ENGLISH -> "Configure minimum and maximum order amounts to qualify for COD"
    }

    fun codThresholdDesc(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဝယ်ယူသူများသည် သတ်မှတ်ထားသော ငွေပမာဏအတွင်း ဝယ်ယူမှသာ ပစ္စည်းရောက်ငွေချေ (COD) ကို ရွေးချယ်နိုင်မည်ဖြစ်ပါသည်။ ယာဉ်မောင်းလုံခြုံရေးနှင့် လုပ်ငန်းအဆင်ပြေစေရန် ကန့်သတ်ချက်များ သတ်မှတ်နိုင်ပါသည်။"
        Language.ENGLISH -> "Set budget thresholds for Cash on Delivery. Customers can only select COD if their order total meets these rules (e.g. over 100,000 MMK)."
    }

    fun minOrderRequired(lang: Language): String = when (lang) {
        Language.BURMESE -> "အနည်းဆုံး မှာယူရမည့် ငွေပမာဏ (Min Budget)"
        Language.ENGLISH -> "Minimum Order Amount (Min Budget)"
    }

    fun maxOrderAllowed(lang: Language): String = when (lang) {
        Language.BURMESE -> "အများဆုံး ကန့်သတ်ငွေပမာဏ (Max Budget Cap)"
        Language.ENGLISH -> "Maximum Order Cap (Max Budget)"
    }

    fun noLimit(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကန့်သတ်မထားပါ"
        Language.ENGLISH -> "No Limit"
    }

    fun editThreshold(lang: Language): String = when (lang) {
        Language.BURMESE -> "ကန့်သတ်ချက် ပြင်ဆင်မည်"
        Language.ENGLISH -> "Edit Threshold"
    }

    fun codMinThresholdHelp(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဤပမာဏနှင့်အထက် ဝယ်ယူမှသာ ပစ္စည်းရောက်ငွေချေ ရွေးချယ်နိုင်ပါမည်"
        Language.ENGLISH -> "Only orders meeting or exceeding this amount can choose COD"
    }

    fun codLockedNote(lang: Language, minMmk: String, diffMmk: String): String = when (lang) {
        Language.BURMESE -> "ပစ္စည်းရောက်ငွေချေအတွက် အနည်းဆုံး $minMmk ကျပ် လိုအပ်ပါသည် (ထပ်မံလိုအပ်ငွေ: $diffMmk ကျပ်)"
        Language.ENGLISH -> "Cash on Delivery requires order ≥ $minMmk MMK (Add $diffMmk MMK more to unlock)"
    }

    fun codUnlockedNote(lang: Language, minMmk: String): String = when (lang) {
        Language.BURMESE -> "✓ $minMmk ကျပ်ပြည့်ပြီးဖြစ်၍ ပစ္စည်းရောက်ငွေချေ (COD) အသုံးပြုနိုင်ပါပြီ"
        Language.ENGLISH -> "✓ Order qualifies! Cash on Delivery is unlocked (≥ $minMmk MMK)"
    }

    // Logistics & Delivery Settings Strings
    fun logisticsTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်ရေး စီမံခန့်ခွဲမှု"
        Language.ENGLISH -> "Logistics & Delivery Settings"
    }

    fun logisticsSubtitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "မြို့နယ်အလိုက် ပို့ဆောင်ခ၊ အခမဲ့သတ်မှတ်ချက်နှင့် ယာဉ်မောင်းများ စီမံခြင်း"
        Language.ENGLISH -> "Manage delivery zones, free shipping rules, rush fees & couriers"
    }

    fun deliveryZones(lang: Language): String = when (lang) {
        Language.BURMESE -> "မြို့နယ်နှင့် ပို့ဆောင်ခ ဇုန်များ"
        Language.ENGLISH -> "Delivery Zones & Fees"
    }

    fun deliveryPolicies(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်ရေး စည်းမျဉ်းနှင့် မူဝါဒများ"
        Language.ENGLISH -> "Delivery Policies & Thresholds"
    }

    fun deliveryCouriers(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်ရေး မိတ်ဖက်များနှင့် ယာဉ်မောင်းများ"
        Language.ENGLISH -> "Couriers & Delivery Fleets"
    }

    fun freeDeliveryRule(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်ခ အခမဲ့ စည်းမျဉ်း"
        Language.ENGLISH -> "Free Delivery Policy"
    }

    fun expressRushDelivery(lang: Language): String = when (lang) {
        Language.BURMESE -> "အမြန်ပို့ဆောင်ရေး (Express Rush)"
        Language.ENGLISH -> "Express Rush Delivery"
    }

    fun storeSelfPickup(lang: Language): String = when (lang) {
        Language.BURMESE -> "ဆိုင်မှ ကိုယ်တိုင်လာယူခြင်း (Takeaway)"
        Language.ENGLISH -> "Store Self-Pickup (Takeaway)"
    }

    fun coldChainInsulation(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပင်လယ်စာ အအေးထိန်း ရေခဲသေတ္တာ ထုပ်ပိုးမှု"
        Language.ENGLISH -> "Cold-Chain Thermal Packaging"
    }

    fun addDeliveryZone(lang: Language): String = when (lang) {
        Language.BURMESE -> "မြို့နယ်/ဇုန် အသစ်ထည့်မည်"
        Language.ENGLISH -> "Add Delivery Zone"
    }

    fun editDeliveryZone(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပို့ဆောင်ခနှင့် ကြာချိန် ပြင်ဆင်ရန်"
        Language.ENGLISH -> "Edit Zone Delivery Fee"
    }

    fun resetTownshipFees(lang: Language): String = when (lang) {
        Language.BURMESE -> "မူလဈေးနှုန်းများသို့ ပြန်ထားမည်"
        Language.ENGLISH -> "Reset to Default Fees"
    }

    // Product Fulfillment, Limits, Additional Costs & Discounts
    fun fulfillmentOptionsTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "ပစ္စည်းပို့ဆောင်မှုနှင့် ရယူမှုစနစ် (Fulfillment Options)"
        Language.ENGLISH -> "Fulfillment & Pickup Options"
    }

    fun allowSelfPickupLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "🏪 ဆိုင်သို့ ကိုယ်တိုင်လာယူခွင့် (Self Pick-up)"
        Language.ENGLISH -> "🏪 Allow Self Pick-up"
    }

    fun allowDeliveryLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "🚚 အိမ်အရောက် ပို့ဆောင်ခွင့် (Home Delivery)"
        Language.ENGLISH -> "🚚 Allow Home Delivery"
    }

    fun regionCoverageTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "📍 ပို့ဆောင်ပေးနိုင်သော မြို့နယ်/ဒေသများ (Service Regions)"
        Language.ENGLISH -> "📍 Available Cities & Regions"
    }

    fun allRegionsText(lang: Language): String = when (lang) {
        Language.BURMESE -> "🇲🇲 တစ်နိုင်ငံလုံးသို့ ပို့ဆောင်ပေးပါသည် (All Regions / Nationwide)"
        Language.ENGLISH -> "🇲🇲 Available Nationwide across all regions & cities"
    }

    fun minPurchaseTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "⚖️ အနည်းဆုံး ဝယ်ယူရန် ပမာဏ (Minimum Purchase)"
        Language.ENGLISH -> "⚖️ Minimum Purchase Requirement"
    }

    fun minPurchaseQtyLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အနည်းဆုံး အရေအတွက်/ကီလို"
        Language.ENGLISH -> "Minimum Quantity / kg"
    }

    fun minPurchaseAmountLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အနည်းဆုံး အော်ဒါကျသင့်ငွေ (MMK)"
        Language.ENGLISH -> "Minimum Order Amount (MMK)"
    }

    fun additionalServicesTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "❄️ အပိုဆောင်း ဝန်ဆောင်မှု ရွေးချယ်စရာများ (Additional Services)"
        Language.ENGLISH -> "❄️ Optional Care & Special Prep"
    }

    fun coldStorageService(lang: Language): String = when (lang) {
        Language.BURMESE -> "ရေခဲပုံး & အအေးထိန်း ထုပ်ပိုးမှု (Ice Box Cold Storage)"
        Language.ENGLISH -> "Ice Box & Cold Storage Packaging"
    }

    fun specialPrepService(lang: Language): String = when (lang) {
        Language.BURMESE -> "အကြေးခွံခွာ သန့်စင် လေလုံထုပ်ပိုးမှု (Special Prep & Vacuum Seal)"
        Language.ENGLISH -> "Special Cleaning & Vacuum Sealing"
    }

    fun discountTiersTitle(lang: Language): String = when (lang) {
        Language.BURMESE -> "🎉 လျှော့စျေး အထူးအခွင့်အရေးများ (Discounts & Offers)"
        Language.ENGLISH -> "🎉 Bulk & Value Discounts"
    }

    fun discountByQtyLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "အရေအတွက်အလိုက် လျှော့စျေး (%)"
        Language.ENGLISH -> "Discount by Quantity (%)"
    }

    fun discountByAmountLabel(lang: Language): String = when (lang) {
        Language.BURMESE -> "ငွေပမာဏအလိုက် လျှော့စျေး (%)"
        Language.ENGLISH -> "Discount by Total Kyats (%)"
    }

    fun discountApplied(lang: Language): String = when (lang) {
        Language.BURMESE -> "အထူးလျှော့စျေး ရရှိထားပါသည်"
        Language.ENGLISH -> "Discount Savings Applied"
    }
}
