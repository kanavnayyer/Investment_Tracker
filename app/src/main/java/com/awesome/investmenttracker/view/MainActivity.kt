package com.awesome.investmenttracker.view

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.awesome.investmenttracker.R
import com.awesome.investmenttracker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomAppBar.backgroundTintList =
            ContextCompat.getColorStateList(this, android.R.color.white)
        binding.fab.translationY = 64f

        binding.bottomNavigationView.background = null


        binding.fab.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    fun navigateTo(destinationId: Int) {
        navController.navigate(destinationId)
    }

    fun navigateHome(view: View) {
        navigateTo(R.id.homeFragment)
    }

    fun navigateTransactions(view: View) {
        val radioGroup = findViewById<RadioGroup>(R.id.rgTimeFilter)
        val selectedId = radioGroup.checkedRadioButtonId

        if (selectedId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedId)
            val selectedText = selectedRadioButton.text.toString()

            val bundle = Bundle().apply {
                putString(getString(R.string.selectedtimefilter), selectedText)
            }

            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.transactionsFragment, bundle)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
                getString(R.string.android_webkit)
            )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                (this.window.decorView.applicationWindowToken),
                0
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    fun navToTran(view: View) {
        val bundle = Bundle().apply {
            putString(getString(R.string.selectedtimefilter), getString(R.string.today_))
        }
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.transactionsFragment, bundle)

    }


}
