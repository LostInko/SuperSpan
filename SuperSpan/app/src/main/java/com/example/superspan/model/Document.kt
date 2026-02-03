package com.example.superspan.model

import android.net.Uri

enum class TipoFile {CV, Video}

data class Document(
    var fileTitle : String,     // Usato per indicare cosa mostrare nel box
    var fileName: String,       // Nome del file selezionato
    var fileUri: Uri? = null,
    var tipo : TipoFile? = TipoFile.CV
)