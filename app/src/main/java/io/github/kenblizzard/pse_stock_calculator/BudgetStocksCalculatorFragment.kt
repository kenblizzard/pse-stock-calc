package io.github.kenblizzard.pse_stock_calculator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator
import io.github.kenblizzard.pse_stock_calculator.util.Constants
import kotlinx.android.synthetic.main.fragment_budget_stocks_calculator.*
import kotlinx.android.synthetic.main.fragment_budget_stocks_calculator.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BudgetStocksCalculatorFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BudgetStocksCalculatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BudgetStocksCalculatorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    fun Double.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)


    lateinit var textWatcher: TextWatcher


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

        var avePricePerShare = totalAmount / numSharesBasedOnBoardLot

        editBudgetNumberOfShare.setText("" + numSharesBasedOnBoardLot.toDouble().format(0))
        textBoardLot.setText("" + numBoardLot.toDouble().format(0))
        textBudgetAvePricePerShare.setText("" + avePricePerShare.format(4))
        textBudgetTotalFee.setText("" + transactionFeeComponents.totalFee.format(2))
        textBudgetTotalAmount.setText("" + totalAmount.format(2))
        textBudgetRemainingBuyingPower.setText("" + (buyingPower - totalAmount).format(2))


    }
//    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var rootView: View = inflater.inflate(R.layout.fragment_budget_stocks_calculator, container, false)

        rootView.editBudgetBuyingPower.addTextChangedListener(textWatcher)
        rootView.editBudgetStockPrice.addTextChangedListener(textWatcher)

        rootView.btnCalculateProfit.setOnClickListener(View.OnClickListener { view ->
            if (activity is MainActivity) {

                var stock = Stock()


                stock.numberOfShares = editBudgetNumberOfShare.text.toString().replace(",","").toLongOrNull()
                stock.buyPrice = editBudgetStockPrice.text.toString().toDoubleOrNull()
                stock.sellPrice = editBudgetStockPrice.text.toString().toDoubleOrNull()

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BudgetStocksCalculatorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BudgetStocksCalculatorFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
