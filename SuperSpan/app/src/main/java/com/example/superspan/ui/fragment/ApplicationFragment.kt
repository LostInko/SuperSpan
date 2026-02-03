package com.example.superspan.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.example.superspan.model.JobOffer
import com.example.superspan.model.Question
import com.example.superspan.model.TipoDomanda
import com.example.superspan.model.TipoFile
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.ui.fragment.ApplicationGlobal.docs_list
import com.example.superspan.ui.fragment.ApplicationGlobal.question_list
import com.example.superspan.viewmodel.WorkWithUsViewModel
import org.w3c.dom.Text
import java.io.File

object ApplicationGlobal{
    val application_list = mutableListOf<Application>()
    val question_list = mutableListOf<Question>()
    val docs_list = mutableListOf<Document>()
}

class ApplicationFragment : Fragment(){

    companion object {
        private const val ARG_ID = "-1"
        private const val ARG_NAME = "arg_name"
        private const val ARG_USER_ID = "arg_user_id"
        private const val ARG_JOB_OFFER = "arg_job_offer"
        private const val ARG_RISPOSTE = "arg_risposte"

        /**
         * Costruttore consigliato: passa anche l'indice se lo conosci.
         * Se non lo hai, usa -1: il fragment farà fallback per nome.
         */

        fun newInstance(
            name : String,
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

    private val applicationName: String by lazy { arguments?.getString(ApplicationFragment.Companion.ARG_NAME).orEmpty() }
    private val applicationOfferId: Int by lazy { arguments?.getInt(ARG_JOB_OFFER) ?: -2 }

    private lateinit var adapterSummary: QuestionCheckAdapter


    // Variabile per ricordare quale riga stiamo modificando
    private var positionToUpdate: Int = -1
    private var tempVideoUri: Uri? = null


    // Launcher per il Curriculum (.pdf)
    private val cvLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            updateDocument(uri, TipoFile.CV)
        }
    }

