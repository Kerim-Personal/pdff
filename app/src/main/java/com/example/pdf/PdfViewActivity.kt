package com.example.pdf

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar // Import Snackbar
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.res.ColorStateList
import androidx.core.graphics.toColorInt
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import androidx.constraintlayout.widget.ConstraintLayout // Import ConstraintLayout

class PdfViewActivity : AppCompatActivity(), OnLoadCompleteListener, OnErrorListener, OnPageErrorListener {

    private lateinit var pdfView: PDFView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAiChat: FloatingActionButton
    private lateinit var fabReadingMode: FloatingActionButton
    private lateinit var eyeComfortOverlay: View
    private lateinit var rootLayout: ConstraintLayout // Reference to the root ConstraintLayout

    // Drawing related views and state
    private lateinit var drawingView: DrawingView
    private lateinit var fabToggleDrawing: FloatingActionButton
    private lateinit var fabEraser: FloatingActionButton
    private lateinit var drawingOptionsPanel: LinearLayout // Drawing options panel
    private lateinit var colorOptions: LinearLayout
    private lateinit var sizeOptions: LinearLayout
    private lateinit var clearAllButtonContainer: LinearLayout // Container for the clear all button

    private lateinit var btnColorRed: ImageButton
    private lateinit var btnColorBlue: ImageButton
    private lateinit var btnColorBlack: ImageButton

    private lateinit var btnSizeSmall: ImageButton
    private lateinit var btnSizeMedium: ImageButton
    private lateinit var btnSizeLarge: ImageButton

    private lateinit var fabClearAll: FloatingActionButton // Clear all button

    private var isDrawingActive: Boolean = false // Track if drawing mode is active (pencil or eraser)
    private var currentPenColor: Int = Color.RED // Default pen color
    private var currentPenSize: Float = 10f // Default pen size
    private var currentEraserSize: Float = 50f // Default eraser size

    private var pdfAssetName: String? = null
    private var fullPdfText: String? = null

    private var currentReadingModeLevel: Int = 0 // 0: off, 1: low, 2: medium, 3: high

    private val generativeModel by lazy {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty()) {
            Log.e("GeminiAI", "API Anahtarı BuildConfig içerisinde bulunamadı veya geçersiz. Lütfen local.properties dosyasını ve build.gradle.kts yapılandırmasını kontrol edin.")
            showSnackbar(getString(R.string.ai_assistant_api_key_not_configured))
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

        rootLayout = findViewById(R.id.root_layout_pdf_view) // Initialize rootLayout

        pdfAssetName = intent.getStringExtra(EXTRA_PDF_ASSET_NAME)
        val pdfTitle = intent.getStringExtra(EXTRA_PDF_TITLE) ?: getString(R.string.app_name)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = pdfTitle

        pdfView = findViewById(R.id.pdfView)
        progressBar = findViewById(R.id.progressBarPdf)
        fabAiChat = findViewById(R.id.fab_ai_chat)
        fabReadingMode = findViewById(R.id.fab_reading_mode)
        eyeComfortOverlay = findViewById(R.id.eyeComfortOverlay)

        // Initialize drawing views
        drawingView = findViewById(R.id.drawingView)
        fabToggleDrawing = findViewById(R.id.fab_toggle_drawing)
        fabEraser = findViewById(R.id.fab_eraser)
        drawingOptionsPanel = findViewById(R.id.drawingOptionsPanel)
        colorOptions = findViewById(R.id.colorOptions)
        sizeOptions = findViewById(R.id.sizeOptions)
        clearAllButtonContainer = findViewById(R.id.clearAllButtonContainer)

        btnColorRed = findViewById(R.id.btn_color_red)
        btnColorBlue = findViewById(R.id.btn_color_blue)
        btnColorBlack = findViewById(R.id.btn_color_black)

        btnSizeSmall = findViewById(R.id.btn_size_small)
        btnSizeMedium = findViewById(R.id.btn_size_medium)
        btnSizeLarge = findViewById(R.id.btn_size_large)

        fabClearAll = findViewById(R.id.fab_clear_all)


        if (pdfAssetName != null) {
            displayPdfFromAssets(pdfAssetName!!)
        } else {
            showSnackbar(getString(R.string.pdf_not_found))
            finish()
        }

        fabAiChat.setOnClickListener {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty()) {
                showSnackbar(getString(R.string.ai_feature_api_key_not_set))
                return@setOnClickListener
            }
            if (fullPdfText != null) {
                showAiChatDialog()
            } else {
                showSnackbar(getString(R.string.pdf_text_not_ready))
            }
        }

