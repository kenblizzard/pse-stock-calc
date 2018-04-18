package io.github.kenblizzard.pse_stock_calculator

import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.model.TransactionFeeComponents
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator
import io.github.kenblizzard.pse_stock_calculator.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    fun Double.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)
    lateinit var mAdView : AdView


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p0 != null) {

            var buyPrice: Double
            if (editBuyPrice.text.toString().isNullOrBlank()) {
                buyPrice = 0.00
            } else {
                buyPrice = editBuyPrice.text.toString().toDouble()
            }

            var fluctuationDecimalPlace = 0


            if (buyPrice < 0.01) {
                fluctuationDecimalPlace = 4
            } else if (buyPrice < 0.5) {
                fluctuationDecimalPlace = 3
            } else if (buyPrice < 100) {
                fluctuationDecimalPlace = 2
            } else if (buyPrice < 1000) {
                fluctuationDecimalPlace = 1
            }


            var profitPercentage: Double = (((p0.progress - 150 + 100)).toDouble())

            if(profitPercentage > 0) {
                seekBarGainPercentage.progressDrawable.setColorFilter(ContextCompat.getColor(this,R.color.colorBuy), PorterDuff.Mode.MULTIPLY)
            }
            else {
                seekBarGainPercentage.progressDrawable.setColorFilter(ContextCompat.getColor(this,R.color.colorSell), PorterDuff.Mode.MULTIPLY)
            }

            editSellPrice.setText("" + ((buyPrice * profitPercentage / 100) + buyPrice).format(fluctuationDecimalPlace).replace(",", ""))
        };
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

       /* MobileAds.initialize(this, "ca-app-pub-1200631640695401~4382253594")
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)*/

        seekBarGainPercentage!!.setOnSeekBarChangeListener(this);


        editBuyPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                calculateResultOnValueChanged()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })


        editSellPrice.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                calculateResultOnValueChanged()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        editNumberOfShares.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                calculateResultOnValueChanged()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


    }


    fun calculateResultOnValueChanged() {
        var buyTransactionFee: TransactionFeeComponents
        var sellTransactionFee: TransactionFeeComponents

        var stock = Stock();

        stock.numberOfShares = editNumberOfShares.text.toString().toIntOrNull();
        stock.buyPrice = editBuyPrice.text.toString().toDoubleOrNull();
        stock.sellPrice = editSellPrice.text.toString().toDoubleOrNull();


        if (stock.numberOfShares == null || stock.numberOfShares == 0) {
            return
        }


        var buyTotalFee: Double;
        var buyTotalAmount: Double;

        buyTransactionFee = StocksCalculator.calculateTransactionFee(stock, TRANSACTION_FEE_BASE_VALUES, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY)
        buyTotalAmount = StocksCalculator.calculateTotalSharesPrice(stock, TRANSACTION_FEE_BASE_VALUES, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY)
        buyTotalFee = buyTransactionFee.totalFee;

        textBuyTotalFee.text = "" + buyTotalFee.format(2);
        textBuyTotalAmount.text = "" + buyTotalAmount.format(2)


        var sellTotalFee: Double;
        var sellTotalAmount: Double;


        sellTransactionFee = StocksCalculator.calculateTransactionFee(stock, TRANSACTION_FEE_BASE_VALUES, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_SELL)
        sellTotalAmount = StocksCalculator.calculateTotalSharesPrice(stock, TRANSACTION_FEE_BASE_VALUES, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_SELL)
        sellTotalFee = sellTransactionFee.totalFee;

        textSellTotalFee.text = "" + sellTotalFee.format(2);
        textSellTotalAmount.text = "" + sellTotalAmount.format(2)

        var totalProfit: Double;
        var totalProfitPercentage: Double;

        totalProfit = sellTotalAmount - (buyTotalAmount - buyTotalFee)
        totalProfitPercentage = (sellTotalAmount / (buyTotalAmount - buyTotalFee)) * 100 - 100


        textSellTotalProft.text = "" + totalProfit.format(2) + " (" + totalProfitPercentage.format(2) + "%)";
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }

        var TRANSACTION_FEE_BASE_VALUES: TransactionFeeComponents = TransactionFeeComponents(0.0025, 0.12,
                0.00005, 0.0001, 0.006)
    }
}
