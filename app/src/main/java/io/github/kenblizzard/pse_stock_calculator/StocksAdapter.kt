package io.github.kenblizzard.pse_stock_calculator

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import kotlinx.android.synthetic.main.fragment_stock_add_button.view.*


class StocksAdapter() : RecyclerView.Adapter<ViewHolder>() {

    class StockFragmentHolder : ViewHolder {

        lateinit var numberOfShares: TextView;
        lateinit var buyPrice: TextView;

        constructor(view: View) : super(view) {
            numberOfShares = view.findViewById(R.id.stockNumberOfShares)
            buyPrice = view.findViewById(R.id.stockBuyPrice)
        }
    }

    class StockAddButtonHolder : ViewHolder {
        lateinit var btnStockAverageAdd : Button

        constructor(view: View) : super(view) {
            btnStockAverageAdd = view.findViewById(R.id.stockAverageAdd)
        }
    }


    lateinit var onStocksAdapterListener : OnStocksAdapterListener;
    lateinit var listStocks: ArrayList<Stock>

    public constructor(listStocks: List<Stock>, onStocksAdapterListener : OnStocksAdapterListener ) : this() {
        this.listStocks = listStocks as ArrayList<Stock>
        this.onStocksAdapterListener = onStocksAdapterListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var itemView = LayoutInflater.from(parent?.getContext())
                .inflate(viewType, parent, false)

        if (viewType == R.layout.fragment_stock) {
            return StockFragmentHolder(itemView)
        } else {
            return StockAddButtonHolder(itemView)
        }
    }

    override fun getItemViewType(position: Int): Int {

        if (position != itemCount - 1) {
            return R.layout.fragment_stock
        } else {
            return R.layout.fragment_stock_add_button
        }
    }


    override fun getItemCount(): Int {
        return listStocks.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        var stock: Stock = listStocks.get(position)

        if (holder is StockFragmentHolder) {
            holder?.buyPrice?.text = stock.buyPrice.toString()
            holder?.numberOfShares?.text = stock.numberOfShares.toString()
        } else if(holder is StockAddButtonHolder) {
            holder.btnStockAverageAdd.setOnClickListener(View.OnClickListener {
                this.onStocksAdapterListener.onStocksAddButtonClicked();
            })
        }
    }


    public interface OnStocksAdapterListener {
        public fun onStocksAddButtonClicked ()

        public fun onStocksSaveButtonClicked (numberOfShares : Long, buyPrice : Double)

        public fun onStocksRemoveButtonClicked (position: Int)
    }
}