        fabReadingMode.setOnClickListener {
            toggleReadingMode()
            UIFeedbackHelper.provideFeedback(it)
        }

        // Set up drawing control listeners
        fabToggleDrawing.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            toggleDrawingMode()
        }

        fabEraser.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            activateEraserMode()
        }

        // Set up new Clear All button listener
        fabClearAll.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            drawingView.clearDrawing()
            showSnackbar(getString(R.string.all_drawings_cleared_toast))
        }


        // Initially hide drawing overlay and set drawing mode to NONE
        drawingView.drawingMode = DrawingView.DrawingMode.NONE
        setDrawingButtonState(false) // Initially show pencil as inactive

        // Setup initial selected states and listeners for drawing options
        setupDrawingOptions()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setupDrawingOptions() {
        // Load saved preferences or use defaults for initial color and size
        currentPenColor = SharedPreferencesManager.getPenColor(this)
        currentPenSize = when (SharedPreferencesManager.getPenSizeType(this)) {
            DrawingModeType.SMALL.ordinal -> 5f
            DrawingModeType.MEDIUM.ordinal -> 10f
            DrawingModeType.LARGE.ordinal -> 20f
            else -> 10f // Default to medium
        }
        currentEraserSize = when (SharedPreferencesManager.getEraserSizeType(this)) {
            DrawingModeType.SMALL.ordinal -> 25f // Smaller eraser size
            DrawingModeType.MEDIUM.ordinal -> 50f // Medium eraser size (default)
            DrawingModeType.LARGE.ordinal -> 75f // Larger eraser size
            else -> 50f // Default to medium
        }

        // Set initial actual brush color/size based on loaded preferences
        drawingView.setBrushColor(currentPenColor)
        //drawingView.setBrushSize(currentPenSize) // Only set when tool is active

        // Initialize default selected states for pen color UI
        when (currentPenColor) {
            ContextCompat.getColor(this, R.color.red) -> updateColorSelection(btnColorRed)
            ContextCompat.getColor(this, R.color.blue) -> updateColorSelection(btnColorBlue)
            ContextCompat.getColor(this, R.color.black) -> updateColorSelection(btnColorBlack)
            else -> updateColorSelection(btnColorRed) // Default to red if saved color is unknown
        }

        // Initialize default selected states for size UI (initially based on pen size)
        when (SharedPreferencesManager.getPenSizeType(this)) {
            DrawingModeType.SMALL.ordinal -> updateSizeSelection(btnSizeSmall)
            DrawingModeType.MEDIUM.ordinal -> updateSizeSelection(btnSizeMedium)
            DrawingModeType.LARGE.ordinal -> updateSizeSelection(btnSizeLarge)
            else -> updateSizeSelection(btnSizeMedium) // Default to medium
        }

        // Color listeners
        btnColorRed.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            currentPenColor = ContextCompat.getColor(this, R.color.red)
            drawingView.setBrushColor(currentPenColor)
            SharedPreferencesManager.savePenColor(this, currentPenColor) // Save selected color
            updateColorSelection(it)
        }
        btnColorBlue.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            currentPenColor = ContextCompat.getColor(this, R.color.blue)
            drawingView.setBrushColor(currentPenColor)
            SharedPreferencesManager.savePenColor(this, currentPenColor) // Save selected color
            updateColorSelection(it)
        }
        btnColorBlack.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            currentPenColor = ContextCompat.getColor(this, R.color.black)
            drawingView.setBrushColor(currentPenColor)
            SharedPreferencesManager.savePenColor(this, currentPenColor) // Save selected color
            updateColorSelection(it)
        }

        // Size listeners
        btnSizeSmall.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            setBrushOrEraserSize(5f, DrawingModeType.SMALL) // Small size for pen
            updateSizeSelection(it)
        }
        btnSizeMedium.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            setBrushOrEraserSize(10f, DrawingModeType.MEDIUM) // Medium size for pen
            updateSizeSelection(it)
        }
        btnSizeLarge.setOnClickListener {
            UIFeedbackHelper.provideFeedback(it)
            setBrushOrEraserSize(20f, DrawingModeType.LARGE) // Large size for pen
            updateSizeSelection(it)
        }
    }

    private fun setBrushOrEraserSize(size: Float, type: DrawingModeType) {
        if (drawingView.drawingMode == DrawingView.DrawingMode.PEN) {
            currentPenSize = size
            drawingView.setBrushSize(currentPenSize)
            SharedPreferencesManager.savePenSizeType(this, type.ordinal) // Save pen size type
        } else if (drawingView.drawingMode == DrawingView.DrawingMode.ERASER) {
            currentEraserSize = size
            drawingView.setBrushSize(currentEraserSize) // DrawingView uses setBrushSize for eraser width too
            SharedPreferencesManager.saveEraserSizeType(this, type.ordinal) // Save eraser size type
        }
    }

    private fun updateColorSelection(selectedView: View) {
        // Deselect all color buttons
        btnColorRed.isSelected = false
        btnColorBlue.isSelected = false
        btnColorBlack.isSelected = false
        // Select the chosen one
        selectedView.isSelected = true
    }

    private fun updateSizeSelection(selectedView: View) {
        // Deselect all size buttons
        btnSizeSmall.isSelected = false
        btnSizeMedium.isSelected = false
        btnSizeLarge.isSelected = false
        // Select the chosen one
        selectedView.isSelected = true
    }

    private fun toggleReadingMode() {
        currentReadingModeLevel = (currentReadingModeLevel + 1) % 4
        applyReadingModeFilter(currentReadingModeLevel)
    }

    private fun applyReadingModeFilter(level: Int) {
        when (level) {
            0 -> { // Off
                eyeComfortOverlay.visibility = View.GONE
                eyeComfortOverlay.setBackgroundColor(Color.TRANSPARENT)
                showSnackbar(getString(R.string.reading_mode_off_toast))
            }
            1 -> { // Low filter (e.g., light sepia/yellow tint)
                eyeComfortOverlay.visibility = View.VISIBLE
                eyeComfortOverlay.setBackgroundColor("#33FDF6E3".toColorInt())
                showSnackbar(getString(R.string.reading_mode_low_toast))
            }
            2 -> { // Medium filter
                eyeComfortOverlay.visibility = View.VISIBLE
                eyeComfortOverlay.setBackgroundColor("#66FDF6E3".toColorInt())
                showSnackbar(getString(R.string.reading_mode_medium_toast))
            }
            3 -> { // High filter
                eyeComfortOverlay.visibility = View.VISIBLE
                eyeComfortOverlay.setBackgroundColor("#99FDF6E3".toColorInt())
                showSnackbar(getString(R.string.reading_mode_high_toast))
            }
        }
    }

    private fun toggleDrawingMode() {
        if (isDrawingActive && drawingView.drawingMode == DrawingView.DrawingMode.PEN) {
            // If pen is already active, turn off drawing and hide panel
            drawingView.drawingMode = DrawingView.DrawingMode.NONE
            isDrawingActive = false
            drawingOptionsPanel.visibility = View.GONE
            clearAllButtonContainer.visibility = View.GONE // Hide clear all button
            showSnackbar(getString(R.string.drawing_mode_off_toast))
        } else {
            // Activate pen mode and show options panel
            drawingView.drawingMode = DrawingView.DrawingMode.PEN
            isDrawingActive = true
            drawingView.setBrushColor(currentPenColor) // Set stored pen color
            drawingView.setBrushSize(currentPenSize) // Set stored pen size
            drawingOptionsPanel.visibility = View.VISIBLE
            colorOptions.visibility = View.VISIBLE // Show color options for pen
            sizeOptions.visibility = View.VISIBLE // Show size options for pen
            clearAllButtonContainer.visibility = View.VISIBLE // Show clear all button

            // Restore previously selected pen size UI
            when (SharedPreferencesManager.getPenSizeType(this)) {
                DrawingModeType.SMALL.ordinal -> updateSizeSelection(btnSizeSmall)
                DrawingModeType.MEDIUM.ordinal -> updateSizeSelection(btnSizeMedium)
                DrawingModeType.LARGE.ordinal -> updateSizeSelection(btnSizeLarge)
                else -> updateSizeSelection(btnSizeMedium) // Default to medium
            }

            // Restore previously selected color UI
            val savedColor = SharedPreferencesManager.getPenColor(this)
            when (savedColor) {
                ContextCompat.getColor(this, R.color.red) -> updateColorSelection(btnColorRed)
                ContextCompat.getColor(this, R.color.blue) -> updateColorSelection(btnColorBlue)
                ContextCompat.getColor(this, R.color.black) -> updateColorSelection(btnColorBlack)
                else -> updateColorSelection(btnColorRed) // Default if no color saved
            }

            showSnackbar(getString(R.string.drawing_mode_pencil_toast))
        }
        setDrawingButtonState(isDrawingActive)
    }

    private fun activateEraserMode() {
        if (isDrawingActive && drawingView.drawingMode == DrawingView.DrawingMode.ERASER) {
            // If eraser is already active, turn off drawing and hide panel
            drawingView.drawingMode = DrawingView.DrawingMode.NONE
            isDrawingActive = false
            drawingOptionsPanel.visibility = View.GONE
            clearAllButtonContainer.visibility = View.GONE // Hide clear all button
            showSnackbar(getString(R.string.drawing_mode_off_toast))
        } else {
            // Activate eraser mode and show options panel
            drawingView.drawingMode = DrawingView.DrawingMode.ERASER
            isDrawingActive = true
            drawingView.setBrushSize(currentEraserSize) // Set stored eraser size
            drawingOptionsPanel.visibility = View.VISIBLE
            colorOptions.visibility = View.GONE // Hide color options for eraser
            sizeOptions.visibility = View.VISIBLE // Show size options for eraser
            clearAllButtonContainer.visibility = View.VISIBLE // Show clear all button

            // Restore previously selected eraser size UI
            when (SharedPreferencesManager.getEraserSizeType(this)) {
                DrawingModeType.SMALL.ordinal -> updateSizeSelection(btnSizeSmall)
                DrawingModeType.MEDIUM.ordinal -> updateSizeSelection(btnSizeMedium)
                DrawingModeType.LARGE.ordinal -> updateSizeSelection(btnSizeLarge)
                else -> updateSizeSelection(btnSizeMedium) // Default to medium
            }
            showSnackbar(getString(R.string.drawing_mode_eraser_toast))
        }
        setDrawingButtonState(isDrawingActive)
    }

    // Helper function to update the visual state of the drawing buttons
    private fun setDrawingButtonState(active: Boolean) {
        val activeColor = ContextCompat.getColorStateList(this, R.color.serene_text_light)
        val inactiveColor = ContextCompat.getColorStateList(this, R.color.serene_teal_accent)

        if (active) {
            // Highlight the active tool, dim the inactive one
            if (drawingView.drawingMode == DrawingView.DrawingMode.PEN) {
                fabToggleDrawing.imageTintList = activeColor
                fabEraser.imageTintList = inactiveColor
            } else if (drawingView.drawingMode == DrawingView.DrawingMode.ERASER) {
                fabToggleDrawing.imageTintList = inactiveColor
                fabEraser.imageTintList = activeColor
            }
        } else {
            // Dim both when no drawing tool is active
            fabToggleDrawing.imageTintList = inactiveColor
            fabEraser.imageTintList = inactiveColor
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
            showSnackbar(getString(R.string.pdf_load_failed_with_error, e.localizedMessage ?: "Bilinmeyen hata"))
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

        val dialog = builder.create()

        buttonSend.setOnClickListener {
            val question = editTextQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                textViewAnswer.text = ""
                progressChat.visibility = View.VISIBLE
                buttonSend.isEnabled = false
                editTextQuestion.isEnabled = false

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

                        val responseFlow = generativeModel.generateContentStream(prompt)

                        responseFlow.collect { chunk ->
                            textViewAnswer.append(chunk.text)
                        }

                    } catch (e: Exception) {
                        textViewAnswer.text = getString(R.string.ai_chat_error) + "\n\n" + e.localizedMessage
                        Log.e("GeminiError", "AI Hatası: ", e)
                    } finally {
                        progressChat.visibility = View.GONE
                        buttonSend.isEnabled = true
                        editTextQuestion.isEnabled = true
                    }
                }
            } else {
                showSnackbar(getString(R.string.please_enter_a_question))
            }
        }
        dialog.show()
    }

    @Suppress("unused") // Suppress if this warning persists and you know it's used
    private fun extractTextFromPdf(assetName: String) {
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
                            if (text.isNotBlank() && apiKey.isNotEmpty()) {
                                val fadeInAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                                fabAiChat.startAnimation(fadeInAnimation)
                                fabAiChat.visibility = View.VISIBLE
                            } else if (text.isBlank()) {
                                showSnackbar(getString(R.string.pdf_text_blank_or_empty))
                            }
                            Log.d("PdfTextExtraction", "Metin başarıyla çıkarıldı. Karakter sayısı: ${text.length}")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showSnackbar(getString(R.string.pdf_text_extraction_failed, e.localizedMessage ?: "Bilinmeyen hata"))
                    Log.e("PdfTextExtraction", "Hata: ", e)
                }
            }
        }
    }

    override fun loadComplete(nbPages: Int) {
        progressBar.visibility = View.GONE
        showSnackbar(getString(R.string.pdf_loaded_toast, nbPages))
        // Extract text after PDF loads to enable AI chat
        pdfAssetName?.let {
            if (fullPdfText == null) {
                extractTextFromPdf(it)
            }
        }
    }

    override fun onError(t: Throwable?) {
        progressBar.visibility = View.GONE
        showSnackbar(getString(R.string.error_toast, t?.localizedMessage ?: "Bilinmeyen PDF hatası"))
        Log.e("PdfView_onError", "PDF Yükleme Hatası", t)
        finish()
    }

    override fun onPageError(page: Int, t: Throwable?) {
        showSnackbar(getString(R.string.page_load_error_toast, page, t?.localizedMessage ?: "Bilinmeyen sayfa hatası"))
        Log.e("PdfView_onPageError", "Sayfa Yükleme Hatası: $page", t)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Helper function to show Snackbar
    private fun showSnackbar(message: String) {
        // Snackbar'ı Activity'nin root view'ına bağlamak, konumlandırma ve tuşları engelleme konusunda Material Design'ın önerdiği yöntemdir.
        // Snackbar varsayılan olarak ekranın altından yükselir ve tuşların üzerini kaplar ancak tuşları itmez.
        // LENGTH_SHORT süresi, mesajın kısa kalmasını sağlar.
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}

// DrawingModeType enum (defined outside the class for broader access)
enum class DrawingModeType {
    SMALL, MEDIUM, LARGE
}