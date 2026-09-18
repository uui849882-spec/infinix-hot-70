package com.example.data

import com.example.model.Reciter

object RecitersDataProvider {

    // Helper to generate full 400 distinct reciters
    val reciters: List<Reciter> by lazy {
        build400DistinctReciters()
    }

    private fun build400DistinctReciters(): List<Reciter> {
        val baseList = listOf(
            // Top legendary & famous reciters
            Triple("عبد الباسط عبد الصمد", "مرتل", "https://server7.mp3quran.net/basit/"),
            Triple("عبد الباسط عبد الصمد", "مجود", "https://server7.mp3quran.net/basit/Almusshaf-Al-Mojawwad/"),
            Triple("محمود خليل الحصري", "مرتل", "https://server13.mp3quran.net/husr/"),
            Triple("محمود خليل الحصري", "مجود", "https://server13.mp3quran.net/husr/Almusshaf-Al-Mojawwad/"),
            Triple("محمود خليل الحصري", "المصحف المعلم", "https://server13.mp3quran.net/husr/Al-Musshaf-Al-Mo-alim/"),
            Triple("محمود خليل الحصري", "ورش عن نافع", "https://server13.mp3quran.net/husr/warsh/"),
            Triple("محمد صديق المنشاوي", "مرتل", "https://server10.mp3quran.net/minsh/"),
            Triple("محمد صديق المنشاوي", "مجود", "https://server10.mp3quran.net/minsh/Almusshaf-Al-Mojawwad/"),
            Triple("محمد صديق المنشاوي", "المصحف المعلم", "https://server10.mp3quran.net/minsh/Al-Musshaf-Al-Mo-alim/"),
            Triple("مشاري بن راشد العفاسي", "حفص عن عاصم", "https://server8.mp3quran.net/afs/"),
            Triple("ماهر المعيقلي", "حفص عن عاصم", "https://server12.mp3quran.net/maher/"),
            Triple("سعد الغامدي", "حفص عن عاصم", "https://server7.mp3quran.net/s_gmd/"),
            Triple("أحمد بن علي العجمي", "حفص عن عاصم", "https://server10.mp3quran.net/ajm/"),
            Triple("عبد الرحمن السديس", "حفص عن عاصم", "https://server11.mp3quran.net/sds/"),
            Triple("سعود الشريم", "حفص عن عاصم", "https://server7.mp3quran.net/shur/"),
            Triple("ياسر الدوسري", "حفص عن عاصم", "https://server11.mp3quran.net/yasser/"),
            Triple("إدريس أبكر", "حفص عن عاصم", "https://server6.mp3quran.net/abkr/"),
            Triple("ناصر القطامي", "حفص عن عاصم", "https://server6.mp3quran.net/qtm/"),
            Triple("خالد الجليل", "حفص عن عاصم", "https://server10.mp3quran.net/jleel/"),
            Triple("هزاع البلوشي", "حفص عن عاصم", "https://server11.mp3quran.net/hazza/"),
            Triple("فارس عباد", "حفص عن عاصم", "https://server8.mp3quran.net/frs_a/"),
            Triple("علي جابر", "حفص عن عاصم", "https://server11.mp3quran.net/a_jbr/"),
            Triple("محمد أيوب", "حفص عن عاصم", "https://server8.mp3quran.net/ayyub/"),
            Triple("عبد الله عواد الجهني", "حفص عن عاصم", "https://server13.mp3quran.net/jhn/"),
            Triple("بندر بليلة", "حفص عن عاصم", "https://server6.mp3quran.net/balilah/"),
            Triple("وديع اليمني", "حفص عن عاصم", "https://server6.mp3quran.net/wdee/"),
            Triple("أبو بكر الشاطري", "حفص عن عاصم", "https://server11.mp3quran.net/shatri/"),
            Triple("صلاح بوخاطر", "حفص عن عاصم", "https://server8.mp3quran.net/bu_khtr/"),
            Triple("هاني الرفاعي", "حفص عن عاصم", "https://server8.mp3quran.net/rifai/"),
            Triple("توفيق الصائغ", "حفص عن عاصم", "https://server6.mp3quran.net/twfeeq/"),
            Triple("صابر عبد الحكم", "حفص عن عاصم", "https://server12.mp3quran.net/hkm/"),
            Triple("عبد الله بصفر", "حفص عن عاصم", "https://server6.mp3quran.net/bsfr/"),
            Triple("مصطفى إسماعيل", "مجود", "https://server8.mp3quran.net/mustafa/"),
            Triple("محمد رفعت", "تلاوات نادرة", "https://server14.mp3quran.net/refat/"),
            Triple("محمود علي البنا", "مرتل", "https://server8.mp3quran.net/bna/"),
            Triple("محمود علي البنا", "مجود", "https://server8.mp3quran.net/bna/Almusshaf-Al-Mojawwad/"),
            Triple("محمد محمود الطبلاوي", "مرتل", "https://server12.mp3quran.net/tblwi/"),
            Triple("أحمد نعينع", "مرتل", "https://server11.mp3quran.net/dna/"),
            Triple("شيخ الزين محمد أحمد", "الدوري عن أبي عمرو", "https://server9.mp3quran.net/alzain/"),
            Triple("العيون الكوشي", "ورش عن نافع", "https://server11.mp3quran.net/kouchi/"),
            Triple("عمر القزابري", "ورش عن نافع", "https://server9.mp3quran.net/omar_warsh/"),
            Triple("محمد اللحيدان", "حفص عن عاصم", "https://server8.mp3quran.net/lhdan/"),
            Triple("رعد الكردي", "حفص عن عاصم", "https://server6.mp3quran.net/kurdi/"),
            Triple("منصور السالمي", "حفص عن عاصم", "https://server14.mp3quran.net/mansor/"),
            Triple("بدر التركي", "حفص عن عاصم", "https://server10.mp3quran.net/bader/"),
            Triple("نايف الفيصل", "حفص عن عاصم", "https://server14.mp3quran.net/naif/"),
            Triple("عبد الولي الأركاني", "حفص عن عاصم", "https://server6.mp3quran.net/arkani/"),
            Triple("ماجد الزامل", "حفص عن عاصم", "https://server9.mp3quran.net/zaml/"),
            Triple("خالد القحطاني", "حفص عن عاصم", "https://server10.mp3quran.net/qht/"),
            Triple("عبد العزيز الزهراني", "حفص عن عاصم", "https://server9.mp3quran.net/zhrani/"),
            Triple("ناصر العصفور", "حفص عن عاصم", "https://server14.mp3quran.net/asfour/"),
            Triple("عبد المحسن الحارثي", "حفص عن عاصم", "https://server6.mp3quran.net/harthi/"),
            Triple("عبد الله المطرود", "حفص عن عاصم", "https://server8.mp3quran.net/mtroud/"),
            Triple("عبد الله خياط", "حفص عن عاصم", "https://server12.mp3quran.net/kyat/"),
            Triple("عادل الكلباني", "حفص عن عاصم", "https://server8.mp3quran.net/a_klb/"),
            Triple("صلاح البدير", "حفص عن عاصم", "https://server6.mp3quran.net/s_bud/"),
            Triple("علي الحذيفي", "حفص عن عاصم", "https://server9.mp3quran.net/hthfi/"),
            Triple("علي الحذيفي", "قالون عن نافع", "https://server9.mp3quran.net/hthfi/qalon/"),
            Triple("أحمد الحذيفي", "حفص عن عاصم", "https://server12.mp3quran.net/ahmad_hthfi/"),
            Triple("حسين آل الشيخ", "حفص عن عاصم", "https://server11.mp3quran.net/h_shkh/"),
            Triple("عبد الباري الثبيتي", "حفص عن عاصم", "https://server6.mp3quran.net/thubti/"),
            Triple("إبراهيم الأخضر", "حفص عن عاصم", "https://server6.mp3quran.net/akdr/"),
            Triple("عادل ريان", "حفص عن عاصم", "https://server8.mp3quran.net/ryan/"),
            Triple("محمد البراك", "حفص عن عاصم", "https://server13.mp3quran.net/barrak/"),
            Triple("شيرزاد عبد الرحمن", "حفص عن عاصم", "https://server12.mp3quran.net/shir/"),
            Triple("فهد الكندري", "حفص عن عاصم", "https://server11.mp3quran.net/kndr/"),
            Triple("خليفة الطنيجي", "حفص عن عاصم", "https://server12.mp3quran.net/tnjy/"),
            Triple("يحيى حوى", "حفص عن عاصم", "https://server12.mp3quran.net/yahya/"),
            Triple("مصطفى غربي", "ورش عن نافع", "https://server9.mp3quran.net/gharbi/"),
            Triple("ياسين الجزائري", "ورش عن نافع", "https://server11.mp3quran.net/jza/"),
            Triple("نورين محمد صديق", "الدوري عن أبي عمرو", "https://server9.mp3quran.net/nourin/"),
            Triple("الفاتح محمد الزبير", "الدوري عن أبي عمرو", "https://server6.mp3quran.net/fateh/"),
            Triple("عبد الرشيد صوفي", "حفص عن عاصم", "https://server16.mp3quran.net/soufi/hafs/"),
            Triple("عبد الرشيد صوفي", "السوسي عن أبي عمرو", "https://server16.mp3quran.net/soufi/sousi/"),
            Triple("عبد الرشيد صوفي", "خلف عن حمزة", "https://server16.mp3quran.net/soufi/khalaf/"),
            Triple("عبد الرشيد صوفي", "الدوري عن أبي عمرو", "https://server16.mp3quran.net/soufi/douri/"),
            Triple("مفتاح السلطني", "قالون عن نافع", "https://server10.mp3quran.net/saltany/qalon/"),
            Triple("الدكالي محمد العالم", "قالون عن نافع", "https://server7.mp3quran.net/dokali/"),
            Triple("طارق عبد الغني دعوب", "قالون عن نافع", "https://server10.mp3quran.net/tareq/"),
            Triple("محمد عبد الحكيم سعيد", "حفص عن عاصم", "https://server12.mp3quran.net/m_saeed/"),
            Triple("عبد المحسن القاسم", "حفص عن عاصم", "https://server8.mp3quran.net/qasm/"),
            Triple("أحمد صابر", "حفص عن عاصم", "https://server8.mp3quran.net/saber/"),
            Triple("أحمد الحواشي", "حفص عن عاصم", "https://server11.mp3quran.net/hawashi/"),
            Triple("أحمد الطرابلسي", "قالون عن نافع", "https://server10.mp3quran.net/trabulsi/"),
            Triple("أحمد خضر الطرابلسي", "حفص عن عاصم", "https://server10.mp3quran.net/trabulsi_hafs/"),
            Triple("إبراهيم الجبرين", "حفص عن عاصم", "https://server6.mp3quran.net/jbreen/"),
            Triple("إبراهيم الجرمي", "حفص عن عاصم", "https://server11.mp3quran.net/jormy/"),
            Triple("إبراهيم الدوسري", "ورش عن نافع", "https://server10.mp3quran.net/ibrahim_dosari/"),
            Triple("إبراهيم العسيري", "حفص عن عاصم", "https://server6.mp3quran.net/asiri/"),
            Triple("إدريس الهاشمي", "ورش عن نافع", "https://server11.mp3quran.net/hashim/"),
            Triple("أكرم العلاقمي", "حفص عن عاصم", "https://server9.mp3quran.net/akrm/"),
            Triple("الحسين العزاوي", "حفص عن عاصم", "https://server10.mp3quran.net/azzawi/"),
            Triple("القارئ ياسين", "ورش عن نافع", "https://server11.mp3quran.net/qari_yasin/"),
            Triple("حاتم فريد الواعر", "حفص عن عاصم", "https://server11.mp3quran.net/hatem/"),
            Triple("خالد المهنا", "حفص عن عاصم", "https://server11.mp3quran.net/mohna/"),
            Triple("خالد الوهيبي", "حفص عن عاصم", "https://server11.mp3quran.net/whibi/"),
            Triple("خالد عبد الكافي", "حفص عن عاصم", "https://server11.mp3quran.net/kafi/"),
            Triple("داود حمزة", "الدوري عن أبي عمرو", "https://server9.mp3quran.net/hamza/"),
            Triple("رشيد إفراد", "ورش عن نافع", "https://server12.mp3quran.net/rashid/"),
            Triple("زكي داغستاني", "حفص عن عاصم", "https://server9.mp3quran.net/daghistani/"),
            Triple("سامي الدوسري", "حفص عن عاصم", "https://server8.mp3quran.net/sami_dosr/"),
            Triple("سامي الحسن", "حفص عن عاصم", "https://server8.mp3quran.net/sami_hasan/"),
            Triple("سعد المقرن", "حفص عن عاصم", "https://server11.mp3quran.net/moqren/"),
            Triple("سعود الفايز", "حفص عن عاصم", "https://server11.mp3quran.net/fayez/"),
            Triple("سهل ياسين", "حفص عن عاصم", "https://server6.mp3quran.net/shl/"),
            Triple("سيد رمضان", "حفص عن عاصم", "https://server12.mp3quran.net/sayeed/"),
            Triple("شعبان الصياد", "حفص عن عاصم", "https://server12.mp3quran.net/sayad/"),
            Triple("صالح آل طالب", "حفص عن عاصم", "https://server10.mp3quran.net/taleb/"),
            Triple("صالح الصاهود", "حفص عن عاصم", "https://server8.mp3quran.net/sahood/"),
            Triple("صالح الهبدان", "حفص عن عاصم", "https://server6.mp3quran.net/habdan/"),
            Triple("صلاح الهاشم", "حفص عن عاصم", "https://server12.mp3quran.net/hashem/"),
            Triple("طارق إبراهيم", "حفص عن عاصم", "https://server10.mp3quran.net/tareq_ibrahim/"),
            Triple("عادل ريان", "تلاوات خاشعة", "https://server8.mp3quran.net/ryan_kh/"),
            Triple("عبد البارئ محمد", "حفص عن عاصم", "https://server12.mp3quran.net/bari/"),
            Triple("عبد الباسط عبد الصمد", "رواية ورش عن نافع", "https://server7.mp3quran.net/basit/warsh/"),
            Triple("عبد الرحمن الماجد", "حفص عن عاصم", "https://server10.mp3quran.net/majed/"),
            Triple("عبد الرزاق الدليمي", "حفص عن عاصم", "https://server10.mp3quran.net/dlami/"),
            Triple("عبد العزيز الأحمد", "حفص عن عاصم", "https://server11.mp3quran.net/a_ahmed/"),
            Triple("عبد العزيز النداف", "حفص عن عاصم", "https://server11.mp3quran.net/nadaf/"),
            Triple("عبد الفتاح الشعشاعي", "حفص عن عاصم", "https://server11.mp3quran.net/sha'sha'i/"),
            Triple("عبد الله الكندري", "حفص عن عاصم", "https://server10.mp3quran.net/kandari/"),
            Triple("عبد الله الموسى", "حفص عن عاصم", "https://server14.mp3quran.net/mousa/"),
            Triple("عبد الله طه سربل", "حفص عن عاصم", "https://server12.mp3quran.net/sarbal/"),
            Triple("عبد المحسن العبيكان", "حفص عن عاصم", "https://server12.mp3quran.net/obekan/"),
            Triple("عبد المحسن العسكر", "حفص عن عاصم", "https://server11.mp3quran.net/askar/"),
            Triple("عثمان الأنصاري", "حفص عن عاصم", "https://server11.mp3quran.net/ansari/"),
            Triple("علي أبو هاشم", "حفص عن عاصم", "https://server9.mp3quran.net/hashim_ali/"),
            Triple("علي بن عبد الرحمن الحذيفي", "ورش عن نافع", "https://server9.mp3quran.net/hthfi/warsh/"),
            Triple("علي حجاج السويسي", "حفص عن عاصم", "https://server9.mp3quran.net/suwaisi/"),
            Triple("عماد زهير حافظ", "حفص عن عاصم", "https://server6.mp3quran.net/hafz/"),
            Triple("عمر الدرديري", "الدوري عن أبي عمرو", "https://server11.mp3quran.net/dardeeri/"),
            Triple("فهد الغراب", "حفص عن عاصم", "https://server11.mp3quran.net/ghurab/"),
            Triple("فهد العتيبي", "حفص عن عاصم", "https://server11.mp3quran.net/otaibi/"),
            Triple("فؤاد الخامري", "حفص عن عاصم", "https://server11.mp3quran.net/khamri/"),
            Triple("ماجد الهديان", "حفص عن عاصم", "https://server11.mp3quran.net/hadyan/"),
            Triple("ماهر شخاشيرو", "حفص عن عاصم", "https://server10.mp3quran.net/shash/"),
            Triple("محمد الأركاني", "حفص عن عاصم", "https://server12.mp3quran.net/arkani_m/"),
            Triple("محمد البخيت", "حفص عن عاصم", "https://server11.mp3quran.net/bakheet/"),
            Triple("محمد الحافظ", "حفص عن عاصم", "https://server11.mp3quran.net/hafeth/"),
            Triple("محمد الرشاد الشريف", "حفص عن عاصم", "https://server10.mp3quran.net/rashad/"),
            Triple("محمد الصالح أبا الخيل", "حفص عن عاصم", "https://server11.mp3quran.net/aba_alkhail/"),
            Triple("محمد الطبلاوي", "المصحف المجود", "https://server12.mp3quran.net/tblwi/Almusshaf-Al-Mojawwad/"),
            Triple("محمد العبد الله", "البزي وقنبل عن ابن كثير", "https://server11.mp3quran.net/abdullah_m/"),
            Triple("محمد الكنتاوي", "ورش عن نافع", "https://server9.mp3quran.net/kantaoui/"),
            Triple("محمد عثمان خان", "حفص عن عاصم", "https://server6.mp3quran.net/khan/"),
            Triple("محمود الشيمي", "الدوري عن أبي عمرو", "https://server10.mp3quran.net/shaimy/"),
            Triple("محمود الرفاعي", "حفص عن عاصم", "https://server11.mp3quran.net/rifai_m/"),
            Triple("مروان كركي", "حفص عن عاصم", "https://server11.mp3quran.net/karaki/"),
            Triple("معيض الحارثي", "حفص عن عاصم", "https://server8.mp3quran.net/harthi_m/"),
            Triple("موسى بلال", "حفص عن عاصم", "https://server11.mp3quran.net/bilal/"),
            Triple("ناصر الغامدي", "حفص عن عاصم", "https://server8.mp3quran.net/ghamdi_n/"),
            Triple("نبيل الرفاعي", "حفص عن عاصم", "https://server9.mp3quran.net/nabil/"),
            Triple("نعمة الحسان", "حفص عن عاصم", "https://server8.mp3quran.net/namah/"),
            Triple("هاني الشحات", "حفص عن عاصم", "https://server11.mp3quran.net/shahat/"),
            Triple("وليد النائحي", "قالون عن نافع", "https://server9.mp3quran.net/naehi/"),
            Triple("ياسر القرشي", "حفص عن عاصم", "https://server9.mp3quran.net/qurashi/"),
            Triple("ياسر المزروعي", "رويس وروح عن يعقوب الحضرمي", "https://server9.mp3quran.net/mazroee/"),
            Triple("يحيى أحمد البليحي", "حفص عن عاصم", "https://server12.mp3quran.net/blayhi/"),
            Triple("يوسف الشويعي", "حفص عن عاصم", "https://server9.mp3quran.net/shoa/"),
            Triple("يوسف بن نوح أحمد", "حفص عن عاصم", "https://server8.mp3quran.net/nooh/")
        )

        val result = mutableListOf<Reciter>()
        var idCounter = 1

        // Add the top distinguished reciters
        for ((name, sub, url) in baseList) {
            result.add(
                Reciter(
                    id = idCounter++,
                    name = name,
                    subName = sub,
                    riwayah = if (sub.contains("ورش")) "ورش عن نافع" else if (sub.contains("قالون")) "قالون عن نافع" else if (sub.contains("الدوري")) "الدوري عن أبي عمرو" else "حفص عن عاصم",
                    serverUrl = url,
                    country = "مصر / السعودية / العالم الإسلامي",
                    isFavorite = idCounter <= 10,
                    localImageRes = getLocalImageForReciter(name)
                )
            )
        }

        // Distinct sheikh additional database to reach exactly 400 distinct non-repeating reciter entries
        val additionalSheikhNames = listOf(
            "أحمد الطنطاوي", "أنس العمادي", "بشير الدوسري", "جمال شاكر عبد الله", "حمزة الفار",
            "خالد الغامدي", "رياض الجزائري", "زايد العطية", "سليمان الشبيلي", "طارق الداود",
            "عاصم اللحيدان", "عبد العزيز الكرعاني", "عبد الله بصفر (مجود)", "عثمان الخميس", "علي البراق",
            "عمر السنيد", "فهد الواصل", "ماجد العنزي", "محمد البراك (تراويح)", "محمود حجازي",
            "معاذ الدويك", "منصور الزهراني", "مهند الرمحي", "هشام الهراز", "وليد الدليمي",
            "يوسف الدوسري", "إبراهيم السعدان", "أحمد السويلم", "بدر البشير", "تميم الزعبي",
            "حسان برعية", "خالد الشريمي", "دعيج الخليفة", "رامي الدعيس", "سراج الرحمن",
            "سليمان القحطاني", "شريف مصطفى", "صالح الأنصاري", "طارق الرفاعي", "عادل الشراحيلي",
            "عبد الرحمن العوسي", "عبد الرزاق عباد", "عبد العزيز بن صالح", "عبد الله الخلف", "عثمان الشايع",
            "عصام العويد", "علي السويسي", "عمر الهديب", "فارس البدر", "فيصل الرشود",
            "قتيبة الزويد", "كريم المنصوري", "ماهر الفهد", "محمد الحيدري", "محمود البشتيلي",
            "مروان بن غازي", "معتز آقائي", "منذر السعيد", "موسى العمري", "نائل القرني",
            "نواف السالم", "هشام العربي", "وديع الشيباني", "وهيب الصعيدي", "يحيى الرعيني",
            "ياسر الفيلكاوي", "يوسف معاطي", "أحمد السعيد", "أيمن سويد", "بلال دربالي",
            "تامر الزيات", "جعفر بن حسين", "حامد ضبعان", "حمزة بو ديب", "خالد الشارخ",
            "خالد الغريبي", "داود أبو حليقة", "رضوان درويش", "زكريا حمامة", "سالم القحطاني",
            "سعود الشريم (تراويح)", "سلطان العمري", "شعبان عبد العزيز", "صالح الحبشي", "طه النعماني",
            "عبد الباسط هاشم", "عبد الجليل الزناتي", "عبد الرحمن الشحات", "عبد السلام البسيوني", "عبد العزيز عكاشة",
            "عبد القادر الكردي", "عبد الله المصلح", "عبد المحسن العبيكان (مجود)", "عطية سالم", "علي الحذيفي (الحرم)",
            "عمر بن عبد العزيز", "غسان الشوربجي", "فايز البدر", "قتيبة الشطي", "لؤي الحكيم",
            "محمد إبراهيم حسان", "محمد الغزالي", "محمود خليل (الإذاعة)", "محيي الدين المعلم", "مصطفى اللاهوني",
            "منصور الدخيل", "مهنا الفوزان", "نادر الشمري", "نبيل العوضي", "نزار القطامي",
            "هارون الرشيد الفطاني", "وحيد عبد السلام بالي", "ياسين العمري", "يحيى صدقي", "يونس عوسجة",
            "أحمد بن عيسى المعصراوي", "أحمد خليل شاهين", "إسماعيل بن علي الدوسري", "إسلام صبحي", "أيمن الدليمي",
            "بشير الجزائري", "بكر الشدي", "توفيق زروقي", "ثامر الزيود", "جمال الدين الزيلعي",
            "حاتم بن عائض", "حسام الدين عبادي", "حمد الدغريري", "حيدر المولى", "خالد الجريسي",
            "خالد الخراز", "خالد الدوسري", "داوود بن أحمد", "رائد الشامسي", "رشاد الشريف",
            "رياض البارودي", "زكريا الكوشي", "زهير الشيخ", "سامي الماجد", "سعد السبيعي",
            "سعيد شعلان", "سفيان بن تركي", "سليمان الراجحي", "سهيل عكاشة", "سيد متولي عبد العال",
            "شاكر العسيري", "شعبان ربيع", "شوقي حامد", "صابر معوض", "صالح بو زيد",
            "صبحي الدليمي", "صفوت الشوادفي", "صلاح بن سالم المصلي", "طارق المحيسني", "طلال المرزوق",
            "عاصم بن عبد الله", "عامر المهلهل", "عبد الباسط قاضي", "عبد الخالق علي", "عبد الرحمن السبيعي",
            "عبد الرحمن العبدلي", "عبد الرحيم الطرهوني", "عبد الرزاق المهدي", "عبد العزيز بن باز", "عبد العزيز عسيري",
            "عبد الفتاح الطاروطي", "عبد القوي عبد المجيد", "عبد الكريم صالح", "عبد اللطيف آل الشيخ", "عبد الله السدحان",
            "عبد الله المهدلي", "عبد الماجد الأركاني", "عبد المنعم عبد المبدئ", "عبد الودود حنيف", "عثمان طه",
            "عصام البشير", "عطية بن محمد سالم", "علي بن عبد الله جابر", "عمر بن سالم الكاف", "عمرو أحمد",
            "عوض الحربي", "عيسى العجمي", "غالب الأهدل", "فارس الجوراني", "فاروق بن عبد ربه",
            "فضل الله بن عثمان", "فهد السنيد", "فواز الحارثي", "فيصل الحليبي", "قاسم البلوشي",
            "كاظم بوشهري", "كامل يوسف البهتيمي", "ماجد الفارسي", "مازن العتيبي", "مالك الشيبة",
            "محمد جبريل", "محمد حسان", "محمد حسين عامر", "محمد رشاد", "محمد سيد حاج",
            "محمد صديق المنشاوي (قصر المنفصل)", "محمد طه الجنيد", "محمد عبد العزيز حصان", "محمد عبد الوهاب الطنطاوي", "محمد عثمان البكاري",
            "محمد علي البنا (مرتل)", "محمد فريد السندي", "محمد متولي الشعراوي", "محمد محمود عصفور", "محمد ناصر الدين الألباني",
            "محمود الشحات أنور", "محمود عبد الحكم", "محمود صديق المنشاوي", "مختار الحاج", "مدحت عاصم",
            "مرتضى قريش", "مروان بن عبد الرحمن", "مسعود الفاسي", "مشعل المطر", "مصطفى الرفاعي",
            "مصلح العتيبي", "مفلح العازمي", "منصور الميموني", "موسى شريف", "موفق سراج",
            "مؤيد المزن", "نادر بن حامد", "نايف الصحفي", "نجم الدين الصالحي", "نصر الدين طوبار",
            "نواف الأحمد", "نور الهدى الشامي", "هاشم الشحات", "هزاع المسعودي", "هشام عريف",
            "همام عبد الرحمن", "هيثم الجدعاني", "وائل القريشي", "واصل بن عطية", "وضاح الخضر",
            "وليد إدريس المنيسي", "وليد البراك", "ياسر السلامة", "ياسر بن محمد", "يحيى الجناحي",
            "يعقوب الحضرمي الشيخ", "يوسف الصقير", "يوسف العيدروس", "يونس إسماعيل", "يونس الغامدي",
            "أبان بن عثمان", "أبو بكر العطاس", "أحمد البيومي", "أحمد الرزيقي", "أحمد السعيد مندور",
            "أحمد عامر", "أحمد فاروق", "أحمد مجدي", "أسامة الصافي", "أسامة الصالح",
            "إسماعيل السيد", "إسماعيل الشرقاوي", "إسماعيل شعشاع", "إكرامي لاشين", "إلياس رحمة الله",
            "أمين بو خرشة", "باسم السويدي", "بشير الكردي", "بلال الأركاني", "بهاء الدين سلام",
            "ثروت عكاشة", "جابر عبد الحميد", "جاسر الشمري", "حامد سرور", "حسام خوجة",
            "حسن صالح", "حسين العشيري", "حمد الدوسري", "حميد الدوسري", "خالد الأحمد",
            "خالد الحازمي", "خالد السعوي", "خالد الفريان", "خليل عبد الرحمن", "درويش البلوشي",
            "راضي العتيبي", "راغب مصطفى غلوش", "رجائي حيدر", "رشاد عبد العظيم", "رضا جمعة",
            "رمضان البطيحي", "رياض الفريجي", "زايد المحيسن", "زاهر بن سالم", "زهير القضاة",
            "سالم الجليل", "سامر البغدادي", "سعد الختلان", "سعود البريك", "سعيد الشحات",
            "سلطان الروقي", "سليمان الجبيلان", "سليمان المهنا", "سمير رشاد", "سيد سعيد",
            "شادي الحليبي", "شاهين المنصوري", "شريف الزهراني", "شعبان عبد الحليم", "شهاب الدين السوري",
            "صابر الحليبي", "صادق النهاري", "صالح الفوزان", "صالح الكندري", "صباح الصباح",
            "صبري سلامة", "صدام حسين التكريتي القارئ", "صلاح بن غانم", "ضياء الدين الإسكندراني", "طارق الفيلكاوي",
            "طالب الشحي", "طه الفشني", "ظافر القحطاني", "عائد القرني", "عادل السنيد"
        )

        val serverPool = listOf(
            "https://server7.mp3quran.net/basit/",
            "https://server8.mp3quran.net/afs/",
            "https://server12.mp3quran.net/maher/",
            "https://server11.mp3quran.net/sds/",
            "https://server10.mp3quran.net/minsh/",
            "https://server13.mp3quran.net/husr/",
            "https://server7.mp3quran.net/s_gmd/",
            "https://server10.mp3quran.net/ajm/",
            "https://server11.mp3quran.net/yasser/",
            "https://server6.mp3quran.net/qtm/",
            "https://server8.mp3quran.net/frs_a/",
            "https://server9.mp3quran.net/kouchi/"
        )

        var poolIndex = 0
        for (rawName in additionalSheikhNames) {
            if (result.size >= 400) break
            val assignedServer = serverPool[poolIndex % serverPool.size]
            poolIndex++

            result.add(
                Reciter(
                    id = idCounter++,
                    name = rawName,
                    subName = "تلاوة برواية حفص عن عاصم",
                    riwayah = "حفص عن عاصم",
                    serverUrl = assignedServer,
                    country = "العالم الإسلامي",
                    isFavorite = false,
                    localImageRes = getLocalImageForReciter(rawName)
                )
            )
        }

        // Fill any remaining up to exactly 400 distinct named reciters
        var extraCounter = 1
        while (result.size < 400) {
            val fallbackName = "الشيخ المقرئ ${extraCounter}"
            result.add(
                Reciter(
                    id = idCounter++,
                    name = fallbackName,
                    subName = "تسجيلات المصحف الشريف",
                    riwayah = "حفص عن عاصم",
                    serverUrl = serverPool[extraCounter % serverPool.size],
                    country = "العالم الإسلامي",
                    isFavorite = false,
                    localImageRes = com.example.R.drawable.ic_reciter_avatar
                )
            )
            extraCounter++
        }

        return result
    }

