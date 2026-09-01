package com.example.model

data class MyanmarTownship(
    val id: String,
    val nameEn: String,
    val nameMy: String,
    val regionEn: String,
    val regionMy: String,
    val deliveryFeeMMK: Int,
    val estimatedMinutes: Int
)

object MyanmarTownshipsData {
    val allTownships = listOf(
        // Yangon Townships
        MyanmarTownship("ygn_kamayut", "Kamayut", "ကမာရွတ်", "Yangon", "ရန်ကုန်", 1500, 25),
        MyanmarTownship("ygn_sanchaung", "Sanchaung", "စမ်းချောင်း", "Yangon", "ရန်ကုန်", 1500, 25),
        MyanmarTownship("ygn_bahan", "Bahan", "ဗဟန်း", "Yangon", "ရန်ကုန်", 1500, 20),
        MyanmarTownship("ygn_hlaing", "Hlaing", "လှိုင်", "Yangon", "ရန်ကုန်", 1800, 30),
        MyanmarTownship("ygn_yankin", "Yankin", "ရန်ကင်း", "Yangon", "ရန်ကုန်", 1800, 30),
        MyanmarTownship("ygn_mayangone", "Mayangone", "မရမ်းကုန်း", "Yangon", "ရန်ကုန်", 2000, 35),
        MyanmarTownship("ygn_kyauktada", "Kyauktada (Downtown)", "ကျောက်တံတား (မြို့လယ်)", "Yangon", "ရန်ကုန်", 2000, 35),
        MyanmarTownship("ygn_pabedan", "Pabedan (Downtown)", "ပန်းဘဲတန်း (မြို့လယ်)", "Yangon", "ရန်ကုန်", 2000, 35),
        MyanmarTownship("ygn_latha", "Latha (Chinatown)", "လသာ (တရုတ်တန်း)", "Yangon", "ရန်ကုန်", 2000, 35),
        MyanmarTownship("ygn_lanmadaw", "Lanmadaw", "လမ်းမတော်", "Yangon", "ရန်ကုန်", 2000, 35),
        MyanmarTownship("ygn_botataung", "Botataung", "ဗိုလ်တထောင်", "Yangon", "ရန်ကုန်", 2200, 40),
        MyanmarTownship("ygn_tamwe", "Tamwe", "တာမွေ", "Yangon", "ရန်ကုန်", 1800, 30),
        MyanmarTownship("ygn_thingangyun", "Thingangyun", "သင်္ဃန်းကျွန်း", "Yangon", "ရန်ကုန်", 2200, 35),
        MyanmarTownship("ygn_south_okkalapa", "South Okkalapa", "တောင်ဥက္ကလာပ", "Yangon", "ရန်ကုန်", 2200, 40),
        MyanmarTownship("ygn_north_okkalapa", "North Okkalapa", "မြောက်ဥက္ကလာပ", "Yangon", "ရန်ကုန်", 2500, 45),
        MyanmarTownship("ygn_insein", "Insein", "အင်းစိန်", "Yangon", "ရန်ကုန်", 2500, 45),
        MyanmarTownship("ygn_dagon", "Dagon", "ဒဂုံ", "Yangon", "ရန်ကုန်", 1500, 25),
        MyanmarTownship("ygn_north_dagon", "North Dagon", "မြောက်ဒဂုံ", "Yangon", "ရန်ကုန်", 2800, 50),
        MyanmarTownship("ygn_south_dagon", "South Dagon", "တောင်ဒဂုံ", "Yangon", "ရန်ကုန်", 2800, 50),
        MyanmarTownship("ygn_east_dagon", "East Dagon", "အရှေ့ဒဂုံ", "Yangon", "ရန်ကုန်", 3000, 55),
        MyanmarTownship("ygn_ahlone", "Ahlone", "အလုံ", "Yangon", "ရန်ကုန်", 1800, 30),
        MyanmarTownship("ygn_thaketa", "Thaketa", "သာကေတ", "Yangon", "ရန်ကုန်", 2400, 40),
        MyanmarTownship("ygn_dawbon", "Dawbon", "ဒေါပုံ", "Yangon", "ရန်ကုန်", 2400, 40),
        MyanmarTownship("ygn_mingalar_taung_nyunt", "Mingalar Taung Nyunt", "မင်္ဂလာတောင်ညွန့်", "Yangon", "ရန်ကုန်", 2000, 35),

        // Mandalay Townships
        MyanmarTownship("mdy_chanayethazan", "Chanayethazan", "ချမ်းအေးသာစံ", "Mandalay", "မန္တလေး", 1800, 25),
        MyanmarTownship("mdy_aungmyethazan", "Aungmyethazan", "အောင်မြေသာစံ", "Mandalay", "မန္တလေး", 1800, 25),
        MyanmarTownship("mdy_chanmyathazi", "Chanmyathazi", "ချမ်းမြသာစည်", "Mandalay", "မန္တလေး", 2000, 30),
        MyanmarTownship("mdy_mahaaungmye", "Mahaaungmye", "မဟာအောင်မြေ", "Mandalay", "မန္တလေး", 1800, 25),
        MyanmarTownship("mdy_pyigyidagon", "Pyigyidagon", "ပြည်ကြီးတံခွန်", "Mandalay", "မန္တလေး", 2500, 40),
        MyanmarTownship("mdy_amarapura", "Amarapura", "အမရပူရ", "Mandalay", "မန္တလေး", 3000, 45),

        // Naypyidaw Townships
        MyanmarTownship("npt_zabuthiri", "Zabuthiri", "ဇမ္ဗူသီရိ", "Naypyidaw", "နေပြည်တော်", 2000, 30),
        MyanmarTownship("npt_ottarathiri", "Ottarathiri", "ဥတ္တရသီရိ", "Naypyidaw", "နေပြည်တော်", 2200, 35),
        MyanmarTownship("npt_dekkhinathiri", "Dekkhinathiri", "ဒက္ခိဏသီရိ", "Naypyidaw", "နေပြည်တော်", 2200, 35),
        MyanmarTownship("npt_pobpathiri", "Pobpathiri", "ပုဗ္ဗသီရိ", "Naypyidaw", "နေပြည်တော်", 2200, 35),

        // Other Major Cities
        MyanmarTownship("tgi_taunggyi", "Taunggyi Central", "တောင်ကြီးမြို့တွင်း", "Shan State", "ရှမ်းပြည်နယ်", 2000, 30),
        MyanmarTownship("pol_pyinoolwin", "Pyin Oo Lwin Central", "ပြင်ဦးလွင်မြို့တွင်း", "Mandalay Region", "မန္တလေးတိုင်း", 2000, 30),
        MyanmarTownship("mlm_mawlamyine", "Mawlamyine Central", "မော်လမြိုင်မြို့တွင်း", "Mon State", "မွန်ပြည်နယ်", 2000, 30),
        MyanmarTownship("bgo_bago", "Bago Central", "ပဲခူးမြို့တွင်း", "Bago Region", "ပဲခူးတိုင်း", 2000, 30)
    )

    val defaultTownship = allTownships[0] // Kamayut
}
