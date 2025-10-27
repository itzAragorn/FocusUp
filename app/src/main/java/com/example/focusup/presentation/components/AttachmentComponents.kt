package com.example.focusup.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusup.domain.model.TaskAttachment
import com.example.focusup.domain.model.FileType
import com.example.focusup.domain.model.formatFileSize

@Composable
fun AttachmentSelector(
    attachments: List<TaskAttachment>,
    onAttachmentsChanged: (List<TaskAttachment>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Obtener información del archivo
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            
            var fileName = "archivo"
            var fileSize = 0L
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    
                    if (nameIndex != -1) {
                        fileName = it.getString(nameIndex)
                    }
                    if (sizeIndex != -1) {
                        fileSize = it.getLong(sizeIndex)
                    }
                }
            }
            
            // Tomar persistencia del URI
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Algunos URIs no soportan permisos persistentes
            }
            
            val attachment = TaskAttachment(
                uri = uri.toString(),
                fileName = fileName,
                fileType = FileType.fromFileName(fileName),
                fileSize = fileSize
            )
            
            onAttachmentsChanged(attachments + attachment)
        }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Archivos adjuntos",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            OutlinedButton(
                onClick = { filePickerLauncher.launch("*/*") },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.AttachFile,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adjuntar", fontSize = 13.sp)
            }
        }
        
        if (attachments.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(attachments) { attachment ->
                    AttachmentChip(
                        attachment = attachment,
                        onRemove = {
                            onAttachmentsChanged(attachments - attachment)
                        },
                        onClick = {
                            // Abrir archivo
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(Uri.parse(attachment.uri), getMimeType(attachment))
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Manejar error
                            }
                        }
                    )
                }
            }
        } else {
            Text(
                text = "Sin archivos adjuntos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AttachmentChip(
    attachment: TaskAttachment,
    onRemove: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(160.dp)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = attachment.fileType.icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
                
                if (onRemove != null) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar archivo",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(onClick = onRemove),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = attachment.fileName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = attachment.fileType.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = formatFileSize(attachment.fileSize),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AttachmentsList(
    attachments: List<TaskAttachment>,
    modifier: Modifier = Modifier
) {
    if (attachments.isEmpty()) return
    
    val context = LocalContext.current
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "📎 Archivos (${attachments.size})",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(attachments) { attachment ->
                AttachmentChip(
                    attachment = attachment,
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(Uri.parse(attachment.uri), getMimeType(attachment))
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Manejar error - podría mostrar un Toast
                        }
                    }
                )
            }
        }
    }
}

private fun getMimeType(attachment: TaskAttachment): String {
    return when (attachment.fileType) {
        FileType.PDF -> "application/pdf"
        FileType.WORD -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        FileType.EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        FileType.POWERPOINT -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        FileType.IMAGE -> "image/*"
        FileType.OTHER -> "*/*"
    }
}
