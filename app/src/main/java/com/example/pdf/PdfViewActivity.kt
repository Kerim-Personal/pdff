package com.example.pdf

import android.content.Context // Required for attachBaseContext
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener

class PdfViewActivity : AppCompatActivity(), OnLoadCompleteListener, OnErrorListener, OnPageErrorListener {

    private lateinit var pdfView: PDFView
    private lateinit var progressBar: ProgressBar
    private var pdfAssetName: String? = null

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
        val pdfTitle = intent.getStringExtra(EXTRA_PDF_TITLE) ?: "PDF"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = pdfTitle // Title will be set based on the activity's locale

        pdfView = findViewById(R.id.pdfView)
        progressBar = findViewById(R.id.progressBarPdf)

        if (pdfAssetName != null) {
            displayPdfFromAssets(pdfAssetName!!)
        } else {
            Toast.makeText(this, "PDF dosyası bulunamadı.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    // ... (rest of your PdfViewActivity code remains the same)

    private fun displayPdfFromAssets(assetName: String) {
        progressBar.visibility = View.VISIBLE
        pdfView.fromAsset(assetName)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableAnnotationRendering(false)
            .onLoad(this)
            .onError(this)
            .onPageError(this)
            .enableAntialiasing(true)
            .spacing(10)
            .load()
    }

    override fun loadComplete(nbPages: Int) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "PDF Yüklendi ($nbPages sayfa)", Toast.LENGTH_SHORT).show()
    }

    override fun onError(t: Throwable?) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Hata: " + t?.message, Toast.LENGTH_LONG).show()
        t?.printStackTrace()
        finish()
    }

    override fun onPageError(page: Int, t: Throwable?) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Sayfa yüklenirken hata: $page - " + t?.message, Toast.LENGTH_LONG).show()
        t?.printStackTrace()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}