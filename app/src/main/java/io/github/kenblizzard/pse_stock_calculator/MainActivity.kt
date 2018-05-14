package io.github.kenblizzard.pse_stock_calculator


import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.kobakei.ratethisapp.RateThisApp
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.util.Constants.*
import kotlinx.android.synthetic.main.activity_nav_main.*
import kotlinx.android.synthetic.main.app_bar_nav_main.*
import kotlinx.android.synthetic.main.fragment_average_price_calculator.*
import kotlinx.android.synthetic.main.fragment_budget_stocks_calculator.*
import kotlinx.android.synthetic.main.fragment_stock_price_calculator.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var mAdView: AdView

    var budgetStocksCalculatorFragment = BudgetStocksCalculatorFragment()
    var stockPriceCalculatorFragment = StockPriceCalculatorFragment()
    var averagePriceCalculatorFragment = AveragePriceCalculatorFragment()
    var cashDividendPayoutFragment = CashDividendPayoutFragment()

    var mFirebaseAnalytics: FirebaseAnalytics? = null;


    fun displayFragmentView(itemId: Int, stock: Stock? = null) {

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, FIREBASE_ANALYTICS_MENU_CLICK)

        var title: String = "";
        var ft: FragmentTransaction = supportFragmentManager.beginTransaction()



        when (itemId) {
            R.id.nav_stocks_calc -> {
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, FIREBASE_ANALYTICS_MENU_STOCKS_PRICE_CALC)
                title = "Price & Profit"

                if (stock != null) {
                    stockPriceCalculatorFragment = StockPriceCalculatorFragment.newInstance(stock.numberOfShares, stock.buyPrice, stock.sellPrice)
                }
                ft.replace(R.id.content_frame, stockPriceCalculatorFragment, FRAGMENT_TAG_STOCKS_PRICE)
                ft.commit()

            }

            R.id.nav_budget_stocks_calc -> {
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, FIREBASE_ANALYTICS_MENU_BUDGET_STOCS_CALC)
                title = "Budget your Buy Power"
                ft.replace(R.id.content_frame, budgetStocksCalculatorFragment, FRAGMENT_TAG_BUDGET_BP)
                ft.commit()
            }

            R.id.nav_average_up_down -> {
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, FIREBASE_ANALYTICS_MENU_MULTIPLE_BUYS)
                title = "Multiple Buy Transaction"
                ft.replace(R.id.content_frame, averagePriceCalculatorFragment, FRAGMENT_TAG_MULTIPLE_BUYS)
                ft.commit()
            }
        }

        if (supportActionBar != null) {
            getSupportActionBar()?.title = title
        }

        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)


        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);

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
        menuInflater.inflate(R.menu.nav_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var stockPriceCalculatorFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_STOCKS_PRICE);
        var budgetStocksCalculatorFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_BUDGET_BP)
        var averagePriceCalculatorFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_MULTIPLE_BUYS)

        when (item.itemId) {
            R.id.action_reset_fields -> {

                if (stockPriceCalculatorFragment != null && stockPriceCalculatorFragment.isVisible) {
//
                    if (stockPriceCalculatorFragment.editNumberOfShares != null) {
                        stockPriceCalculatorFragment.editNumberOfShares.setText("")
                    }

                    if (stockPriceCalculatorFragment.editBuyPrice != null) {
                        stockPriceCalculatorFragment.editBuyPrice.setText("")
                    }

                    if (stockPriceCalculatorFragment.editSellPrice != null) {
                        stockPriceCalculatorFragment.editSellPrice.setText("")
                    }
                }

                if (budgetStocksCalculatorFragment != null && budgetStocksCalculatorFragment.isVisible) {
                    if (budgetStocksCalculatorFragment.editBudgetBuyingPower != null) {
                        budgetStocksCalculatorFragment.editBudgetBuyingPower.setText("")
                    }

                    if (budgetStocksCalculatorFragment.editBudgetStockPrice != null) {
                        budgetStocksCalculatorFragment.editBudgetStockPrice.setText("")
                    }

                    if (budgetStocksCalculatorFragment.editBudgetNumberOfShare != null) {
                        budgetStocksCalculatorFragment.editBudgetNumberOfShare.setText("")
                    }
                }

                return true
            }
            R.id.action_compute_profit -> {
                if (averagePriceCalculatorFragment != null && averagePriceCalculatorFragment.isVisible) {
                    var stock = Stock()
                    stock.numberOfShares = averagePriceCalculatorFragment.textTotalShares.text.toString().replace(",", "").toLongOrNull()
                    stock.buyPrice = averagePriceCalculatorFragment.textTotalAverage.text.toString().replace(",", "").toDoubleOrNull()
                    stock.sellPrice = stock.buyPrice

                    this.displayFragmentView(R.id.nav_stocks_calc, stock)
                }
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
