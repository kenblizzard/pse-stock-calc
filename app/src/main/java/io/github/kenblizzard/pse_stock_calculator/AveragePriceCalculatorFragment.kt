package io.github.kenblizzard.pse_stock_calculator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.kenblizzard.pse_stock_calculator.model.Stock

class AveragePriceCalculatorFragment : Fragment(), StocksAdapter.OnStocksAdapterListener {

    lateinit var listStocks: ArrayList<Stock>
    lateinit var stocksAdapter: StocksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listStocks = ArrayList()

        var stock: Stock = Stock()

        stock.buyPrice = 40.0
        stock.numberOfShares = 1000
        stock.sellPrice = 0.0

        listStocks.add(stock)

        var newStock: Stock = Stock()
        newStock.buyPrice = 0.0
        newStock.numberOfShares = 0
        newStock.sellPrice = 0.0
        listStocks.add(newStock)

        stocksAdapter = StocksAdapter(listStocks, this)
        stocksAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view: View = inflater.inflate(R.layout.fragment_average_price_calculator, container, false)

        val mLayoutManager = LinearLayoutManager(this.context)
        var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider))
        recyclerView.addItemDecoration(itemDecorator)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(stocksAdapter)

        return view
    }


    override fun onStocksAddButtonClicked() {
        var newStock: Stock = Stock()
        newStock.buyPrice = 0.0
        newStock.numberOfShares = 0
        newStock.sellPrice = 0.0

        this.listStocks.add(newStock)

        stocksAdapter.notifyDataSetChanged()
    }

    override fun onStocksSaveButtonClicked(numberOfShares: Long, buyPrice: Double) {
    }

    override fun onStocksRemoveButtonClicked(position: Int) {
    }

}