    // Launcher per il video (dalla galleria)
    private val videoGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            updateDocument(uri, TipoFile.Video)
        }
    }

    // Launcher per il video (dalla camera)
    private val takeVideoLauncher = registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success && tempVideoUri != null) {
            // Sappiamo per certo che è un Video dalla Camera
            updateDocument(tempVideoUri!!, TipoFile.Video, isCamera = true)
        } else {
            Toast.makeText(requireContext(), "Video non registrato", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Permesso fotocamera necessario", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Adapters per i vari step della candidatura
    private lateinit var adapterQuestionsStep1: QuestionAdapter
    private lateinit var adapterQuestionsStep2: QuestionAdapter
    private lateinit var adapterFileCV: DocumentsAdapter
    private lateinit var adapterFileVideo: DocumentsAdapter


    // References per l'accesso globale a certi elementi
    private lateinit var btnStepAvanti: Button
    private lateinit var cbPrivacy: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_job_applications, container, false)

        view.findViewById<TextView>(R.id.offerTitle)?.text = applicationName

        return(view)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null){
            setupInitialData()
        }

        val viewFlipper = view.findViewById<ViewFlipper>(R.id.viewFlipper)
        btnStepAvanti = view.findViewById<Button>(R.id.btnStepAvanti)
        val btnStepIndietro = view.findViewById<Button>(R.id.btnStepIndietro)
        cbPrivacy = view.findViewById<CheckBox>(R.id.cbPrivacy)

        val rvSummary = view.findViewById<RecyclerView>(R.id.rvSummary)

        // Setup RV

        // Step 1 - Domande Personali
        val listStep1 = question_list.take(3)
        val rvStep1 = view.findViewById<RecyclerView>(R.id.rvStep1)
        rvStep1.layoutManager = LinearLayoutManager(requireContext())
        adapterQuestionsStep1 = QuestionAdapter(listStep1) { }
        rvStep1.adapter = adapterQuestionsStep1


        // Step 2 - Domande di Lavoro
        val listStep2 = question_list.drop(3)
        val rvStep2Questions = view.findViewById<RecyclerView>(R.id.rvStep2Questions)
        rvStep2Questions.isNestedScrollingEnabled = false
        rvStep2Questions.layoutManager = LinearLayoutManager(requireContext())
        adapterQuestionsStep2 = QuestionAdapter(listStep2) { }
        rvStep2Questions.adapter = adapterQuestionsStep2


        // Step 3 - Curriculum
        val listCV = docs_list.filter { it.tipo == TipoFile.CV }.toMutableList()
        val rvStep2File = view.findViewById<RecyclerView>(R.id.rvStep2File)
        rvStep2File.isNestedScrollingEnabled = false
        rvStep2File.layoutManager = LinearLayoutManager(requireContext())
        adapterFileCV = DocumentsAdapter(listCV) { _ ->
            // Nota: qui positionToUpdate è sempre 0 perché la lista ha 1 elemento
            positionToUpdate = 0
            cvLauncher.launch("application/pdf")
        }
        rvStep2File.adapter = adapterFileCV


        // Step 3 - Video Presentazione

        val listVideo = docs_list.filter { it.tipo == TipoFile.Video }.toMutableList()
        val rvStep3Video = view.findViewById<RecyclerView>(R.id.rvStep3Video)
        rvStep3Video.layoutManager = LinearLayoutManager(requireContext())
        adapterFileVideo = DocumentsAdapter(listVideo) { _ ->
            // Click sul Video
            positionToUpdate = 0
            showVideoOptionsDialog()
        }
        rvStep3Video.adapter = adapterFileVideo


        // Step 4 - Riepilogo

        rvSummary.layoutManager = LinearLayoutManager(requireContext())
        adapterSummary = QuestionCheckAdapter(emptyList())
        rvSummary.adapter = adapterSummary


        fun updateButtonsState() {
            val currentChild = viewFlipper.displayedChild
            val childCount = viewFlipper.childCount

            // Gestione bottone Indietro
            if (currentChild == 0) {
                btnStepIndietro.visibility = View.INVISIBLE
            } else {
                btnStepIndietro.visibility = View.VISIBLE
            }

            // Gestione bottone Avanti / Bottom Nav
            if (currentChild == childCount - 1) {
                btnStepAvanti.text = "Invia"
                btnStepAvanti.isEnabled = cbPrivacy.isChecked
            } else {
                btnStepAvanti.text = "Avanti"
                btnStepAvanti.isEnabled = true
            }
        }

        // Init stato iniziale
        updateButtonsState()

        // Logica VALIDAZIONE Step per Step
        btnStepAvanti.setOnClickListener {
            val currentStep = viewFlipper.displayedChild

            val totalSteps = viewFlipper.childCount
            if(currentStep == totalSteps - 1) {
                submitApplication()
                return@setOnClickListener
            }

            var isValid = true
            var errorMsg = ""

            when(currentStep) {
                0 -> { // Step 1: Dati Personali
                    if (listStep1.any { it.answer.isBlank() }) {
                        isValid = false
                        errorMsg = "Rispondi a tutte le domande personali"
                    }
                    for(domanda in listStep1){
                        if (domanda.answer.isBlank()) {
                            domanda.hasError = true
                        }
                    }
                    adapterQuestionsStep1.notifyDataSetChanged()
                }
                1 -> { // Step 2: Lavoro + CV
                    if (listStep2.any { it.answer.isBlank() }) {
                        isValid = false
                        errorMsg = "Rispondi a tutte le domande di lavoro"
                    }
                    else if (listCV[0].fileName.isBlank()) {
                        isValid = false
                        errorMsg = "Allega il curriculum"
                    }
                    for(domanda in listStep2){
                        if (domanda.answer.isBlank()) {
                            domanda.hasError = true
                        }
                    }
                    adapterQuestionsStep2.notifyDataSetChanged()
                }
                2 -> { // Step 3: Video
                    if (listVideo[0].fileName.isBlank()) {
                        isValid = false
                        errorMsg = "Allega il video presentazione"
                    }
                }
            }

            if (isValid) {
                viewFlipper.setInAnimation(requireContext(), R.anim.slide_in_right)
                viewFlipper.setOutAnimation(requireContext(), R.anim.slide_out_left)
                viewFlipper.showNext()

                if (viewFlipper.displayedChild == totalSteps - 1) {
                    populateSummary()
                }

                updateButtonsState()
            } else {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        btnStepIndietro.setOnClickListener {
            viewFlipper.setInAnimation(requireContext(), android.R.anim.slide_in_left)
            viewFlipper.setOutAnimation(requireContext(), android.R.anim.slide_out_right)
            viewFlipper.showPrevious()
            updateButtonsState()
        }

        // Logica Privacy (Step 4)
        cbPrivacy.setOnCheckedChangeListener { _, isChecked ->
            if (viewFlipper.displayedChild == viewFlipper.childCount - 1) {
                btnStepAvanti.isEnabled = isChecked
            }
        }

        // Back button in alto
        view.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            if (viewFlipper.displayedChild > 0) {
                btnStepIndietro.performClick()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

    }

    private fun populateSummary() {

        // Crea la lista combinata per l'adapter
        val summaryList = mutableListOf<Question>()

        // Aggiungi tutte le domande
        summaryList.addAll(question_list)

        // Aggiungi i file convertendoli in oggetti "Question"
        for (file in docs_list) {
            val fileQuestion = Question(
                title = file.fileTitle,
                answer = file.fileName,
                options = null,
                tipo = TipoDomanda.Aperta
            )
            summaryList.add(fileQuestion)
        }

        // Aggiorna l'adapter
        adapterSummary.updateList(summaryList)
    }


    // Funzione che svuota tutti i campi e li ricrea con la "risposta" vuota
    private fun setupInitialData() {
        // Pulizia lista domande
        question_list.clear()

        // Domande 1 - Anagrafica
        val currentUser = GlobalData.currentUser // I dati anagrafici vengono inseriti automaticamente
        question_list.add(Question("Nome", currentUser!!.name, null, tipo = TipoDomanda.Aperta))
        question_list.add(Question("Cognome", currentUser.surname, null, tipo = TipoDomanda.Aperta))
        question_list.add(Question("Città", currentUser.citta, null, tipo = TipoDomanda.Aperta))


        // Domande 2 - Lavoro
        question_list.add(Question("Anni di esperienza lavorativa:", "", listOf("Nessuno", "1 - 2", "3 o più"), tipo = TipoDomanda.Chiusa))
        question_list.add(Question("Descrivi la tua ultima esperienza lavorativa:", "", null, tipo = TipoDomanda.Aperta))
        question_list.add(Question("Quali sono i tuoi punti di forza per questa posizione?", "", null, tipo = TipoDomanda.Aperta))

        // Pulizia lista file
        docs_list.clear()
        tempVideoUri = null
        positionToUpdate = -1

        // Aggiunta slot per curriculum e video
        docs_list.add(Document(fileTitle = "Allega il tuo curriculum", fileName = "", tipo = TipoFile.CV))
        docs_list.add(Document(fileTitle = "Carica il tuo video presentazione", fileName = "", tipo = TipoFile.Video))
    }


    // Invio definitivo della candidatura e salvataggio nella lista
    private fun submitApplication(){

        if (applicationOfferId == -1) {
            Toast.makeText(context, "Errore: Offerta non trovata", Toast.LENGTH_SHORT).show()
            return
        }

        val user = GlobalData.currentUser
        val currentUserId = user?.username ?: "Guest"

        val answers = question_list.map { it.answer }
        val stringaRisposte = answers.joinToString ( "###" )

        val fileNames = docs_list.map { it.fileName}
        val stringaFiles = fileNames.joinToString("&&&")



        val newApplication = Application(
            name = applicationName,
            userId = currentUserId,
            offerId = applicationOfferId,
            risposte = stringaRisposte,
            files = stringaFiles
        )

        ApplicationGlobal.application_list.add(newApplication)

        Toast.makeText(context, "Candidatura Inviata con successo!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()

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
    }

    private fun showVideoOptionsDialog() {
        val options = arrayOf("Registra Video", "Scegli dalla Galleria")

        AlertDialog.Builder(requireContext())
            .setTitle("Carica Video")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen() // Registra
                    1 -> videoGalleryLauncher.launch("video/*") // Galleria
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

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result: String? = null

        if (uri.scheme == "content") {
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            result = it.getString(nameIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (result == null) {
            result = uri.lastPathSegment
        }

        if (result != null) {
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }

        return result ?: "File_Sconosciuto"
    }

}


