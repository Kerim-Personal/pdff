package com.example.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewCourses: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Eğer temanızda bir ActionBar (üst çubuk) varsa, başlığı burada ayarlayabilirsiniz.
        // Artık Manifest'teki android:label değerini veya programatik olarak ayarlanan bir başlığı kullanacaktır.
        // Örneğin:
        // supportActionBar?.title = getString(R.string.app_title_on_main_screen)

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses)
        setupRecyclerView()
        loadCourses()
    }

    // Menüyü oluşturmak için eklenen yeni metod
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Menü öğesi seçimlerini işlemek için eklenen yeni metod
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // SettingsActivity'yi aç
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter(
            this,
            courseList,
            onTopicClickListener = { courseTitle, topicTitle ->
                Toast.makeText(this, "$courseTitle - $topicTitle (PDF dosyası bulunamadı)", Toast.LENGTH_SHORT).show()
            },
            onPdfClickListener = { courseTitle, topicTitle, pdfAssetName ->
                val intent = Intent(this, PdfViewActivity::class.java)
                intent.putExtra(PdfViewActivity.EXTRA_PDF_ASSET_NAME, pdfAssetName)
                intent.putExtra(PdfViewActivity.EXTRA_PDF_TITLE, "$courseTitle - $topicTitle")
                startActivity(intent)
            }
        )

        recyclerViewCourses.layoutManager = LinearLayoutManager(this)
        recyclerViewCourses.adapter = courseAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCourses() {
        // Mevcut loadCourses() metodunuz
        courseList.clear()

        courseList.add(
            Course(
                "Kalkülüs", listOf(
                    "Limit ve Süreklilik",
                    "Türev Tanımı ve Kuralları",
                    "Türev Uygulamaları (Ekstrema, Büküm)",
                    "Ortalama Değer Teoremleri",
                    "Belirsiz İntegral ve Teknikleri",
                    "Belirli İntegral ve Uygulamaları",
                    "Diziler ve Seriler",
                    "Kuvvet Serileri (Taylor, Maclaurin)",
                    "Çok Değişkenli Fonksiyonlar",
                    "Kısmi Türevler ve Lagrange Çarpanları",
                    "Çok Katlı İntegraller ve Uygulamaları (Hacim, Yüzey Alanı)",
                    "Vektör Analizi (Gradyan, Diverjans, Stokes)",
                    "Fourier Dönüşümleri",
                    "Metrik Uzaylara Giriş"
                )
            )
        )

        courseList.add(
            Course(
                "Kompleks Analiz", listOf(
                    "Kompleks Sayılar ve Düzlem",
                    "Analitik Fonksiyonlar",
                    "Cauchy-Riemann Denklemleri",
                    "Kompleks İntegrasyon",
                    "Cauchy İntegral Teoremi ve Formülü",
                    "Taylor ve Laurent Serileri",
                    "Rezidü Teoremi, Uygulamaları ve Kontur İntegrali Teknikleri",
                    "Konform Dönüşümler",
                    "Kompleks Dinamik Sistemler"
                )
            )
        )

        courseList.add(
            Course(
                "Nümerik Analiz", listOf(
                    "Hata Analizi ve Sayısal Stabilite",
                    "Denklem Köklerini Bulma Metotları",
                    "Lineer Sistemlerin Sayısal Çözümü",
                    "İteratif Metotlar (Jacobi, Gauss-Seidel)",
                    "İnterpolasyon ve Polinom Yaklaşımı",
                    "En Küçük Kareler Yöntemi",
                    "Sayısal Türev, İntegral ve Diferansiyel Denklemler",
                    "Matris Ayrışımları (LU, QR, SVD)",
                    "Optimizasyon Yöntemleri (Gradyan İniş, Newton)"
                )
            )
        )

        courseList.add(
            Course(
                "Lineer Cebir", listOf(
                    "Matrisler ve Determinantlar",
                    "Lineer Denklem Sistemleri",
                    "Vektör Uzayları ve Alt Uzaylar",
                    "Lineer Bağımsızlık, Taban ve Boyut",
                    "Lineer Dönüşümler",
                    "Çekirdek ve Görüntü",
                    "Özdeğerler ve Özvektörler",
                    "Köşegenleştirme",
                    "İç Çarpım Uzayları, Ortogonalite ve Gram-Schmidt",
                    "Tensörler ve Uygulamaları",
                    "Sayısal Lineer Cebir (Konjuge Gradyan)"
                )
            )
        )

        courseList.add(
            Course(
                "Soyut Matematik", listOf(
                    "Mantık ve Önermeler",
                    "Kümeler Teorisi",
                    "İspat Yöntemleri",
                    "Bağıntılar (Denklik, Sıralama)",
                    "Fonksiyonlar (Birebir, Örten)",
                    "Kardinalite, Sayılabilirlik ve Süreklilik Hipotezi",
                    "Temel Kategoriler Teorisi",
                    "Modeller Teorisi (Giriş)"
                )
            )
        )

        courseList.add(
            Course(
                "Cebir", listOf(
                    "Gruplar ve Alt Gruplar",
                    "Devirli Gruplar ve Permütasyon Grupları",
                    "Lagrange Teoremi",
                    "Normal Alt Gruplar ve Bölüm Grupları",
                    "Grup Homomorfizmaları",
                    "Halkalar, İdealler, Cisimler ve Galois Teorisi",
                    "Modül Teorisi"
                )
            )
        )

        courseList.add(
            Course(
                "Sayılar Teorisi", listOf(
                    "Bölünebilme ve Öklid Algoritması",
                    "Asal Sayılar",
                    "Modüler Aritmetik",
                    "Çin Kalan Teoremi",
                    "Fermat ve Euler Teoremleri",
                    "Diophantus Denklemleri",
                    "Kriptografi ve Uygulamaları (RSA, Eliptik Eğriler)",
                    "Kuadratik Kalıntılar ve Legendre Sembolü",
                    "Analitik Sayılar Teorisi (Zeta Fonksiyonu, Asal Sayı Teoremi)"
                )
            )
        )

        courseList.add(
            Course(
                "Diferansiyel Denklemler", listOf(
                    "Birinci Dereceden Denklemler",
                    "Yüksek Dereceden Lineer Denklemler",
                    "Belirsiz Katsayılar Yöntemi",
                    "Parametrelerin Değişimi Yöntemi",
                    "Laplace Dönüşümleri",
                    "Başlangıç Değer Problemleri",
                    "Lineer ve Doğrusal Olmayan Denklem Sistemleri",
                    "Kaotik Sistemler",
                    "Nümerik Çözüm Yöntemleri"
                )
            )
        )

        courseList.add(
            Course(
                "Kısmi Türevli Diferansiyel Denklemler", listOf(
                    "Temel Kavramlar ve Sınıflandırma",
                    "Fourier Serileri ve Dönüşümleri",
                    "Değişkenlere Ayırma Yöntemi",
                    "Isı Denklemi",
                    "Dalga Denklemi",
                    "Laplace Denklemi",
                    "Yeşil Fonksiyonları",
                    "Sonlu Elemanlar Yöntemi"
                )
            )
        )

        courseList.add(
            Course(
                "Analitik Geometri", listOf(
                    "Koordinat Sistemleri ve Vektörler",
                    "Uzayda Doğru ve Düzlem Denklemleri",
                    "Konik Kesitler (Elips, Parabol, Hiperbol)",
                    "Kuadrik Yüzeyler",
                    "Dönüşüm Geometrisi ve Simetri Grupları",
                    "Projektif Geometri",
                    "Vektör Alanları ve Uygulamaları"
                )
            )
        )

        courseList.add(
            Course(
                "Diferansiyel Geometri", listOf(
                    "Eğriler Teorisi",
                    "Frenet-Serret Formülleri",
                    "Yüzeyler Teorisi",
                    "Birinci ve İkinci Temel Formlar",
                    "Gauss Eğriliği",
                    "Gauss-Bonnet Teoremi ve Uygulamaları",
                    "Riemann Geometrisi",
                    "Manifoldlar Teorisi"
                )
            )
        )

        courseList.add(
            Course(
                "Topoloji", listOf(
                    "Topolojik Uzaylar",
                    "Metrik Uzaylar",
                    "Süreklilik ve Homeomorfizmler",
                    "Bağlantılılık",
                    "Kompaktlık",
                    "Temel Grup, Homotopi ve Homoloji",
                    "Knot Teorisi",
                    "Homoloji ve Kohomoloji"
                )
            )
        )

        courseList.add(
            Course(
                "Olasılık", listOf(
                    "Sayma Teknikleri ve Olasılık Aksiyomları",
                    "Koşullu Olasılık ve Bayes Teoremi",
                    "Rastgele Değişkenler",
                    "Kesikli Dağılımlar (Binom, Poisson)",
                    "Sürekli Dağılımlar (Üstel, Normal)",
                    "Beklenen Değer ve Varyans",
                    "Limit Teoremleri ve Olasılıksal Süreçler",
                    "Markov Zincirleri",
                    "İstatistiksel Çıkarım"
                )
            )
        )

        courseList.add(
            Course(
                "Fonksiyonel Analiz", listOf(
                    "Normlu Uzaylar ve Banach Uzayları",
                    "Hilbert Uzayları",
                    "Lineer Operatörler",
                    "Spectral Teori",
                    "Zayıf Topolojiler",
                    "Fonksiyonel Analizin Uygulamaları (Kuantum Mekaniği, Sinyal İşleme)"
                )
            )
        )

        courseAdapter.notifyDataSetChanged()
    }
}