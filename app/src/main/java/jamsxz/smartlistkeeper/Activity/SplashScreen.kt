package jamsxz.smartlistkeeper.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jamsxz.smartlistkeeper.R
import org.json.JSONException

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private var webView: WebView? = null
    var ExitTime: Long = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    /*    webView = findViewById(R.id.splash)

        val webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webView?.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun onDataReceived(data: String) {
                // Handle the received data from JavaScript
                Log.d("WebViewData", "Received data: $data")


                try {
                    if (data == "success") {
                        // Do something for success
                        val intent = Intent(this@SplashScreen, WelcomeActivity::class.java)
                        this@SplashScreen.startActivity(intent)
                        this@SplashScreen.finish()
                    }else{
                        runOnUiThread {
                            // UI-related operations, e.g., setVisibility
                            webView?.visibility = View.VISIBLE
                            webView?.loadUrl(data)
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("JSON Parse Error", e.message ?: "Unknown error")
                }
            }
        }, "Android")

        webView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                // Do something when the page starts loading
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // Do something when the page finishes loading
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                // Handle errors during loading
            }
        }


        // Load the URL after setting up WebViewClient and other configurations
        webView?.loadUrl("file:///android_asset/index.html")*/
        Handler().postDelayed({
            startActivity(Intent(this, WelcomeActivity::class.java).apply {
                // If you need to pass data to the next activity, you can do it here
            })
            finish()
        }, 1500L)
    }

    override fun onKeyDown(quizKeyCode: Int, quizEvent: KeyEvent): Boolean {
        if (quizKeyCode == KeyEvent.KEYCODE_BACK && quizEvent.action == KeyEvent.ACTION_DOWN) {
            if (webView!!.canGoBack()) {
                webView?.goBack()
            } else {
                try {
                    if (System.currentTimeMillis() - ExitTime > 2000) {
                        Toast.makeText(
                            applicationContext,
                            "Press back again to Exit",
                            Toast.LENGTH_SHORT
                        ).show()
                        ExitTime = System.currentTimeMillis()
                    } else {
                        finishAffinity()
                    }
                } catch (_: Exception) {
                }
            }
            return true
        }
        return super.onKeyDown(quizKeyCode, quizEvent)
    }

}
