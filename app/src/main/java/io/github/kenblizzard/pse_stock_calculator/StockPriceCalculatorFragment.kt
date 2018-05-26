package io.github.kenblizzard.pse_stock_calculator

import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.SeekBar
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.model.TransactionFeeComponents
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator.TRANSACTION_FEE_BASE_VALUES
import io.github.kenblizzard.pse_stock_calculator.util.Constants
import kotlinx.android.synthetic.main.fragment_stock_price_calculator.*
import kotlinx.android.synthetic.main.fragment_stock_price_calculator.view.*


private const val ARG_NUMBER_OF_SHARES = "ARG_NUMBER_OF_SHARES"
private const val ARG_BUY_PRICE = "ARG_BUY_PRICE"
private const val ARG_SELL_PRICE = "ARG_SELL_PRICE"

class StockPriceCalculatorFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    // TODO: Rename and change types of parameters

    private var SAVE_KEY_NUM_SHARES = "SAVE_KEY_NUM_SHARES"
    private var SAVE_KEY_BUY_PRICE = "SAVE_KEY_BUY_PRICE"
    private var SAVE_KEY_SELL_PRICE = "SAVE_KEY_SELL_PRICE"


    lateinit var textWatcher: TextWatcher
//    private var listener: OnFragmentInteractionListener? = null


    fun Double.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)

    private fun calculateResultOnValueChanged() {
        var buyTransactionFee: TransactionFeeComponents
        var sellTransactionFee: TransactionFeeComponents

        var stock = Stock();

        stock.numberOfShares = editNumberOfShares.text.toString().toLongOrNull()
        stock.buyPrice = editBuyPrice.text.toString().toDoubleOrNull()
        stock.sellPrice = editSellPrice.text.toString().toDoubleOrNull()


        if (stock.numberOfShares == null || stock.numberOfShares == 0L) {
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


        textSellTotalProfit.text = "" + totalProfit.format(2) + " (" + totalProfitPercentage.format(2) + "%)";

        if (totalProfitPercentage > 0) {
            textSellTotalProfit.setTextColor(ContextCompat.getColor(this.context, R.color.colorBuy))
        } else {
            textSellTotalProfit.setTextColor(ContextCompat.getColor(this.context, R.color.colorSell))
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.action_compute_profit)?.setVisible(false);
        menu?.findItem(R.id.action_reset_fields)?.setVisible(true);

        super.onPrepareOptionsMenu(menu)
    }



    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p0 != null) {

            var buyPrice: Double
            if (editBuyPrice.text.toString().isNullOrBlank()) {
                buyPrice = 0.00
                return
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

            if (profitPercentage > 0) {
                seekBarGainPercentage.progressDrawable.setColorFilter(ContextCompat.getColor(this.context, R.color.colorBuy), PorterDuff.Mode.MULTIPLY)
            } else {
                seekBarGainPercentage.progressDrawable.setColorFilter(ContextCompat.getColor(this.context, R.color.colorSell), PorterDuff.Mode.MULTIPLY)
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
        setHasOptionsMenu(true);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            editNumberOfShares.setText("" + it?.getLong(ARG_NUMBER_OF_SHARES))
            editBuyPrice.setText("" + it?.getDouble(ARG_BUY_PRICE))
            editSellPrice.setText("" + it?.getDouble(ARG_SELL_PRICE))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView: View = inflater.inflate(R.layout.fragment_stock_price_calculator, container, false)

        rootView.seekBarGainPercentage.setOnSeekBarChangeListener(this);

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                calculateResultOnValueChanged()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }

        rootView.editBuyPrice.addTextChangedListener(textWatcher)
        rootView.editSellPrice.addTextChangedListener(textWatcher)
        rootView.editNumberOfShares.addTextChangedListener(textWatcher)


        MobileAds.initialize(this.context, "ca-app-pub-1200631640695401~4382253594");

        val adRequest = AdRequest.Builder().build()
        rootView.adView.loadAd(adRequest)

        rootView.adView.setAdListener(object : AdListener() {

            override fun onAdLoaded() {
                rootView.adView.setVisibility(View.VISIBLE)
            }

            override fun onAdFailedToLoad(error: Int) {
                rootView.adView.setVisibility(View.GONE)
            }
        })

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return rootView
    }


    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

    companion object {

        @JvmStatic
        fun newInstance(numberOfShares: Long, buyPrice: Double, sellPrice: Double) =
                StockPriceCalculatorFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_NUMBER_OF_SHARES, numberOfShares)
                        putDouble(ARG_BUY_PRICE, buyPrice)
                        putDouble(ARG_SELL_PRICE, sellPrice)
                    }
                }
    }
}
