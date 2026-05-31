package com.example.leitorrecibo.domain.extractors

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

fun QRCodeExtractor(context: Context, imageUri: Uri, onResult: (String?) -> Unit) {
    try {
        // 1. Prepara a imagem para o ML Kit
        val image = InputImage.fromFilePath(context, imageUri)

        // 2. Instancia o leitor de código de barras
        val scanner = BarcodeScanning.getClient()

        // 3. Processa a imagem
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Procura especificamente por um QR Code que contenha uma URL
                val qrCodeUrl = barcodes.firstOrNull {
                    it.valueType == Barcode.TYPE_URL
                }?.url?.url

                onResult(qrCodeUrl)
            }
            .addOnFailureListener {
                // Falhou na leitura
                onResult(null)
            }
    } catch (e: Exception) {
        e.printStackTrace()
        onResult(null)
    }
}