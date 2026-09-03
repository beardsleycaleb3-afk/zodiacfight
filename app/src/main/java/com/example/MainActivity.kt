package com.example

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.CelestialDarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private var gameWebView: WebView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        ZodiacFusionGameScreen { webView ->
          gameWebView = webView
        }
      }
    }
  }

  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
    if (gameWebView?.canGoBack() == true) {
      gameWebView?.goBack()
    } else {
      super.onBackPressed()
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ZodiacFusionGameScreen(onWebViewCreated: (WebView) -> Unit = {}) {
  AndroidView(
    modifier = Modifier
      .fillMaxSize()
      .background(CelestialDarkBg)
      .testTag("zodiac_arena_webview"),
    factory = { context ->
      WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.parseColor("#0F111A"))

        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          databaseEnabled = true
          mediaPlaybackRequiresUserGesture = false
          allowFileAccess = true
          cacheMode = WebSettings.LOAD_DEFAULT
          useWideViewPort = true
          loadWithOverviewMode = true
          displayZoomControls = false
          builtInZoomControls = false
          setSupportZoom(false)
        }

        webViewClient = object : WebViewClient() {}
        webChromeClient = object : WebChromeClient() {}

        loadUrl("file:///android_asset/index.html")
        onWebViewCreated(this)
      }
    }
  )
}

