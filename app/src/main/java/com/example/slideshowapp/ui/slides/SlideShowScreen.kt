package com.example.slideshowapp.ui.slides

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.slideshowapp.data.model.Slide
import com.example.slideshowapp.data.model.SlideCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlideShowScreen(
    isAdmin: Boolean,
    viewModel: SlidesViewModel = hiltViewModel()
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val slides by viewModel.slides.collectAsState()
    val pagerState = rememberPagerState(pageCount = { slides.size })
    
    var showAddEditDialog by remember { mutableStateOf(false) }
    var slideToEdit by remember { mutableStateOf<Slide?>(null) }
    
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Categorías
        TabRow(
            selectedTabIndex = if (selectedCategory == SlideCategory.HOME_PRODUCTS) 0 else 1
        ) {
            Tab(
                selected = selectedCategory == SlideCategory.HOME_PRODUCTS,
                onClick = { viewModel.setCategory(SlideCategory.HOME_PRODUCTS) }
            ) {
                Text("Productos Hogar", modifier = Modifier.padding(16.dp))
            }
            Tab(
                selected = selectedCategory == SlideCategory.POSTPAID_PRODUCTS,
                onClick = { viewModel.setCategory(SlideCategory.POSTPAID_PRODUCTS) }
            ) {
                Text("Productos Pospago", modifier = Modifier.padding(16.dp))
            }
        }

        // Contenido
        if (slides.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay diapositivas disponibles")
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) { page ->
                val slide = slides[page]
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = slide.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (slide.imageUrl != null) {
                            AsyncImage(
                                model = slide.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Text(
                            text = slide.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        if (slide.documentUrl != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(slide.documentUrl)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("Ver Documento")
                            }
                        }
                        
                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        slideToEdit = slide
                                        showAddEditDialog = true
                                    }
                                ) {
                                    Text("Editar")
                                }
                                IconButton(
                                    onClick = { viewModel.deleteSlide(slide) }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isAdmin) {
            FloatingActionButton(
                onClick = {
                    slideToEdit = null
                    showAddEditDialog = true
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End)
            ) {
                Text("+")
            }
        }
    }
    
    if (showAddEditDialog) {
        AddEditSlideDialog(
            slide = slideToEdit,
            category = selectedCategory,
            onDismiss = { showAddEditDialog = false },
            onSave = { slide ->
                if (slideToEdit != null) {
                    viewModel.updateSlide(slide)
                } else {
                    viewModel.addSlide(slide)
                }
            }
        )
    }
} 