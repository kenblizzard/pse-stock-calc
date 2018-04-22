package io.github.kenblizzard.pse_stock_calculator


import android.support.v4.app.Fragment
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.model.TransactionFeeComponents
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator
import io.github.kenblizzard.pse_stock_calculator.util.Constants
import kotlinx.android.synthetic.main.activity_nav_main.*
import kotlinx.android.synthetic.main.app_bar_nav_main.*
import kotlinx.android.synthetic.main.content_nav_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var mAdView: AdView

    var budgetStocksCalculatorFragment = BudgetStocksCalculatorFragment()
    var stockPriceCalculatorFragment   = StockPriceCalculatorFragment()


    fun displayFragmentView(itemId : Int) {

        var fragment : Fragment? = null;


        var title : String = "";

        var ft : FragmentTransaction = supportFragmentManager.beginTransaction()

        when(itemId) {
            R.id.nav_stocks_calc -> {

                title = "Stocks Price Calculator"
                ft.replace(R.id.content_frame, stockPriceCalculatorFragment)
            }

            R.id.nav_budget_stocks_calc -> {

                title = "Stocks Budget Calculator"
                ft.replace(R.id.content_frame, budgetStocksCalculatorFragment)
            }
        }

        ft.commit()

        if(supportActionBar != null) {
            getSupportActionBar()?.title = title
        }

        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        MobileAds.initialize(this, "ca-app-pub-1200631640695401~4382253594");



        displayFragmentView(R.id.nav_stocks_calc)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        displayFragmentView(item.itemId)
        return true
    }


    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }


    }
}
