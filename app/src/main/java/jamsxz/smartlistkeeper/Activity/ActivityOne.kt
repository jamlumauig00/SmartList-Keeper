package jamsxz.smartlistkeeper.Activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import jamsxz.smartlistkeeper.R
import jamsxz.smartlistkeeper.databinding.ActivityOneBinding
import java.lang.Math.abs

class ActivityOne : AppCompatActivity() {
    private lateinit var binding: ActivityOneBinding
    private var webView: WebView? = null
    private val IS_FULLSCREEN = "is_fullscreen_pref"
    private var lastTouch: Long = 0
    private val touchThreshold: Long = 2000
    private var exitTime: Long = 0
    private var url: String = ""

    @SuppressLint(
        "SetJavaScriptEnabled", "ShowToast", "ClickableViewAccessibility",
        "MissingInflatedId"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        url = intent.getStringExtra("category").toString()
        webView = binding.webView
        Log.e("url", url)

        val isOrientationEnabled = try {
            Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 1
        } catch (e: Settings.SettingNotFoundException) {
            Log.d("WebActivity", "Settings could not be loaded")
            false
        }

        val screenLayout =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        if ((screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE || screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            && isOrientationEnabled
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }

        val settings = webView?.settings
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            settings?.cacheMode = WebSettings.LOAD_DEFAULT
        }

        if (settings != null) {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            settings.allowFileAccess = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            settings.builtInZoomControls = false
            settings.setSupportZoom(false)
        }

        if (savedInstanceState != null) {
            webView?.restoreState(savedInstanceState)
        } else {
            webView?.loadUrl(url)
        }

        webView?.setOnTouchListener { _, event ->
            val currentTime = System.currentTimeMillis()
            if (event.action == MotionEvent.ACTION_UP && kotlin.math.abs(currentTime - lastTouch) > touchThreshold) {
                val toggledFullScreen = !isFullScreen()
                saveFullScreen(toggledFullScreen)
                applyFullScreen(toggledFullScreen)
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                lastTouch = currentTime
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        webView?.loadUrl(url)
    }

    private fun saveFullScreen(isFullScreen: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putBoolean(IS_FULLSCREEN, isFullScreen)
        editor.apply()
    }

    private fun isFullScreen(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(IS_FULLSCREEN, true)
    }

    private fun applyFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (webView!!.canGoBack()) {
                webView!!.goBack()
            } else {
                try {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        Toast.makeText(
                            applicationContext,
                            "Press back again to Exit",
                            Toast.LENGTH_SHORT
                        ).show()
                        exitTime = System.currentTimeMillis()
                    } else {
                        finishAffinity()
                    }
                } catch (_: Exception) {
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        webView?.destroy()
    }
}