package com.example.data.local

import com.example.data.model.Question

object SampleQuestionsBank {

    fun getQuestionsForSubject(subject: String, count: Int): List<Question> {
        val pool = when {
            subject.contains("Matematik", ignoreCase = true) -> mathQuestions
            subject.contains("Fen", ignoreCase = true) -> scienceQuestions
            subject.contains("Türkçe", ignoreCase = true) -> turkishQuestions
            subject.contains("İnkılap", ignoreCase = true) -> historyQuestions
            subject.contains("Din", ignoreCase = true) -> religionQuestions
            subject.contains("İngilizce", ignoreCase = true) || subject.contains("Dil", ignoreCase = true) -> englishQuestions
            else -> mathQuestions + scienceQuestions + turkishQuestions
        }
        return pool.shuffled().take(count)
    }

    private val mathQuestions = listOf(
        Question(
            q = "Ayrıt uzunlukları 48 m ve 72 m olan dikdörtgen şeklindeki bir bahçenin etrafına, köşelere de gelmek şartıyla eşit aralıklarla fidan dikilecektir. İki fidan arasındaki mesafe en fazla kaç metre olabilir?",
            options = listOf("A) 12", "B) 18", "C) 24", "D) 36"),
            answer = 2,
            sol = "Bahçenin etrafına eşit ve en büyük aralıklarla fidan dikmek için 48 ile 72 sayılarının EBOB'u (En Büyük Ortak Bölen) bulunmalıdır. EBOB(48, 72) = 24 m'dir. Dolayısıyla iki fidan arasındaki mesafe en fazla 24 metre olabilir.",
            subject = "Matematik"
        ),
        Question(
            q = "2^8 · 5^6 işleminin sonucu kaç basamaklı bir sayıdır?",
            options = listOf("A) 6", "B) 7", "C) 8", "D) 9"),
            answer = 1,
            sol = "2^8 ifadesini 2^2 · 2^6 şeklinde yazarsak: 2^2 · 2^6 · 5^6 = 4 · (2 · 5)^6 = 4 · 10^6 olur. 4 sayısının yanına 6 tane sıfır geleceğinden sayı toplam 7 basamaklıdır.",
            subject = "Matematik"
        ),
        Question(
            q = "Alanı 147 cm² olan bir karenin bir kenar uzunluğu hangi iki ardışık tam sayı arasındadır?",
            options = listOf("A) 10 ile 11", "B) 11 ile 12", "C) 12 ile 13", "D) 13 ile 14"),
            answer = 2,
            sol = "Karenin bir kenar uzunluğu a = √147 cm'dir. 12^2 = 144 ve 13^2 = 169 olduğuna göre; √144 < √147 < √169 eşitsizliğinden kenar uzunluğu 12 ile 13 arasındadır.",
            subject = "Matematik"
        ),
        Question(
            q = "Bir torbada 6 kırmızı, 8 mavi ve 10 sarı top bulunmaktadır. Torbadan rastgele çekilen bir topun kırmızı OLMAMA olasılığı kaçtır?",
            options = listOf("A) 1/4", "B) 3/4", "C) 1/3", "D) 2/3"),
            answer = 1,
            sol = "Toplam top sayısı = 6 + 8 + 10 = 24'tür. Kırmızı olmayan top sayısı (mavi + sarı) = 8 + 10 = 18'dir. Olasılık = İstenen / Toplam = 18/24 = 3/4 bulunur.",
            subject = "Matematik"
        ),
        Question(
            q = "(3x - 4)^2 ifadesinin özdeşi aşağıdakilerden hangisidir?",
            options = listOf("A) 9x² - 16", "B) 9x² - 12x + 16", "C) 9x² - 24x + 16", "D) 9x² + 24x + 16"),
            answer = 2,
            sol = "Tam kare açılımı (a - b)^2 = a^2 - 2ab + b^2 formülünden: (3x)^2 - 2·(3x)·4 + 4^2 = 9x² - 24x + 16 elde edilir.",
            subject = "Matematik"
        ),
        Question(
            q = "Kenar uzunlukları 2√3 cm ve 3√2 cm olan bir dikdörtgenin alanı kaç cm² dir?",
            options = listOf("A) 6√5", "B) 6√6", "C) 12√6", "D) 36"),
            answer = 1,
            sol = "Dikdörtgenin alanı = Kısa kenar · Uzun kenar = (2√3) · (3√2) = (2 · 3) · √(3 · 2) = 6√6 cm²'dir.",
            subject = "Matematik"
        )
    )

