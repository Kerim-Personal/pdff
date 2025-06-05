package com.example.pdf // Paket adının doğru olduğundan emin ol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem // Geri butonu için
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener

// import com.example.pdf.R // R dosyasını import etmeyi unutma

class PdfViewActivity : AppCompatActivity(), OnLoadCompleteListener, OnErrorListener, OnPageErrorListener {

    private lateinit var pdfView: PDFView
    private lateinit var progressBar: ProgressBar
    private var pdfAssetName: String? = null

    companion object {
        const val EXTRA_PDF_ASSET_NAME = "pdf_asset_name"
        const val EXTRA_PDF_TITLE = "pdf_title" // Başlık için
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ekran görüntüsü alınmasını engelle
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_pdf_view)

        // Toolbar'a geri butonu ve başlık ekle (isteğe bağlı)
        pdfAssetName = intent.getStringExtra(EXTRA_PDF_ASSET_NAME)
        val pdfTitle = intent.getStringExtra(EXTRA_PDF_TITLE) ?: "PDF" // Başlık yoksa varsayılan

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = pdfTitle

        pdfView = findViewById(R.id.pdfView)
        progressBar = findViewById(R.id.progressBarPdf)

        if (pdfAssetName != null) {
            displayPdfFromAssets(pdfAssetName!!)
        } else {
            Toast.makeText(this, "PDF dosyası bulunamadı.", Toast.LENGTH_SHORT).show()
            finish() // Aktiviteyi kapat
        }
    }

    private fun displayPdfFromAssets(assetName: String) {
        progressBar.visibility = View.VISIBLE // Yükleniyor göstergesini göster
        pdfView.fromAsset(assetName)
            .defaultPage(0) // Başlangıç sayfası
            .enableSwipe(true) // Sayfalar arası kaydırmayı etkinleştir
            .swipeHorizontal(false) // Dikey kaydırma
            .enableAnnotationRendering(false) // Annotasyonları gösterme (temel görüntüleyici için)
            .onLoad(this) // Yükleme tamamlandığında çağrılacak listener
            .onError(this) // Hata oluştuğunda çağrılacak listener
            .onPageError(this) // Sayfa hatası oluştuğunda
            .enableAntialiasing(true) // Kenar yumuşatma (daha iyi görünüm için)
            .spacing(10) // Sayfalar arası boşluk (dp cinsinden)
            .load()
    }

    // PDF yükleme tamamlandığında
    override fun loadComplete(nbPages: Int) {
        progressBar.visibility = View.GONE // Yükleniyor göstergesini gizle
        Toast.makeText(this, "PDF Yüklendi ($nbPages sayfa)", Toast.LENGTH_SHORT).show()
    }

    // PDF yüklenirken genel bir hata oluşursa
    override fun onError(t: Throwable?) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Hata: " + t?.message, Toast.LENGTH_LONG).show()
        t?.printStackTrace()
        finish() // Hata durumunda aktiviteyi kapatabilirsin
    }

    // Belirli bir sayfa yüklenirken hata oluşursa
    override fun onPageError(page: Int, t: Throwable?) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Sayfa yüklenirken hata: $page - " + t?.message, Toast.LENGTH_LONG).show()
        t?.printStackTrace()
    }

    // Toolbar'daki geri butonuna basıldığında
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Aktiviteyi kapat
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}