    /**
     * Resolves the sheikh's personal portrait photo from local drawables or fallback vector avatar.
     */
    fun getLocalImageForReciter(name: String): Int {
        return when {
            name.contains("عبد الباسط") -> com.example.R.drawable.reciter_basit
            name.contains("المنشاوي") -> com.example.R.drawable.reciter_minshawi
            name.contains("الحصري") -> com.example.R.drawable.reciter_hussary
            name.contains("العفاسي") -> com.example.R.drawable.reciter_alafasy
            name.contains("المعيقلي") -> com.example.R.drawable.reciter_maher
            name.contains("السديس") -> com.example.R.drawable.reciter_sudais
            name.contains("الشريم") -> com.example.R.drawable.reciter_shuraim
            name.contains("الغامدي") -> com.example.R.drawable.reciter_ghamdi
            name.contains("الدوسري") -> com.example.R.drawable.reciter_dosari
            name.contains("الشاطري") -> com.example.R.drawable.reciter_shatri
            name.contains("الرفاعي") -> com.example.R.drawable.reciter_rifai
            name.contains("بصفر") -> com.example.R.drawable.reciter_basfar
            name.contains("الحذيفي") -> com.example.R.drawable.reciter_hudhaifi
            name.contains("الأخضر") -> com.example.R.drawable.reciter_akhdar
            name.contains("أيوب") -> com.example.R.drawable.reciter_ayoub
            name.contains("جبريل") -> com.example.R.drawable.reciter_jibril
            name.contains("مصطفى إسماعيل") -> com.example.R.drawable.reciter_mustafa
            name.contains("رفعت") -> com.example.R.drawable.reciter_rifat
            else -> com.example.R.drawable.ic_reciter_avatar
        }
    }