    private val scienceQuestions = listOf(
        Question(
            q = "21 Aralık tarihinde Güneş ışınlarının Güney Yarımküre'de dik açıyla düştüğü enlem aşağıdakilerden hangisidir?",
            options = listOf("A) Yengeç Dönencesi", "B) Ekvator", "C) Oğlak Dönencesi", "D) Kutup Dairesi"),
            answer = 2,
            sol = "21 Aralık tarihinde Güneş ışınları Güney Yarımküre'de bulunan Oğlak Dönencesi'ne öğle vakti 90 derecelik dik açıyla düşer. Bu tarihte Güney Yarımküre'de en uzun gündüz ve yaz mevsimi başlar.",
            subject = "Fen Bilimleri"
        ),
        Question(
            q = "Bir DNA molekülünde 600 Guanin nükleotidi bulunmaktadır. Bu DNA'da Guanin sayısı toplam nükleotid sayısının %20'si olduğuna göre, Timin nükleotidi sayısı kaçtır?",
            options = listOf("A) 600", "B) 900", "C) 1200", "D) 1500"),
            answer = 1,
            sol = "G = 600 ve %20 ise, C = 600 (%20)'dir. G + C = %40 eder. Geriye kalan A + T oranı %60'tır. Dolayısıyla Timin oranı %30 olur. %20'si 600 olan sayının %30'u: (600 / 20) · 30 = 900 Timin nükleotididir.",
            subject = "Fen Bilimleri"
        ),
        Question(
            q = "Katı basıncını artırmak isteyen bir mühendis, aşağıdaki uygulamalardan hangisini tercih etmelidir?",
            options = listOf(
                "A) Traktör tekerleklerinin geniş yüzeyli yapılması",
                "B) Bıçakların kesici ucunun inceltilip bilenmesi",
                "C) Kar ayakkabılarının geniş tabanlı tasarlanması",
                "D) Trenlerde tekerlek sayısının artırılması"
            ),
            answer = 1,
            sol = "Katı basıncı P = F / S (Kuvvet / Temas Alanı) formülüne dayanır. Temas yüzeyi küçüldükçe basınç artar. Bıçağın bilenmesi temas yüzeyini küçültür ve basıncı artırır. Diğer şıklarda yüzey alanı büyütülerek basınç azaltılmıştır.",
            subject = "Fen Bilimleri"
        ),
        Question(
            q = "Aşağıdakilerden hangisi kimyasal bir değişime örnektir?",
            options = listOf(
                "A) Suyun donarak buz haline gelmesi",
                "B) Şekerin suda çözünmesi",
                "C) Demirin paslanması",
                "D) Kağıdın yırtılarak parçalanması"
            ),
            answer = 2,
            sol = "Demirin paslanması demir atomlarının oksijenle tepkimeye girmesiyle (oksitlenme) oluşur ve maddenin kimlik özelliği değişir. Buzun donması, şekerin çözünmesi ve kağıdın yırtılması ise fiziksel değişimdir.",
            subject = "Fen Bilimleri"
        ),
        Question(
            q = "Periyodik tablonun aynı yatay sırasında (aynı periyotta) soldan sağa doğru gidildikçe genel olarak aşağıdakilerden hangisi artar?",
            options = listOf("A) Katman sayısı", "B) Atom numarası", "C) Metalik özellik", "D) Atom yarıçapı"),
            answer = 1,
            sol = "Periyodik tabloda soldan sağa doğru gidildikçe proton sayısı yani atom numarası 1'er 1'er artar. Katman sayısı aynı periyotta değişmez, metalik özellik ve atom yarıçapı ise genel olarak azalır.",
            subject = "Fen Bilimleri"
        )
    )

