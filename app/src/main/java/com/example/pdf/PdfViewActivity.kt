package com.example.pdf

// YENİ: BuildConfig importu
import com.example.pdf.BuildConfig // Bu satırın olduğundan emin olun
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PdfViewActivity : AppCompatActivity(), OnLoadCompleteListener, OnErrorListener, OnPageErrorListener {

    private lateinit var pdfView: PDFView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAiChat: FloatingActionButton
    private var pdfAssetName: String? = null
    private var fullPdfText: String? = null

    private val generativeModel by lazy {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY" || apiKey == "\"\"") {
            Log.e("GeminiAI", "API Anahtarı BuildConfig içerisinde bulunamadı veya geçersiz. Lütfen local.properties dosyasını ve build.gradle.kts yapılandırmasını kontrol edin.")
            // Kullanıcıya Toast ile bilgi verilebilir.
            Toast.makeText(this, "AI Asistanı için API anahtarı yapılandırılmamış.", Toast.LENGTH_LONG).show()
        }
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }

    companion object {
        const val EXTRA_PDF_ASSET_NAME = "pdf_asset_name"
        const val EXTRA_PDF_TITLE = "pdf_title"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_pdf_view)

        pdfAssetName = intent.getStringExtra(EXTRA_PDF_ASSET_NAME)
        val pdfTitle = intent.getStringExtra(EXTRA_PDF_TITLE) ?: getString(R.string.app_name)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = pdfTitle

        pdfView = findViewById(R.id.pdfView)
        progressBar = findViewById(R.id.progressBarPdf)
        fabAiChat = findViewById(R.id.fab_ai_chat)
        fabAiChat.visibility = View.GONE

        if (pdfAssetName != null) {
            displayPdfFromAssets(pdfAssetName!!)
        } else {
            Toast.makeText(this, getString(R.string.pdf_not_found), Toast.LENGTH_SHORT).show()
            finish()
        }

        fabAiChat.setOnClickListener {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY" || apiKey == "\"\"") {
                Toast.makeText(this, "AI özelliği için API anahtarı ayarlanmamış.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (fullPdfText != null) {
                showAiChatDialog()
            } else {
                Toast.makeText(this, "PDF metni henüz hazır değil, lütfen bekleyin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayPdfFromAssets(assetName: String) {
        progressBar.visibility = View.VISIBLE
        try {
            pdfView.fromAsset(assetName)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onLoad(this)
                .onError(this)
                .onPageError(this)
                .load()
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Log.e("PdfViewError", "PDF yüklenirken hata: ${e.localizedMessage}")
            Toast.makeText(this, "PDF yüklenemedi: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun showAiChatDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_ai_chat, null)
        builder.setView(dialogView)

        val editTextQuestion = dialogView.findViewById<EditText>(R.id.editTextQuestion)
        val buttonSend = dialogView.findViewById<Button>(R.id.buttonSend)
        val textViewAnswer = dialogView.findViewById<TextView>(R.id.textViewAnswer)
        val progressChat = dialogView.findViewById<ProgressBar>(R.id.progressChat)

        textViewAnswer.movementMethod = ScrollingMovementMethod()

        val dialog = builder.create()

        buttonSend.setOnClickListener {
            val question = editTextQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                textViewAnswer.text = ""
                progressChat.visibility = View.VISIBLE
                buttonSend.isEnabled = false

                lifecycleScope.launch {
                    try {
                        val prompt = """
                        SADECE ve SADECE aşağıdaki PDF metnini referans alarak kullanıcının sorusunu yanıtla.
                        Eğer sorunun cevabı bu metinde kesin olarak bulunmuyorsa, "Bu sorunun cevabı belgede bulunmuyor." şeklinde yanıt ver.
                        Cevabını Markdown formatında (örneğin, başlıklar için #, alt başlıklar için ##, listeler için * veya - kullanarak) ve Türkçe olarak ver.

                        Kullanıcının Sorusu: "$question"

                        PDF Metni:
                        "$fullPdfText"
                        """.trimIndent()

                        val responseText = withContext(Dispatchers.IO) {
                            generativeModel.generateContent(prompt).text
                        }
                        textViewAnswer.text = responseText ?: getString(R.string.ai_chat_error)
                    } catch (e: Exception) {
                        textViewAnswer.text = getString(R.string.ai_chat_error)
                        Log.e("GeminiError", "AI Hatası: ", e)
                        Toast.makeText(applicationContext, "AI Asistanı ile iletişim kurulamadı: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    } finally {
                        progressChat.visibility = View.GONE
                        buttonSend.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Lütfen bir soru girin.", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun extractTextFromPdf(assetName: String) {
        // Bu fonksiyon artık ana progressBar'ı doğrudan yönetmiyor,
        // çünkü displayPdfFromAssets ve loadComplete bunu yapıyor.
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                PDFBoxResourceLoader.init(applicationContext)
                assets.open(assetName).use { inputStream ->
                    PDDocument.load(inputStream).use { document ->
                        val stripper = PDFTextStripper()
                        val text = stripper.getText(document)
                        withContext(Dispatchers.Main) {
                            fullPdfText = text
                            val apiKey = BuildConfig.GEMINI_API_KEY
                            if (text.isNotBlank() && apiKey.isNotEmpty() && apiKey != "YOUR_API_KEY" && apiKey != "\"\"") {
                                fabAiChat.visibility = View.VISIBLE
                            } else if (text.isBlank()) {
                                Toast.makeText(applicationContext, "PDF'ten metin çıkarılamadı veya PDF boş.", Toast.LENGTH_LONG).show()
                            }
                            Log.d("PdfTextExtraction", "Metin başarıyla çıkarıldı. Karakter sayısı: ${text.length}")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "PDF metni çıkarılırken hata: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.e("PdfTextExtraction", "Hata: ", e)
                }
            }
            // Boş if bloğu (eski satır 212 civarı) kaldırıldı.
        }
    }

    override fun loadComplete(nbPages: Int) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, getString(R.string.pdf_loaded_toast, nbPages), Toast.LENGTH_SHORT).show()
        pdfAssetName?.let {
            if (fullPdfText == null) {
                extractTextFromPdf(it)
            }
        }
    }

    override fun onError(t: Throwable?) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, getString(R.string.error_toast, t?.localizedMessage ?: "Bilinmeyen PDF hatası"), Toast.LENGTH_LONG).show()
        Log.e("PdfView_onError", "PDF Yükleme Hatası", t)
        finish()
    }

    override fun onPageError(page: Int, t: Throwable?) {
        Toast.makeText(this, getString(R.string.page_load_error_toast, page, t?.localizedMessage ?: "Bilinmeyen sayfa hatası"), Toast.LENGTH_LONG).show()
        Log.e("PdfView_onPageError", "Sayfa Yükleme Hatası: $page", t)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
