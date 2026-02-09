package com.example.superspan.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.DocumentsAdapter
import com.example.superspan.adapter.QuestionAdapter
import com.example.superspan.adapter.QuestionCheckAdapter
import com.example.superspan.model.Application
import com.example.superspan.model.Document
import com.example.superspan.model.Question
import com.example.superspan.model.TipoDomanda
import com.example.superspan.model.TipoFile
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.ui.fragment.ApplicationGlobal.docs_list
import com.example.superspan.ui.fragment.ApplicationGlobal.question_list
import java.io.File

object ApplicationGlobal {
    val application_list = mutableListOf<Application>()
    val question_list = mutableListOf<Question>()
    val docs_list = mutableListOf<Document>()
}

class ApplicationFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_USER_ID = "arg_user_id"
        private const val ARG_JOB_OFFER = "arg_job_offer"
        private const val ARG_RISPOSTE = "arg_risposte"

        fun newInstance(
            name: String,
            userId: String,
            offerId: Int,
            risposte: String
        ): ApplicationFragment {
            return ApplicationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putString(ARG_USER_ID, userId)
                    putInt(ARG_JOB_OFFER, offerId)
                    putString(ARG_RISPOSTE, risposte)
                }
            }
        }
    }

    // Variabili recuperate dagli argomenti
    private val originalOfferName: String by lazy { arguments?.getString(ARG_NAME).orEmpty() }
    private val applicationUserId: String by lazy { arguments?.getString(ARG_USER_ID).orEmpty() }
    private val applicationOfferId: Int by lazy { arguments?.getInt(ARG_JOB_OFFER) ?: -2 }

    private lateinit var adapterSummary: QuestionCheckAdapter
    private var tempVideoUri: Uri? = null

    // Launcher caricamento CV
    private val cvLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            updateDocument(uri, TipoFile.CV)
            Toast.makeText(requireContext(), "CV caricato", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher caricamento Video Galleria
    private val videoGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            updateDocument(uri, TipoFile.Video)
            Toast.makeText(requireContext(), "Video Caricato", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher caricamento Video Camera
    private val takeVideoLauncher = registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success && tempVideoUri != null) {
            updateDocument(tempVideoUri!!, TipoFile.Video, isCamera = true)
            Toast.makeText(requireContext(), "Video caricato!", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Permesso fotocamera necessario", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var adapterQuestionsStep1: QuestionAdapter
    private lateinit var adapterQuestionsStep2: QuestionAdapter
    private lateinit var adapterFileCV: DocumentsAdapter
    private lateinit var adapterFileVideo: DocumentsAdapter

    private lateinit var btnStepAvanti: Button
    private lateinit var cbPrivacy: CheckBox
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var progressBar: ProgressBar
    private lateinit var tvOfferTitle: TextView
    private lateinit var btnBackTop: AppCompatImageView

    // Liste di supporto per la validazione
    private lateinit var listStep1: List<Question>
    private lateinit var listStep2: List<Question>
    private lateinit var listCV: MutableList<Document>
    private lateinit var listVideo: MutableList<Document>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            setupInitialData()
        }

        // Binding elementi View
        viewFlipper = view.findViewById(R.id.viewFlipper)
        btnStepAvanti = view.findViewById(R.id.btnStepAvanti)
        cbPrivacy = view.findViewById(R.id.cbPrivacy)
        progressBar = view.findViewById(R.id.progressBar)
        tvOfferTitle = view.findViewById(R.id.offerTitle)
        btnBackTop = view.findViewById(R.id.btnBackTop)
        val rvSummary = view.findViewById<RecyclerView>(R.id.rvSummary)

        // Setup Recycler Views

        // Step 1 - Dati Personali
        listStep1 = question_list.take(4)
        val rvStep1 = view.findViewById<RecyclerView>(R.id.rvStep1)
        rvStep1.layoutManager = LinearLayoutManager(requireContext())
        adapterQuestionsStep1 = QuestionAdapter(listStep1) { validateCurrentStep() }
        rvStep1.adapter = adapterQuestionsStep1

        // Step 2 - Esperienza
        listStep2 = question_list.drop(4)
        val rvStep2Questions = view.findViewById<RecyclerView>(R.id.rvStep2Questions)
        rvStep2Questions.isNestedScrollingEnabled = false
        rvStep2Questions.layoutManager = LinearLayoutManager(requireContext())
        adapterQuestionsStep2 = QuestionAdapter(listStep2) { validateCurrentStep() }
        rvStep2Questions.adapter = adapterQuestionsStep2

        // Step 2 - CV
        listCV = docs_list.filter { it.tipo == TipoFile.CV }.toMutableList()
        val rvStep2File = view.findViewById<RecyclerView>(R.id.rvStep2File)
        rvStep2File.isNestedScrollingEnabled = false
        rvStep2File.layoutManager = LinearLayoutManager(requireContext())
        adapterFileCV = DocumentsAdapter(listCV) { _ ->
            cvLauncher.launch("application/pdf")
        }
        rvStep2File.adapter = adapterFileCV

        // Step 3 - Video
        listVideo = docs_list.filter { it.tipo == TipoFile.Video }.toMutableList()
        val rvStep3Video = view.findViewById<RecyclerView>(R.id.rvStep3Video)
        rvStep3Video.layoutManager = LinearLayoutManager(requireContext())
        adapterFileVideo = DocumentsAdapter(listVideo) { _ ->
            showVideoOptionsDialog()
        }
        rvStep3Video.adapter = adapterFileVideo

        // Step 4 - Riepilogo
        rvSummary.layoutManager = LinearLayoutManager(requireContext())
        adapterSummary = QuestionCheckAdapter(emptyList())
        rvSummary.adapter = adapterSummary


        // Pulsanti

        // Tasto Avanti
        btnStepAvanti.setOnClickListener {
            val currentStep = viewFlipper.displayedChild
            val totalSteps = viewFlipper.childCount

            if (currentStep == totalSteps - 1) {
                // Siamo all'ultimo step -> Invia
                submitApplication()

            } else {
                // Andiamo avanti
                viewFlipper.setInAnimation(requireContext(), R.anim.slide_in_right)
                viewFlipper.setOutAnimation(requireContext(), R.anim.slide_out_left)
                viewFlipper.showNext()

                if (viewFlipper.displayedChild == totalSteps - 1) {
                    populateSummary()
                }

                updateUiForStep()
            }
        }

        // Tasto Indietro
        btnBackTop.setOnClickListener {
            handleBackNavigation()
        }

        // Privacy Checkbox
        cbPrivacy.setOnCheckedChangeListener { _, _ ->
            validateCurrentStep()
        }

        // Setup iniziale UI
        updateUiForStep()
    }

    // Gestione logica del tasto indietro
    private fun handleBackNavigation() {
        val currentStep = viewFlipper.displayedChild

        if (currentStep == 0) {
            // Step 1: chiediamo conferma per uscire
            val customView = layoutInflater.inflate(R.layout.dialog3, null)
            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(customView)
                .create()

            customView.findViewById<Button>(R.id.btn_annulla).setOnClickListener {
                dialog.dismiss() // Chiude il dialog e resta lì
            }

            customView.findViewById<Button>(R.id.btn_conferma).setOnClickListener {
                dialog.dismiss()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            dialog.show()
        } else {
            // Step > 1: torniamo allo step precedente
            viewFlipper.setInAnimation(requireContext(), android.R.anim.slide_in_left)
            viewFlipper.setOutAnimation(requireContext(), android.R.anim.slide_out_right)
            viewFlipper.showPrevious()
            updateUiForStep()
        }
    }

    // Aggiorna Titolo, Progress Bar e abilita/disabilita bottone
    private fun updateUiForStep() {
        val currentStep = viewFlipper.displayedChild
        val totalSteps = viewFlipper.childCount

        // 1. Titolo Dinamico
        tvOfferTitle.text = "$originalOfferName - Step ${currentStep + 1}"

        // 2. Progress Bar
        val progress = ((currentStep + 1).toFloat() / totalSteps.toFloat() * 100).toInt()
        progressBar.progress = progress

        // 3. Testo Bottone
        if (currentStep == totalSteps - 1) {
            btnStepAvanti.text = "Invia"
        } else {
            btnStepAvanti.text = "Avanti"
        }

        // 4. Controlla validazione
        validateCurrentStep()
    }

    // Controlla se abilitare il tasto Avanti
    private fun validateCurrentStep() {
        val currentStep = viewFlipper.displayedChild
        var isValid = false

        when (currentStep) {
            0 -> { // Step 1: Dati Personali
                isValid = listStep1.none { it.answer.isBlank() || it.hasError}
            }
            1 -> { // Step 2: Esperienza + CV
                val questionsOk = listStep2.none { it.answer.isBlank() }
                val cvOk = listCV.firstOrNull()?.fileName?.isNotBlank() == true
                isValid = questionsOk && cvOk
            }
            2 -> { // Step 3: Video
                isValid = listVideo.firstOrNull()?.fileName?.isNotBlank() == true
            }
            3 -> { // Step 4: Privacy
                isValid = cbPrivacy.isChecked
            }
        }

        btnStepAvanti.isEnabled = isValid
    }

    private fun populateSummary() {
        val summaryList = mutableListOf<Question>()
        summaryList.addAll(question_list)
        for (file in docs_list) {
            summaryList.add(
                Question(title = file.fileTitle, answer = file.fileName, options = null, tipo = TipoDomanda.Aperta)
            )
        }
        adapterSummary.updateList(summaryList)
    }

    private fun setupInitialData() {
        question_list.clear()
        val currentUser = GlobalData.currentUser
        question_list.add(Question("Nome", currentUser?.name ?: "", null, tipo = TipoDomanda.Aperta))
        question_list.add(Question("Cognome", currentUser?.surname ?: "", null, tipo = TipoDomanda.Aperta))
        question_list.add(Question("Città", currentUser?.citta ?: "", null, tipo = TipoDomanda.Aperta))
        question_list.add(Question("Numero di Telefono", "", null, tipo = TipoDomanda.Numero))

        question_list.add(Question("Anni di esperienza lavorativa:", "", listOf("Nessuno", "1 - 2", "3 o più"), tipo = TipoDomanda.Chiusa))
        question_list.add(Question("Descrivi la tua ultima esperienza lavorativa:", "", null, tipo = TipoDomanda.Aperta))
        question_list.add(Question("Quali sono i tuoi punti di forza per questa posizione?", "", null, tipo = TipoDomanda.Aperta))

        docs_list.clear()
        tempVideoUri = null
        docs_list.add(Document(fileTitle = "Allega il tuo curriculum", fileName = "", tipo = TipoFile.CV))
        docs_list.add(Document(fileTitle = "Carica il tuo video presentazione", fileName = "", tipo = TipoFile.Video))
    }

    private fun updateDocument(uri: Uri, type: TipoFile, isCamera: Boolean = false) {
        val fileName = if (isCamera) {
            "Video_Presentazione.mp4"
        } else {
            getFileNameFromUri(requireContext(), uri) ?: "file_caricato"
        }

        val globalItem = docs_list.find { it.tipo == type }
        globalItem?.fileName = fileName
        globalItem?.fileUri = uri

        if (type == TipoFile.CV) {
            adapterFileCV.notifyItemChanged(0)
        } else {
            adapterFileVideo.notifyItemChanged(0)
        }
        validateCurrentStep() // Rivalida il bottone dopo il caricamento
    }

    private fun submitApplication() {
        if (applicationOfferId == -1) return

        // Usa l'ID passato negli argomenti, se vuoto usa quello globale
        val currentUserId = if (applicationUserId.isNotBlank()) applicationUserId else (GlobalData.currentUser?.username ?: "Guest")

        val answers = question_list.map { it.answer }
        val stringaRisposte = answers.joinToString("###")
        val fileNames = docs_list.map { it.fileName }
        val stringaFiles = fileNames.joinToString("&&&")

        val newApplication = Application(
            name = originalOfferName,
            userId = currentUserId,
            offerId = applicationOfferId,
            risposte = stringaRisposte,
            files = stringaFiles
        )

        ApplicationGlobal.application_list.add(newApplication)

        Toast.makeText(context, "Candidatura inviata con successo!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    // Metodi per Video e File
    private fun showVideoOptionsDialog() {
        val options = arrayOf("Registra Video", "Scegli dalla Galleria")
        AlertDialog.Builder(requireContext())
            .setTitle("Carica Video")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> videoGalleryLauncher.launch("video/*")
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        tempVideoUri = createTempVideoUri()
        takeVideoLauncher.launch(tempVideoUri)
    }

    private fun createTempVideoUri(): Uri {
        val tempFile = File.createTempFile("video_${System.currentTimeMillis()}", ".mp4", requireContext().externalCacheDir)
        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", tempFile)
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) result = it.getString(nameIndex)
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        if (result == null) result = uri.lastPathSegment
        return result ?: "File_Sconosciuto"
    }
}