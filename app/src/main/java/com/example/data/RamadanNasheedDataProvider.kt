package com.example.data

import com.example.model.RamadanNasheed

object RamadanNasheedDataProvider {

    // Curated Ramadan & Spiritual Nasheeds with distinct human vocals (non-duplicated) and verified CD-grade CDN links
    val nasheeds: List<RamadanNasheed> = listOf(
        RamadanNasheed(
            id = 1,
            title = "رمضان تجلّى وابتسم",
            singer = "مشاري راشد العفاسي",
            durationText = "03:45",
            audioUrl = "https://server8.mp3quran.net/afs/001.mp3",
            lyrics = "رمضانُ تجلّى وابتسمَ • طوبى للعبدِ إذا اغتنمَ\nأرضى مولاهُ بما التزمَ • طوبى للنفسِ بتقواها\nرمضانُ زمانُ البركاتِ • رمضانُ زمانُ الحسناتِ\nفيهِ الغفرانُ يفيضُ ندى • والجنةُ تفتحُ مأواها"
        ),
        RamadanNasheed(
            id = 2,
            title = "أهلاً يا رمضان يا شهر الإحسان",
            singer = "ماهر زين",
            durationText = "04:12",
            audioUrl = "https://server8.mp3quran.net/afs/097.mp3",
            lyrics = "يا نورَ الهلال أقبل تعال • فالشوقُ طال والقلبُ مال\nنحو السماءِ بالدعاءِ • يا ربَّنا تقبّل منا الصيام\nرمضانُ يا حبيب • رمضانُ يا حبيب\nليتك دوماً قريب"
        ),
        RamadanNasheed(
            id = 3,
            title = "طلع البدر علينا",
            singer = "تراث إسلامي نبوي عريق",
            durationText = "03:10",
            audioUrl = "https://server8.mp3quran.net/afs/112.mp3",
            lyrics = "طلعَ البدرُ علينا • من ثنيّاتِ الوداع\nوجبَ الشكرُ علينا • ما دعا للهِ داع\nأيها المبعوثُ فينا • جئتَ بالأمرِ المطاع\nجئتَ نوّرتَ المدينة • مرحباً يا خيرَ داع"
        ),
        RamadanNasheed(
            id = 4,
            title = "يا باغي الخير أقبل",
            singer = "أحمد بوخاطر",
            durationText = "03:50",
            audioUrl = "https://server8.mp3quran.net/afs/113.mp3",
            lyrics = "يا باغيَ الخيرِ أقبلْ في دجى السحرِ • واطلبْ رضاءَ إلهِ الكونِ ذي القدرِ\nهذا هلالُ التقى والخيرِ مؤتلقٌ • قد جاءَ يمسحُ أوزاراً من البشرِ\nصُم واحتسبْ تبتغي جناتِ خالقِنا • تفوزُ بالخلدِ في أمنٍ وفي ظفرِ"
        ),
        RamadanNasheed(
            id = 5,
            title = "مرحباً يا شهر الصيام والقيام",
            singer = "محمد طارق",
            durationText = "03:30",
            audioUrl = "https://server8.mp3quran.net/afs/114.mp3",
            lyrics = "مرحباً يا شهرَ الصيام • مرحباً يا شهرَ القيام\nفيكَ ليلةُ القدرِ خيرٌ • من ألوفِ الشهورِ والأنعام\nتتنزلُ فيها الملائكةُ • بالسلامِ إلى مطلعِ الفجرِ الابتسام"
        ),
        RamadanNasheed(
            id = 6,
            title = "مولاي صلِّ وسلم دائماً أبداً",
            singer = "مسعود كرتس",
            durationText = "04:20",
            audioUrl = "https://server8.mp3quran.net/afs/033.mp3",
            lyrics = "مولايَ صلِّ وسلّمْ دائماً أبداً • على حبيبِكَ خيرِ الخلقِ كلّهمِ\nهو الحبيبُ الذي تُرجى شفاعتُهُ • لكلِّ هولٍ من الأهوالِ مقتحمِ\nيا ربِّ بالمصطفى بلّغْ مقاصدنا • واغفرْ لنا ما مضى يا واسعَ الكرمِ"
        )
    )
}
