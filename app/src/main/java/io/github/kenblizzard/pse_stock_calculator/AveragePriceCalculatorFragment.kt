package io.github.kenblizzard.pse_stock_calculator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import kotlinx.android.synthetic.main.fragment_average_price_calculator.*
import kotlinx.android.synthetic.main.fragment_average_price_calculator.view.*

class AveragePriceCalculatorFragment : Fragment(), StocksAdapter.OnStocksAdapterListener {

    fun Double.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)

    lateinit var listStocks: ArrayList<Stock>
    lateinit var stocksAdapter: StocksAdapter
    lateinit var listNumberOfShares: ArrayList<Long>
    lateinit var listBuyAmount: ArrayList<Double>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);

        listStocks = ArrayList()
        listNumberOfShares = ArrayList()
        listBuyAmount = ArrayList()
    }

    private fun computeTotalResult() {
        var totalShares: Long = 0
        var totalBuyAmount: Double = 0.0
        var totalAveragePrice: Double = 0.0

        if (listNumberOfShares.size > 0 && listBuyAmount.size > 0) {

            for (numShares in this.listNumberOfShares) {
                totalShares += numShares
            }

            for (buyAmount in this.listBuyAmount) {
                totalBuyAmount += buyAmount
            }

            totalAveragePrice = totalBuyAmount / totalShares
        }


        textTotalBuyAmount.text = totalBuyAmount.format(4)
        textTotalShares.text = totalShares.toDouble().format(0)
        textTotalAverage.text = totalAveragePrice.format(4)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.action_compute_profit)?.setVisible(true);
        menu?.findItem(R.id.action_reset_fields)?.setVisible(false);

        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view: View = inflater.inflate(R.layout.fragment_average_price_calculator, container, false)

        var stock: Stock = Stock()

        stock.buyPrice = 0.0
        stock.numberOfShares = 0
        stock.sellPrice = 0.0

        listStocks.add(stock)
        listNumberOfShares.add(0)
        listBuyAmount.add(0.0)

        stocksAdapter = StocksAdapter(listStocks, this)


        val mLayoutManager = LinearLayoutManager(this.context)
        var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider))
        recyclerView.addItemDecoration(itemDecorator)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.adapter = stocksAdapter
        recyclerView.setItemViewCacheSize(50);


        view.fabNewBuyTransaction.setOnClickListener {
            this.onStocksAddButtonClicked()
            view.recyclerView.smoothScrollToPosition(listStocks.size - 1)
        }


        stocksAdapter.notifyDataSetChanged()


        MobileAds.initialize(this.context, "ca-app-pub-1200631640695401~4382253594");

        val adRequest = AdRequest.Builder().build()

        view.multipleBuyAdView2.loadAd(adRequest)
        view.multipleBuyAdView2.setAdListener(object : AdListener() {

            override fun onAdLoaded() {
                view.multipleBuyAdView2.setVisibility(View.VISIBLE)
            }

            override fun onAdFailedToLoad(error: Int) {
                view.multipleBuyAdView2.setVisibility(View.GONE)
            }

        })

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view
    }


    override fun onStocksAddButtonClicked() {

        if (this.listStocks.size > 4) {
            Toast.makeText(activity.applicationContext, "Howdy, you have reached your max limit of 5 buy transactions. Contact the developer to add more!", Toast.LENGTH_LONG).show()
            return
        }
        var newStock: Stock = Stock()
        newStock.buyPrice = 0.0
        newStock.numberOfShares = 0
        newStock.sellPrice = 0.0

        this.listStocks.add(newStock)
        this.listNumberOfShares.add(0)
        this.listBuyAmount.add(0.0)


        stocksAdapter.notifyItemInserted(this.listStocks.size - 1)
    }

    override fun onStocksComputeChanged(numberOfShares: Long?, averagePricePerShare: Double?, averageTotalAmount: Double?, position: Int) {

        if (position > this.listNumberOfShares.size - 1) {
            return
        }
        this.listNumberOfShares.set(position, numberOfShares!!)
        this.listBuyAmount.set(position, averageTotalAmount!!)


        computeTotalResult()
    }

    override fun onStocksRemoveButtonClicked(position: Int) {

        try {

            recyclerView.recycledViewPool.clear()

            var tempPosition: Int = 0

            if (position >= this.listStocks.size) {
                tempPosition = 0
            } else {
                tempPosition = position
            }
            this.listStocks.removeAt(tempPosition)
            this.listNumberOfShares.removeAt(tempPosition)
            this.listBuyAmount.removeAt(tempPosition)

            stocksAdapter.notifyItemRemoved(tempPosition)
        } catch (e: ExceptionInInitializerError) {
            Log.d("error ", e.message)
        }

        computeTotalResult()
    }

}
