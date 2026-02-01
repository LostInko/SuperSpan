package com.example.superspan.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
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

    val question_list = mutableListOf<Question>(
        Question("A", "", listOf("a", "b"), tipo = TipoDomanda.Chiusa),
        Question("B", "", null, tipo = TipoDomanda.Aperta),
        Question("C", "", null, tipo = TipoDomanda.Aperta),
        Question("D", "", listOf("a", "b"), tipo = TipoDomanda.Chiusa)
    )

    val docs_list = mutableListOf<Document>(
        Document(tipo = TipoFile.CV),
        Document(tipo = TipoFile.Video)
    )
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

    private lateinit var vm: WorkWithUsViewModel
    private val applicationName: String by lazy { arguments?.getString(ApplicationFragment.Companion.ARG_NAME).orEmpty() }
    private val applicationOfferId: Int by lazy { arguments?.getInt(ARG_JOB_OFFER) ?: -2 }

    private lateinit var adapter: DocumentsAdapter

    // Variabile per ricordare quale riga stiamo modificando
    private var positionToUpdate: Int = -1
    private var tempVideoUri: Uri? = null // Qui salviamo l'URI prima di aprire la camera

    // 1. IL LAUNCHER (come prima, ma ora aggiorna la lista)
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        handleFileResult(uri)
    }

    private val takeVideoLauncher = registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success && tempVideoUri != null) {
            handleFileResult(tempVideoUri)
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

        val listQuestion = ApplicationGlobal.question_list
        val btnInvia = view.findViewById<ConstraintLayout>(R.id.btnInvia)
        val cbPrivacy = view.findViewById<CheckBox>(R.id.cbPrivacy)

        val rvQuestions : RecyclerView = view.findViewById(R.id.rvQuestions)
        rvQuestions.layoutManager = LinearLayoutManager(requireContext())

        val controlloValidita = {
            val blankAnswer = listQuestion.any() { it.answer.isBlank() }

            if (!blankAnswer && cbPrivacy.isChecked) {
                btnInvia.isEnabled = true;
                btnInvia.alpha = 1f;
            } else {
                btnInvia.isEnabled = false;
                btnInvia.alpha = 0.3f;
                rvQuestions.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.soft_red)
            }
        }



        val rvFiles : RecyclerView = view.findViewById(R.id.rvFiles)

        rvFiles.layoutManager = LinearLayoutManager(requireContext())
        rvFiles.isNestedScrollingEnabled = false

        if (savedInstanceState == null) {
            docs_list.clear()
            tempVideoUri = null
            positionToUpdate = -1
            docs_list.add(Document(tipo = TipoFile.CV))
            docs_list.add(Document(tipo = TipoFile.Video))

            question_list.clear()
            question_list.add(Question("A", "", listOf("a", "b"), tipo = TipoDomanda.Chiusa))
            question_list.add(Question("B", "", null, tipo = TipoDomanda.Aperta))
            question_list.add(Question("C", "", null, tipo = TipoDomanda.Aperta))
            question_list.add(Question("D", "", listOf("a", "b"), tipo = TipoDomanda.Chiusa))

        }

        adapter = DocumentsAdapter(docs_list) { position ->
            // Questa è la callback che viene eseguita quando clicchi la card nell'adapter
            positionToUpdate = position // Memorizzo quale riga ho cliccato
            val item = docs_list[positionToUpdate]
            if(item.tipo == TipoFile.CV) {
                pickFileLauncher.launch("application/pdf") // Apro il picker
            } else {
                showVideoOptionsDialog()
            }

        }

        rvQuestions.adapter = QuestionAdapter(listQuestion, controlloValidita)

        rvFiles.adapter = adapter


        // ---- Back (ID unico presente: btnBackTop) ----
        view.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        cbPrivacy.setOnClickListener { controlloValidita() }

        btnInvia.setOnClickListener {
            val user = GlobalData.currentUser
            val currentUserId = user!!.username
            val currentOfferId = applicationOfferId

            if (applicationOfferId == -1) {
                Toast.makeText(context, "Errore: Offerta non trovata", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val answers = mutableListOf<String>()

            for (domanda in listQuestion) {
                answers.add(domanda.answer)
            }

            val stringaUnica = answers.joinToString ( "###" )

            val newApplication = Application(
                name = applicationName,
                userId = currentUserId,
                offerId = currentOfferId,
                risposte = stringaUnica
            )

            ApplicationGlobal.application_list.add(newApplication)

            Toast.makeText(context, "Candidatura Inviata!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        btnInvia.isEnabled = false;
        btnInvia.alpha = 0.3f;

    }

    private fun showVideoOptionsDialog() {
        val options = arrayOf("Registra Video", "Scegli dalla Galleria")

        AlertDialog.Builder(requireContext())
            .setTitle("Carica Video")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen() // Registra
                    1 -> pickFileLauncher.launch("video/*") // Galleria
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
        // 1. Crea un file temporaneo vuoto dove la camera salverà il video
        tempVideoUri = createTempVideoUri()

        // 2. Lancia la camera passando l'URI
        takeVideoLauncher.launch(tempVideoUri)
    }

    // Funzione Helper per creare l'URI sicuro
    private fun createTempVideoUri(): Uri {
        val tempFile = File.createTempFile("video_${System.currentTimeMillis()}", ".mp4", requireContext().externalCacheDir)

        // ATTENZIONE: "com.example.superspan.provider" deve essere uguale a quello nel Manifest!
        // Solitamente è: context.packageName + ".provider"
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )
    }

    private fun handleFileResult(uri : Uri?) {
        if (uri != null && positionToUpdate != -1) {
            val fileName = getFileNameFromUri(requireContext(), uri)

            docs_list[positionToUpdate].fileName = fileName
            docs_list[positionToUpdate].fileUri = uri

            adapter.notifyItemChanged(positionToUpdate)

            positionToUpdate = -1
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var result: String? = null

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    // 1. Ottieni l'indice della colonna in una variabile
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    // 2. Controlla che l'indice sia valido (quindi >= 0)
                    if (nameIndex >= 0) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result.substring(cut + 1)
            }
        }

        return result
    }

}


