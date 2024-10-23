package com.example.tutormatematica

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
// Importar Widgets
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
// Importar SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

  // Variables
  private lateinit var textToSpeechHelper: TextToSpeechHelper
  private lateinit var speechRecognizerHelper: SpeechRecognizerHelper
  private lateinit var editTextTextToSpeech: EditText
  private lateinit var textViewSpeechToText: TextView
  // Variable para OpenAIService
  private lateinit var openAIService: OpenAIService

  // Valor del código de permiso de audio
  private val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 2

  @SuppressLint("MissingInflatedId")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // SplashScreen
    installSplashScreen()
    // Layout main
    setContentView(R.layout.activity_main)
    //bton para llegar al area de numeros naturales
    val button = findViewById<Button>(R.id.numerosNaturales)
    button.setOnClickListener {
      // Inicia la nueva actividad
      val intent = Intent(this, NumerosNaturales::class.java)
      startActivity(intent)
    }
    // Layout voz-texto
    setContentView(R.layout.voz_texto)

    // Inicializar UI
    editTextTextToSpeech = findViewById(R.id.editTextTextToSpeech)
    textViewSpeechToText = findViewById(R.id.textViewSpeechToText)
    val btnTextToSpeech: Button = findViewById(R.id.btnTextToSpeech)
    val btnSpeechToText: Button = findViewById(R.id.btnSpeechToText)

    // Inicializar Text-to-Speech y Speech-to-Text
    textToSpeechHelper = TextToSpeechHelper(this)
    speechRecognizerHelper = SpeechRecognizerHelper(this) { result ->
      textViewSpeechToText.text = result
    }

    // Inicializar OpenAIService con la API Key
    openAIService = OpenAIService("")

    // Configurar botones
    // Boton Text-to-Speech
    btnTextToSpeech.setOnClickListener {
      // Valor para almacenar el string del prompt
      val prompt = editTextTextToSpeech.text.toString()

      // Llamar a la API de OpenAI para generar el texto
      openAIService.generateText(prompt) { generatedText ->
        // Pasar el texto generado a Text-to-Speech
        textToSpeechHelper.speak(generatedText)
      }
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