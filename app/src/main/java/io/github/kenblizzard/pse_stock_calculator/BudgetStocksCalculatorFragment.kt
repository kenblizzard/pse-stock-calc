package io.github.kenblizzard.pse_stock_calculator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator
import io.github.kenblizzard.pse_stock_calculator.util.Constants
import kotlinx.android.synthetic.main.fragment_budget_stocks_calculator.*
import kotlinx.android.synthetic.main.fragment_budget_stocks_calculator.view.*
import java.util.*

class BudgetStocksCalculatorFragment : Fragment() {

    fun Double.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)

    lateinit var textWatcher: TextWatcher
    private var mInterstitialAd: InterstitialAd? = null;


    fun calculateBudgetShares() {

        var stock: Stock = Stock()

        var numSharesBasedOnBoardLot: Long = 0
        var buyingPower = editBudgetBuyingPower.text.toString().toDoubleOrNull()


        stock.buyPrice = editBudgetStockPrice.text.toString().toDoubleOrNull()
        stock.sellPrice = 0.0;

        if (stock.buyPrice == null || buyingPower == null || stock.buyPrice <= 0 || buyingPower <= 0) {
            return;
        }

        var possibleNumShares: Long = (buyingPower / stock.buyPrice).toLong();

        var numBoardLot = StocksCalculator.getBoardLot(stock.buyPrice)
        numSharesBasedOnBoardLot = StocksCalculator.roundNumberOfSharesByBoardLot(stock.buyPrice, possibleNumShares)

        stock.numberOfShares = numSharesBasedOnBoardLot

        var transactionFeeComponents =
                StocksCalculator.calculateTransactionFee(stock, StocksCalculator.TRANSACTION_FEE_BASE_VALUES,
                        Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY)

        var totalAmount: Double = (stock.buyPrice * numSharesBasedOnBoardLot) + transactionFeeComponents.totalFee


        while (totalAmount > buyingPower) {
            numSharesBasedOnBoardLot = numSharesBasedOnBoardLot - numBoardLot
            stock.numberOfShares = numSharesBasedOnBoardLot

            transactionFeeComponents =
                    StocksCalculator.calculateTransactionFee(stock, StocksCalculator.TRANSACTION_FEE_BASE_VALUES,
                            Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY)

            totalAmount = (stock.buyPrice * numSharesBasedOnBoardLot) + transactionFeeComponents.totalFee
        }
        var avePricePerShare = 0.0
        var breakEvenPrice = stock.buyPrice
        if (numSharesBasedOnBoardLot > 0) {
            avePricePerShare = totalAmount / numSharesBasedOnBoardLot


            var breakEvenTotalAmount = 0.0


                while (breakEvenPrice > 0 && breakEvenTotalAmount < totalAmount) {
                    breakEvenPrice = breakEvenPrice + StocksCalculator.getTickSize(avePricePerShare)
                    var breakEvenStock = Stock()
                    breakEvenStock.buyPrice = avePricePerShare
                    breakEvenStock.sellPrice = breakEvenPrice
                    breakEvenStock.numberOfShares = numSharesBasedOnBoardLot

                    var breakEvenTransactionFeeComponents = StocksCalculator.calculateTransactionFee(breakEvenStock, StocksCalculator.TRANSACTION_FEE_BASE_VALUES, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_SELL)
                    breakEvenTotalAmount = StocksCalculator.calculateTotalSharesPrice(breakEvenStock, breakEvenTransactionFeeComponents, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_SELL)
                }

        } else {
            numSharesBasedOnBoardLot = 0
            avePricePerShare = 0.0
            breakEvenPrice = 0.0
        }


        editBudgetNumberOfShare.setText("" + numSharesBasedOnBoardLot.toDouble().format(0))
        editTextBreakEvenPrice.setText("" + breakEvenPrice.toDouble().format(4))
        textBoardLot.setText("" + numBoardLot.toDouble().format(0))
        textBudgetAvePricePerShare.setText("" + avePricePerShare.format(4))
        textBudgetTotalFee.setText("" + transactionFeeComponents.totalFee.format(2))
        textBudgetTotalAmount.setText("" + totalAmount.format(2))
        textBudgetRemainingBuyingPower.setText("" + (buyingPower - totalAmount).format(2))


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateBudgetShares()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.action_compute_profit)?.setVisible(false);
        menu?.findItem(R.id.action_reset_fields)?.setVisible(true);

        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_budget_stocks_calculator, container, false)

        rootView.editBudgetBuyingPower.addTextChangedListener(textWatcher)
        rootView.editBudgetStockPrice.addTextChangedListener(textWatcher)

        rootView.btnCalculateProfit.setOnClickListener(View.OnClickListener { view ->
            if (activity is MainActivity) {

                val rand = Random()

// nextInt as provided by Random is exclusive of the top value so you need to add 1

                val randomNum = rand.nextInt((10 - 1) + 1) + 1


                if (mInterstitialAd!!.isLoaded && randomNum < 4) {
                    mInterstitialAd!!.show()
                }

                var stock = Stock()


                stock.numberOfShares = editBudgetNumberOfShare.text.toString().replace(",", "").toLongOrNull()
                stock.buyPrice = editBudgetStockPrice.text.toString().replace(",", "").toDoubleOrNull()
                stock.sellPrice = editTextBreakEvenPrice.text.toString().replace(",", "").toDoubleOrNull()

                (activity as MainActivity).displayFragmentView(R.id.nav_stocks_calc, stock)
            }
        })


        MobileAds.initialize(this.context, "ca-app-pub-1200631640695401~4382253594");

        val adRequest = AdRequest.Builder().build()

        rootView.budgetAdView.loadAd(adRequest)
        rootView.budgetAdView.setAdListener(object : AdListener() {

            override fun onAdLoaded() {
                rootView.budgetAdView.setVisibility(View.VISIBLE)
            }

            override fun onAdFailedToLoad(error: Int) {
                rootView.budgetAdView.setVisibility(View.GONE)
            }

        })



        mInterstitialAd = InterstitialAd(this.context)
        mInterstitialAd!!.adUnitId = "ca-app-pub-1200631640695401/4589000058"
        mInterstitialAd!!.loadAd(AdRequest.Builder().build())

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return rootView
    }
}