    private val turkishQuestions = listOf(
        Question(
            q = "\"Güneş batarken dağların ardında parıldayan ışıklar bana çocukluğumu hatırlattı.\" cümlesinde yer alan fiilimsilerin türleri sırasıyla hangisinde doğru verilmiştir?",
            options = listOf(
                "A) İsim-fiil, Zarf-fiil",
                "B) Zarf-fiil, Sıfat-fiil",
                "C) Sıfat-fiil, İsim-fiil",
                "D) Zarf-fiil, İsim-fiil"
            ),
            answer = 1,
            sol = "\"batarken\" kelimesindeki -ken eki zarf-fiil (bağ-fiil) ekidir. \"parıldayan\" kelimesindeki -an eki ise sıfat-fiil (ortaç) ekidir. Dolayısıyla sırasıyla Zarf-fiil ve Sıfat-fiil yer almaktadır.",
            subject = "Türkçe"
        ),
        Question(
            q = "\"Dedemin anlattığı hikâyeyi hepimiz pürdikkat dinledik.\" cümlesinde altı çizili \"pürdikkat\" söz öbeğinin cümleye kattığı anlam aşağıdakilerden hangisidir?",
            options = listOf(
                "A) Çok büyük bir heyecanla",
                "B) Bütün dikkatini vererek, eksiksiz bir yoğunlaşmayla",
                "C) Şüphe ve kararsızlıkla",
                "D) Aceleyle ve sabırsızca"
            ),
            answer = 1,
            sol = "\"Pürdikkat\", Farsça 'pür' (dolu, çok) ve dikkat sözcüklerinden oluşup tüm dikkatini vererek, büyük bir dikkat ve özenle dinlemek/izlemek anlamındadır.",
            subject = "Türkçe"
        ),
        Question(
            q = "Aşağıdaki cümlelerin hangisinde nesne eksikliğinden kaynaklanan bir anlatım bozukluğu vardır?",
            options = listOf(
                "A) Ona her konuda güveniyor ve çok seviyordu.",
                "B) Yarın sabah erkenden yola çıkacağız.",
                "C) Kitapları masaya güzelce yerleştirdi.",
                "D) Sınav sonuçları nihayet açıklandı."
            ),
            answer = 0,
            sol = "\"Ona her konuda güveniyor ve (onu) çok seviyordu.\" İkinci cümleye 'onu' nesnesi getirilmezse 'ona seviyordu' gibi yanlış bir bağdaşma oluşur. Bu nesne eksikliğidir.",
            subject = "Türkçe"
        ),
        Question(
            q = "\"Sanatçı, eserlerinde sadece gördüklerini değil; hissettiklerini de nakış gibi işlemelidir.\" cümlesinden çıkarılabilecek en kapsamlı yargı hangisidir?",
            options = listOf(
                "A) Sanat sadece somut varlıkları anlatmalıdır.",
                "B) Sanatta duygu ve estetik işçilik vazgeçilmezdir.",
                "C) Detaylara girmek eseri karmaşıklaştırır.",
                "D) Sanatçılar sadece kendi hayatlarını anlatır."
            ),
            answer = 1,
            sol = "Cümlede hem hissetmek (duygu) hem de nakış gibi işlemek (estetik ustalık, titizlik) vurgulanmıştır. Dolayısıyla sanatın duygu ve ince işçilik gerektirdiği ana yargıdır.",
            subject = "Türkçe"
        )
    )

