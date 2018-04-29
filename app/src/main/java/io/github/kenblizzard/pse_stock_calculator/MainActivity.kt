package io.github.kenblizzard.pse_stock_calculator


import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator
import kotlinx.android.synthetic.main.activity_nav_main.*
import kotlinx.android.synthetic.main.app_bar_nav_main.*
import kotlinx.android.synthetic.main.fragment_budget_stocks_calculator.*
import kotlinx.android.synthetic.main.fragment_stock_price_calculator.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var mAdView: AdView

    var budgetStocksCalculatorFragment = BudgetStocksCalculatorFragment()
    var stockPriceCalculatorFragment = StockPriceCalculatorFragment()
    var cashDividendPayoutFragment = CashDividendPayoutFragment()


    fun displayFragmentView(itemId: Int, stock: Stock? = null) {

        var fragment: Fragment? = null;


        var title: String = "";

        var ft: FragmentTransaction = supportFragmentManager.beginTransaction()

        when (itemId) {
            R.id.nav_stocks_calc -> {

                title = "Price & Profit"

                if (stock != null) {
                    stockPriceCalculatorFragment = StockPriceCalculatorFragment.newInstance(stock.numberOfShares, stock.buyPrice, stock.sellPrice)
                }
                ft.replace(R.id.content_frame, stockPriceCalculatorFragment)
                ft.commit()

            }

            R.id.nav_budget_stocks_calc -> {

                title = "Budget your Buying Power"
                ft.replace(R.id.content_frame, budgetStocksCalculatorFragment)
                ft.commit()
            }

            R.id.nav_cash_dividend_calc -> {
                title = "Dividend Profits"
                ft.replace(R.id.content_frame, cashDividendPayoutFragment)
                ft.commit()
            }
        }

        if (supportActionBar != null) {
            getSupportActionBar()?.title = title
        }

        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_main)
        setSupportActionBar(toolbar)

        StocksCalculator.initFirebaseTransactionFeeValues();

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
            R.id.action_reset_fields -> {

//
//                if (editNumberOfShares != null) {
//                    editNumberOfShares.setText("")
//                }
//
//                if (editBuyPrice != null) {
//                    editBuyPrice.setText("")
//                }
//
//                if (editSellPrice != null) {
//                    editSellPrice.setText("")
//                }
//
//                if(editBudgetBuyingPower != null) {
//                    editBudgetBuyingPower.setText("")
//                }
//
//                if(editBudgetStockPrice != null) {
//                    editBudgetStockPrice.setText("")
//                }
//
//                if(editBudgetNumberOfShare != null) {
//                    editBudgetNumberOfShare.setText("")
//                }

                return true
            }
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
