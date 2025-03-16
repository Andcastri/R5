package com.example.slideshowapp.ui.slides

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.slideshowapp.data.model.Slide
import com.example.slideshowapp.data.model.SlideCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSlideDialog(
    slide: Slide? = null,
    category: SlideCategory,
    onDismiss: () -> Unit,
    onSave: (Slide) -> Unit
) {
    var title by remember { mutableStateOf(slide?.title ?: "") }
    var content by remember { mutableStateOf(slide?.content ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(slide?.imageUrl?.let { Uri.parse(it) }) }
    var documentUri by remember { mutableStateOf<Uri?>(slide?.documentUrl?.let { Uri.parse(it) }) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        documentUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (slide == null) "Agregar Diapositiva" else "Editar Diapositiva") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (imageUri != null) "Cambiar Imagen" else "Agregar Imagen")
                }

                if (imageUri != null) {
                    Text("Imagen seleccionada: ${imageUri?.lastPathSegment}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { documentPickerLauncher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (documentUri != null) "Cambiar Documento" else "Agregar Documento")
                }

                if (documentUri != null) {
                    Text("Documento seleccionado: ${documentUri?.lastPathSegment}")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newSlide = Slide(
                        id = slide?.id ?: 0,
                        title = title,
                        content = content,
                        imageUrl = imageUri?.toString(),
                        documentUrl = documentUri?.toString(),
                        category = category,
                        order = slide?.order ?: 0
                    )
                    onSave(newSlide)
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 