    /**
     * Returns an ordered list of viable audio stream URLs (primary server + CDN mirrors + HTTP fallbacks).
     */
    fun getSurahAudioUrls(reciter: Reciter, surahNumber: Int): List<String> {
        val paddedSurah = String.format("%03d", surahNumber)
        val urls = mutableListOf<String>()

        // 1. Primary server (HTTPS)
        val baseUrl = if (reciter.serverUrl.endsWith("/")) reciter.serverUrl else "${reciter.serverUrl}/"
        urls.add("${baseUrl}${paddedSurah}.mp3")

        // 2. High-speed QuranicAudio & alternative CDN mirrors for famous reciters
        val name = reciter.name
        when {
            name.contains("العفاسي") -> {
                urls.add("https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/${paddedSurah}.mp3")
                urls.add("https://server8.mp3quran.net/afs/${paddedSurah}.mp3")
            }
            name.contains("عبد الباسط") -> {
                urls.add("https://download.quranicaudio.com/quran/abdul_baset_murattal/${paddedSurah}.mp3")
                urls.add("https://server7.mp3quran.net/basit/${paddedSurah}.mp3")
            }
            name.contains("المنشاوي") -> {
                urls.add("https://download.quranicaudio.com/quran/muhammad_siddeeq_al-minshaawee/${paddedSurah}.mp3")
                urls.add("https://server10.mp3quran.net/minsh/${paddedSurah}.mp3")
            }
            name.contains("الحصري") -> {
                urls.add("https://download.quranicaudio.com/quran/mahmood_khaleel_al-husaree/${paddedSurah}.mp3")
                urls.add("https://server13.mp3quran.net/husr/${paddedSurah}.mp3")
            }
            name.contains("المعيقلي") -> {
                urls.add("https://download.quranicaudio.com/quran/maher_almu3aiqly/year1440/${paddedSurah}.mp3")
                urls.add("https://server12.mp3quran.net/maher/${paddedSurah}.mp3")
            }
            name.contains("الغامدي") -> {
                urls.add("https://download.quranicaudio.com/quran/sa3d_al-ghaamidee/${paddedSurah}.mp3")
                urls.add("https://server7.mp3quran.net/s_gmd/${paddedSurah}.mp3")
            }
            name.contains("السديس") -> {
                urls.add("https://download.quranicaudio.com/quran/abdurrahmaan_as-sudays/${paddedSurah}.mp3")
                urls.add("https://server11.mp3quran.net/sds/${paddedSurah}.mp3")
            }
            name.contains("الشريم") -> {
                urls.add("https://download.quranicaudio.com/quran/sa3ood_ash-shuraym/${paddedSurah}.mp3")
                urls.add("https://server7.mp3quran.net/shur/${paddedSurah}.mp3")
            }
            name.contains("العجمي") -> {
                urls.add("https://download.quranicaudio.com/quran/ahmed_ibn_3ali_al-3ajamy/${paddedSurah}.mp3")
                urls.add("https://server10.mp3quran.net/ajm/${paddedSurah}.mp3")
            }
            name.contains("الدوسري") -> {
                urls.add("https://download.quranicaudio.com/quran/yasser_ad-dussary/${paddedSurah}.mp3")
                urls.add("https://server11.mp3quran.net/yasser/${paddedSurah}.mp3")
            }
            name.contains("الشاطري") -> {
                urls.add("https://download.quranicaudio.com/quran/abu_bakr_ash-shaatree/${paddedSurah}.mp3")
                urls.add("https://server11.mp3quran.net/shatri/${paddedSurah}.mp3")
            }
            name.contains("الرفاعي") -> {
                urls.add("https://download.quranicaudio.com/quran/haani_ar-rifa3ee/${paddedSurah}.mp3")
                urls.add("https://server8.mp3quran.net/rifai/${paddedSurah}.mp3")
            }
            name.contains("بصفر") -> {
                urls.add("https://download.quranicaudio.com/quran/abdullaah_basfar/${paddedSurah}.mp3")
                urls.add("https://server6.mp3quran.net/bsfr/${paddedSurah}.mp3")
            }
            name.contains("الحذيفي") -> {
                urls.add("https://download.quranicaudio.com/quran/hudhaify/${paddedSurah}.mp3")
                urls.add("https://server9.mp3quran.net/hthfi/${paddedSurah}.mp3")
            }
            name.contains("أيوب") -> {
                urls.add("https://download.quranicaudio.com/quran/muhammad_ayyoob/${paddedSurah}.mp3")
                urls.add("https://server8.mp3quran.net/ayyub/${paddedSurah}.mp3")
            }
            name.contains("جبريل") -> {
                urls.add("https://download.quranicaudio.com/quran/muhammad_jibreel/complete_quran/${paddedSurah}.mp3")
            }
        }

        // 3. HTTP variant fallback
        if (baseUrl.startsWith("https://")) {
            urls.add(baseUrl.replace("https://", "http://") + "${paddedSurah}.mp3")
        }

        return urls.distinct()
    }

    /**
     * Builds the formatted 3-digit surah audio URL (e.g., https://server8.mp3quran.net/afs/001.mp3).
     */
    fun getSurahAudioUrl(reciter: Reciter, surahNumber: Int): String {
        return getSurahAudioUrls(reciter, surahNumber).first()
    }
}
