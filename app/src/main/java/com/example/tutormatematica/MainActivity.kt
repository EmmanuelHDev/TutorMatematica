package com.example.tutormatematica

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

  // Variables
  private lateinit var textToSpeechHelper: TextToSpeechHelper
  private lateinit var speechRecognizerHelper: SpeechRecognizerHelper
  private lateinit var editTextTextToSpeech: EditText
  private lateinit var textViewSpeechToText: TextView
  // Valor del codigo de permiso de audio
  private val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 2

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // SplashScreen
    installSplashScreen()
    // Layout main
    setContentView(R.layout.activity_main)
    // Layout voz-texto
    setContentView(R.layout.voz_texto)

    // Inicializar UI
    // Asignar variable EditText Text-to-Speech
    editTextTextToSpeech = findViewById(R.id.editTextTextToSpeech)
    // Asignar variable TextView Speech-to-Text
    textViewSpeechToText = findViewById(R.id.textViewSpeechToText)
    // Definir valor tipo Boton del boton Text-to-Speech
    val btnTextToSpeech: Button = findViewById(R.id.btnTextToSpeech)
    // Definir valor tipo Boton del boton Speech-to-Text
    val btnSpeechToText: Button = findViewById(R.id.btnSpeechToText)

    // Inicializar Text-to-Speech y Speech-to-Text
    textToSpeechHelper = TextToSpeechHelper(this)
    speechRecognizerHelper = SpeechRecognizerHelper(this) { result ->
      textViewSpeechToText.text = result
    }

    // Configurar botones
    // Boton Text-to-Speech
    btnTextToSpeech.setOnClickListener {
      val text = editTextTextToSpeech.text.toString()
      textToSpeechHelper.speak(text)
    }

    // Boton Speech-to-Text
    btnSpeechToText.setOnClickListener {
      if (PermissionHelper.checkAndRequestPermission(
          this, Manifest.permission.RECORD_AUDIO, REQUEST_RECORD_AUDIO_PERMISSION_CODE)) {
        speechRecognizerHelper.startListening()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    textToSpeechHelper.stop()
    speechRecognizerHelper.destroy()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<out String>, grantResults: IntArray
  ) {
    if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_CODE) {
      if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        textViewSpeechToText.text = "Permiso de grabación denegado"
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }
}
