package com.example.focusup.domain.model

import android.net.Uri

data class TaskAttachment(
    val uri: String,
    val fileName: String,
    val fileType: FileType,
    val fileSize: Long = 0 // en bytes
)

enum class FileType(val mimeTypes: List<String>, val displayName: String, val icon: String) {
    PDF(listOf("application/pdf"), "PDF", "📄"),
    WORD(listOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"), "Word", "📝"),
    EXCEL(listOf("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), "Excel", "📊"),
    POWERPOINT(listOf("application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation"), "PowerPoint", "📽️"),
    IMAGE(listOf("image/*"), "Imagen", "🖼️"),
    OTHER(listOf("*/*"), "Archivo", "📎");
    
    companion object {
        fun fromMimeType(mimeType: String): FileType {
            return values().find { fileType ->
                fileType.mimeTypes.any { mime ->
                    if (mime.endsWith("/*")) {
                        mimeType.startsWith(mime.removeSuffix("/*"))
                    } else {
                        mimeType == mime
                    }
                }
            } ?: OTHER
        }
        
        fun fromFileName(fileName: String): FileType {
            val extension = fileName.substringAfterLast('.', "").lowercase()
            return when (extension) {
                "pdf" -> PDF
                "doc", "docx" -> WORD
                "xls", "xlsx" -> EXCEL
                "ppt", "pptx" -> POWERPOINT
                "jpg", "jpeg", "png", "gif", "bmp", "webp" -> IMAGE
                else -> OTHER
            }
        }
        
        fun parseFromString(attachments: String): List<TaskAttachment> {
            if (attachments.isBlank()) return emptyList()
            
            return attachments.split("|||").mapNotNull { item ->
                val parts = item.split("|##|")
                if (parts.size >= 3) {
                    TaskAttachment(
                        uri = parts[0],
                        fileName = parts[1],
                        fileType = fromFileName(parts[1]),
                        fileSize = parts.getOrNull(2)?.toLongOrNull() ?: 0
                    )
                } else null
            }
        }
        
        fun convertToString(attachments: List<TaskAttachment>): String {
            return attachments.joinToString("|||") { attachment ->
                "${attachment.uri}|##|${attachment.fileName}|##|${attachment.fileSize}"
            }
        }
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
