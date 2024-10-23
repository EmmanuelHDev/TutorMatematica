package com.example.tutormatematica

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

  private lateinit var textToSpeech: TextToSpeech
  private lateinit var speechRecognizer: SpeechRecognizer
  private lateinit var editTextInput: EditText
  private lateinit var textViewResult: TextView
  private val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 2

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    setContentView(R.layout.activity_main)
    
    setContentView(R.layout.voz_texto)

    // Inicializar Text-to-Speech
    textToSpeech = TextToSpeech(this, this)
    editTextInput = findViewById(R.id.editTextInput)
    val btnTextToSpeech: Button = findViewById(R.id.btnTextToSpeech)
    textViewResult = findViewById(R.id.textViewResult)
    val btnSpeechToText: Button = findViewById(R.id.btnSpeechToText)

    // Text-to-Speech: Convertir texto a voz
    btnTextToSpeech.setOnClickListener {
      val text = editTextInput.text.toString()
      if (text.isNotEmpty()) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
      }
    }

    // Verificar permisos para grabar audio
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
      != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION_CODE)
    }

    // Inicializar SpeechRecognizer
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

    // Speech-to-Text: Reconocer voz
    btnSpeechToText.setOnClickListener {
      speechRecognizer.startListening(speechIntent)
    }

    // Listener para manejar los resultados del reconocimiento de voz
    speechRecognizer.setRecognitionListener(object : RecognitionListener {
      override fun onReadyForSpeech(params: Bundle?) {}

      override fun onBeginningOfSpeech() {}

      override fun onRmsChanged(rmsdB: Float) {}

      override fun onBufferReceived(buffer: ByteArray?) {}

      override fun onEndOfSpeech() {}

      override fun onError(error: Int) {
        textViewResult.text = "Error al reconocer la voz."
      }

      override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
          textViewResult.text = matches[0] // Mostrar el resultado de voz a texto
        }
      }

      override fun onPartialResults(partialResults: Bundle?) {}

      override fun onEvent(eventType: Int, params: Bundle?) {}
    })
  }

  // Inicialización de Text-to-Speech
  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = textToSpeech.setLanguage(Locale("es", "ES"))
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        Log.e("TTS", "Idioma no soportado")
      }
    } else {
      Log.e("TTS", "Inicialización fallida")
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (textToSpeech != null) {
      textToSpeech.stop()
      textToSpeech.shutdown()
    }
    if (speechRecognizer != null) {
      speechRecognizer.destroy()
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<out String>, grantResults: IntArray
  ) {
    if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_CODE) {
      if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        textViewResult.text = "Permiso de grabación denegado"
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }
}
