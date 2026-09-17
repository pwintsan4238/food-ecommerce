package com.example.model

enum class OrderStatus(val stepIndex: Int) {
    PLACED(1),
    PREPARING(2),
    OUT_FOR_DELIVERY(3),
    DELIVERED(4),
    CANCELLED(0);

    fun title(lang: Language): String = when (this) {
        PLACED -> if (lang == Language.BURMESE) "အော်ဒါ လက်ခံရရှိပါပြီ" else "Order Received"
        PREPARING -> if (lang == Language.BURMESE) "အော်ဒါ ပြင်ဆင်နေပါသည်" else "Order Preparing"
        OUT_FOR_DELIVERY -> if (lang == Language.BURMESE) "ပို့ဆောင်သူ လာနေပါပြီ" else "Rider on the Way"
        DELIVERED -> if (lang == Language.BURMESE) "ပို့ဆောင်ပြီးစီးပါပြီ" else "Delivered Successfully"
        CANCELLED -> if (lang == Language.BURMESE) "အော်ဒါ ပယ်ဖျက်လိုက်ပါပြီ" else "Order Cancelled"
    }

    fun description(lang: Language): String = when (this) {
        PLACED -> if (lang == Language.BURMESE) "ဆိုင်မှ အော်ဒါကို အတည်ပြုပြီး မကြာမီ စတင်ချက်ပြုတ်ပါမည်" else "The restaurant has confirmed your order and will start cooking shortly."
        PREPARING -> if (lang == Language.BURMESE) "စားဖိုမှူးမှ လတ်ဆတ်သော အစားအစာများကို ပူပူနွေးနွေး ချက်ပြုတ်နေပါသည်" else "Chef is freshly preparing your dishes with care."
        OUT_FOR_DELIVERY -> if (lang == Language.BURMESE) "ပို့ဆောင်ရေး ဝန်ထမ်းမှ သင့်မြို့နယ်သို့ အမြန်ပို့ဆောင်ပေးနေပါသည်" else "Rider has picked up your food and is speeding to your address."
        DELIVERED -> if (lang == Language.BURMESE) "အစားအစာ ရောက်ရှိပါပြီ။ အရသာရှိစွာ သုံးဆောင်ပါခင်ဗျာ/ရှင့်" else "Your food has arrived! Enjoy your delicious Myanmar meal."
        CANCELLED -> if (lang == Language.BURMESE) "ဤအော်ဒါကို ပယ်ဖျက်ပြီးပါပြီ" else "This order was cancelled."
    }

    fun nextStatus(): OrderStatus? = when (this) {
        PLACED -> PREPARING
        PREPARING -> OUT_FOR_DELIVERY
        OUT_FOR_DELIVERY -> DELIVERED
        DELIVERED -> null
        CANCELLED -> null
    }
}

enum class PaymentMethod(val id: String) {
    COD("cod"),
    KPAY("kpay"),
    WAVEPAY("wavepay");

    fun displayName(lang: Language): String = when (this) {
        COD -> Strings.cod(lang)
        KPAY -> Strings.kpay(lang)
        WAVEPAY -> Strings.wavepay(lang)
    }
}
