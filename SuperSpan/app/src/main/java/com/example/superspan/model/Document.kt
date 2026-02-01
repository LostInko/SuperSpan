package com.example.superspan.model

import android.net.Uri

enum class TipoFile {CV, Video}


data class Document(
    var fileName: String? = null,
    var fileUri: Uri? = null,
    var tipo : TipoFile? = TipoFile.CV
)