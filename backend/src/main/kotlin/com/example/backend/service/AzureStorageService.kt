package com.example.backend.service

import com.azure.storage.blob.BlobClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.util.UUID

@Service
class AzureStorageService(
    @Value("\${azure.storage.connection-string}")
    private val connectionString: String,
    
    @Value("\${azure.storage.container-name}")
    private val containerName: String
) {
    fun uploadImage(file: MultipartFile): String {
        val fileName = "${UUID.randomUUID()}.${file.originalFilename?.substringAfterLast('.', "jpg")}"
        
        val blobClient = BlobClientBuilder()
            .connectionString(connectionString)
            .containerName(containerName)
            .blobName(fileName)
            .buildClient()

        blobClient.upload(file.inputStream, file.size)
        
        return fileName
    }

    fun getImage(fileName: String): ByteArray {
        val blobClient = BlobClientBuilder()
            .connectionString(connectionString)
            .containerName(containerName)
            .blobName(fileName)
            .buildClient()

        val outputStream = ByteArrayOutputStream()
        blobClient.downloadStream(outputStream)
        
        return outputStream.toByteArray()
    }

    fun deleteImage(fileName: String) {
        val blobClient = BlobClientBuilder()
            .connectionString(connectionString)
            .containerName(containerName)
            .blobName(fileName)
            .buildClient()

        blobClient.deleteIfExists()
    }
} 