    private val historyQuestions = listOf(
        Question(
            q = "Mustafa Kemal'in doğup büyüdüğü Selanik şehrinin hangi özelliği, onun farklı kültürleri tanımasında ve fikir dünyasının zenginleşmesinde en etkili olmuştur?",
            options = listOf(
                "A) Askeri bir garnizon şehri olması",
                "B) Çok uluslu ve canlı bir liman/demiryolu kenti olması",
                "C) İkliminin ılıman olması",
                "D) Osmanlı'nın başkenti olması"
            ),
            answer = 1,
            sol = "Selanik; Türk, Rum, Musevi, Ermeni ve Bulgar gibi farklı milletlerin bir arada yaşadığı, Avrupa'daki gazete ve dergilerin kolayca ulaştığı çok uluslu ve gelişmiş bir ticaret/liman kentiydi. Bu durum Mustafa Kemal'in çok yönlü ve hoşgörülü bir vizyon kazanmasını sağlamıştır.",
            subject = "T.C. İnkılap Tarihi ve Atatürkçülük"
        ),
        Question(
            q = "Amasya Genelgesi'nde yer alan \"Milletin bağımsızlığını yine milletin azim ve kararı kurtaracaktır.\" maddesi Kurtuluş Savaşı'nın hangi yönünü belirtmektedir?",
            options = listOf(
                "A) Sadece gerekçesini",
                "B) Sadece savaş tarihini",
                "C) Amacını ve yöntemini",
                "D) Dış politikasını"
            ),
            answer = 2,
            sol = "\"Milletin bağımsızlığı\" Kurtuluş Savaşı'nın AMACINI, \"Milletin azim ve kararı\" ise bu mücadelenin halk iradesine dayalı YÖNTEMİNİ ifade eder.",
            subject = "T.C. İnkılap Tarihi ve Atatürkçülük"
        ),
        Question(
            q = "Misak-ı Milli kararlarında yer alan \"Siyasi, adli ve mali gelişmemizi engelleyen kapitülasyonlar kaldırılmalıdır.\" kararı doğrudan hangi ilkeyi hedefler?",
            options = listOf("A) Tam bağımsızlık", "B) Batıcılık", "C) Gelenekçilik", "D) Yayılmacılık"),
            answer = 0,
            sol = "Kapitülasyonlar bir ülkenin ekonomik ve hukuki egemenliğini zedeleyen ayrıcalıklardır. Bunların reddedilmesi devletin tam ve koşulsuz bağımsızlığını amaçlar.",
            subject = "T.C. İnkılap Tarihi ve Atatürkçülük"
        )
    )

    private val religionQuestions = listOf(
        Question(
            q = "İslam dinine göre bir Müslümanın zekât vermekle yükümlü olabilmesi için sahip olması gereken zenginlik ölçüsüne ne ad verilir?",
            options = listOf("A) Sadaka-i Cariye", "B) Nisap miktarı", "C) Öşür", "D) İnfak"),
            answer = 1,
            sol = "Dinen zengin sayılmak ve zekât vermekle mükellef olmak için belirlenen asgari zenginlik sınırına 'Nisap' denir (yaklaşık 80.18 gram altın veya değeri).",
            subject = "Din Kültürü ve Ahlak Bilgisi"
        ),
        Question(
            q = "Evrendeki ölçü, düzen ve yasaları ifade eden, Allah'ın her şeyi bir plana göre yaratmasını belirten kavram aşağıdakilerden hangisidir?",
            options = listOf("A) Tevekkül", "B) Ecel", "C) Kader", "D) Rızık"),
            answer = 2,
            sol = "Kader; Allah'ın evrendeki her şeyi belirli bir ölçü, düzen, ahenk ve yasa ile önceden takdir etmesi ve planlamasıdır.",
            subject = "Din Kültürü ve Ahlak Bilgisi"
        )
    )

    private val englishQuestions = listOf(
        Question(
            q = "Emily: \"Would you like to come to my birthday party on Sunday afternoon?\"\nDavid: \"I'd love to, but I have to study for my LGS exam.\"\nAccording to the conversation, David ________.",
            options = listOf(
                "A) accepts the invitation excitedly",
                "B) refuses the invitation and gives an excuse",
                "C) organizes another party",
                "D) invites Emily to his house"
            ),
            answer = 1,
            sol = "David 'I'd love to, but...' diyerek daveti nazikçe reddetmiş ve LGS sınavına çalışması gerektiğini mazeret (excuse) olarak sunmuştur. Doğru yanıt B'dir.",
            subject = "İngilizce"
        ),
        Question(
            q = "Which of the following proverbs best matches the qualities of a true friend?\n\"A friend in need is a friend indeed.\"",
            options = listOf(
                "A) True friends only talk about good things.",
                "B) Good friends support each other in difficult times.",
                "C) Friends never disagree on any topic.",
                "D) You should only spend time with rich friends."
            ),
            answer = 1,
            sol = "\"A friend in need is a friend indeed\" atasözü 'Dost kara günde belli olur' anlamına gelir. Zor zamanlarda birbirine destek olan arkadaşları anlatır.",
            subject = "İngilizce"
        )
    )
}
