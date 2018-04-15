package io.github.kenblizzard.pse_stock_calculator

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.model.TransactionFeeComponents
import io.github.kenblizzard.pse_stock_calculator.service.StockPriceCalculator
import io.github.kenblizzard.pse_stock_calculator.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    fun Double.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p0 != null) {
            Snackbar.make(p0.rootView, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (p0 != null) {
            editSellPrice.setText("" + p0.progress)
        };
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        seekBarGainPercentage!!.setOnSeekBarChangeListener(this);

        editBuyPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var transactionFeeComponents = TransactionFeeComponents(0.0025, 0.12,
                        0.00005, 0.0001, 0.006)

                var stock = Stock();

                stock.numberOfShares = editNumberOfShares.text.toString().toIntOrNull();
                stock.buyPrice = editBuyPrice.text.toString().toDoubleOrNull();
                stock.sellPrice = editSellPrice.text.toString().toDoubleOrNull();


                var totalFee: Double;
                var totalAmount: Double;


                transactionFeeComponents = StockPriceCalculator.calculateTransactionFee(stock, transactionFeeComponents, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY)
                totalAmount = StockPriceCalculator.calculateTotalSharesPrice(stock, transactionFeeComponents, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY)


                totalFee = transactionFeeComponents.totalFee;

                textBuyTotalFee.text = "" + totalFee.format(2);
                textBuyTotalAmount.text = "" + totalAmount.format(2)

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        editSellPrice.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                var transactionFeeComponents = TransactionFeeComponents(0.0025, 0.12,
                        0.00005, 0.0001, 0.006)

                var stock = Stock();

                stock.numberOfShares = editNumberOfShares.text.toString().toIntOrNull();
                stock.buyPrice = editBuyPrice.text.toString().toDoubleOrNull();
                stock.sellPrice = editSellPrice.text.toString().toDoubleOrNull();


                var totalFee: Double;
                var totalAmount: Double;


                transactionFeeComponents = StockPriceCalculator.calculateTransactionFee(stock, transactionFeeComponents, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_SELL)
                totalAmount = StockPriceCalculator.calculateTotalSharesPrice(stock, transactionFeeComponents, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_SELL)


                totalFee = transactionFeeComponents.totalFee;

                textSellTotalFee.text = "" + totalFee.format(2);
                textSellTotalAmount.text = "" + totalAmount.format(2)

                var totalProfit : Double;

                totalProfit = totalAmount - textBuyTotalAmount.text.toString().replace(
                        ",",
                        "",
                        true
                ).toDouble();

                textSellTotalProft.text = "" + totalProfit.format(2);

            }

        })


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
    }
}
