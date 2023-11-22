package jamsxz.smartlistkeeper.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import jamsxz.smartlistkeeper.Adapter.PrefManager
import jamsxz.smartlistkeeper.R
import jamsxz.smartlistkeeper.databinding.ActivityGroceryFirstPageBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroceryFirstPageBinding
    private var viewPager: ViewPager? = null
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var dotsLayout: LinearLayout? = null
    private lateinit var dots: Array<TextView?>
    private lateinit var layouts: IntArray
    private var btnSkip: Button? = null
    private var btnNext: Button? = null
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroceryFirstPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Checking for first time launch - before calling setContentView()
        prefManager = PrefManager(this)
        if (!prefManager!!.isFirstTimeLaunch) {
            launchHomeScreen()
            finish()
        }

        // Making notification bar transparent
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        viewPager = binding.viewPager
        dotsLayout = binding.layoutDots
        btnSkip = binding.btnSkip
        btnNext = binding.btnNext


        // layouts of welcome sliders
        layouts = intArrayOf(
            R.drawable.ey,
            R.drawable.bi,
            R.drawable.si
        )

        // adding bottom dots
        addBottomDots(0)

        // making notification bar transparent
        changeStatusBarColor()
        myViewPagerAdapter = MyViewPagerAdapter()
        viewPager!!.adapter = myViewPagerAdapter
        viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
        btnSkip!!.setOnClickListener { launchHomeScreen() }
        btnNext!!.setOnClickListener {
            // checking for last page if true launch MainActivity
            val current = getItem(+1)


            if (current < layouts.size) {
                // move to next screen
                viewPager!!.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts.size)
        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("⸻")
            dots[i]!!.textSize = 50f

            if (i < colorsInactive.size) {
                if (i == currentPage) {
                    if (i < colorsActive.size) {
                        dots[i]!!.setTextColor(colorsActive[i])
                    } else {
                        // Handle cases where active colors are out of bounds
                        // For example, set a default color or handle it in a way that fits your design
                    }
                } else {
                    dots[i]!!.setTextColor(colorsInactive[i])
                }
            } else {
                // Handle cases where inactive colors are out of bounds
                // For example, set a default color or handle it in a way that fits your design
            }

            dotsLayout!!.addView(dots[i])
        }

    }

    private fun getItem(i: Int): Int {
        return viewPager!!.currentItem + i
    }

    private fun launchHomeScreen() {
        prefManager!!.isFirstTimeLaunch = false
        startActivity(Intent(this@WelcomeActivity, MainmainActivity::class.java))
        finish()
    }

    //  viewpager change listener
    private var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.size - 1) { // Check if it's the last page
                // last page. make button text to GOT IT
                btnNext!!.text = "Start  ➤"
                btnSkip!!.visibility = View.GONE
                binding.wala.visibility = View.GONE
            } else {
                // still pages are left
                btnNext!!.text = "Next  ➤"
                btnSkip!!.visibility = View.VISIBLE
                binding.wala.visibility = View.VISIBLE // Show 'wala' if it's not the last page
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    // Making notification bar transparent
    private fun changeStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    inner class MyViewPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val context = container.context
            val view = ImageView(context)
            view.setImageResource(layouts[position])
            view.scaleType = ImageView.ScaleType.FIT_XY
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}