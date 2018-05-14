package io.github.kenblizzard.pse_stock_calculator

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.github.kenblizzard.pse_stock_calculator.model.Stock
import io.github.kenblizzard.pse_stock_calculator.service.StocksCalculator
import io.github.kenblizzard.pse_stock_calculator.util.Constants


class StocksAdapter() : RecyclerView.Adapter<ViewHolder>() {


    class StockFragmentHolder : ViewHolder {
        fun Double.format(digits: Int) = java.lang.String.format("%,.${digits}f", this)


        lateinit var numberOfShares: EditText;
        lateinit var buyPrice: EditText;
        lateinit var btnCompute: Button
        lateinit var btnRemove: Button
        lateinit var stockAveragePrice: TextView
        lateinit var stockTotalAmount: TextView


        constructor(view: View) : super(view) {
            numberOfShares = view.findViewById(R.id.stockNumberOfShares)
            buyPrice = view.findViewById(R.id.stockBuyPrice)
            btnCompute = view.findViewById(R.id.btnCompute)
            btnRemove = view.findViewById(R.id.btnRemove)
            stockAveragePrice = view.findViewById(R.id.stockAveragePrice)
            stockTotalAmount = view.findViewById(R.id.stockTotalAmount)
        }


        fun populateView(stock: Stock, onStocksAdapterListener: OnStocksAdapterListener, position: Int) {


            this.buyPrice.setText(if (stock!!.buyPrice <= 0.0) "" else stock!!.buyPrice.toString())
            this.numberOfShares?.setText(if (stock!!.numberOfShares <= 0.0) "" else stock!!.numberOfShares.toString())
            this.stockAveragePrice.text = "0.0"
            this.stockTotalAmount.text = "0.0"

            this.btnCompute.setOnClickListener(View.OnClickListener {

                var stock = Stock();

                stock.numberOfShares = this.numberOfShares.text.toString().toLongOrNull()
                stock.buyPrice = this.buyPrice?.text.toString().toDoubleOrNull()
                stock.sellPrice = 0.0


                if (stock.numberOfShares != null || stock.numberOfShares != 0L) {

                    var buyTotalAmount: Double = 0.0;
                    buyTotalAmount = StocksCalculator.calculateTotalSharesPrice(stock, StocksCalculator.TRANSACTION_FEE_BASE_VALUES, Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY)

                    var averagePricePerShare = (buyTotalAmount / stock.numberOfShares)

                    this.stockAveragePrice.text = averagePricePerShare.format(2);
                    this.stockTotalAmount.text = "" + buyTotalAmount.format(2)

                    onStocksAdapterListener.onStocksComputeButtonClicked(stock.numberOfShares, averagePricePerShare, buyTotalAmount, position)
                }
            })

            this.btnRemove.setOnClickListener(View.OnClickListener {
                onStocksAdapterListener.onStocksRemoveButtonClicked(position)
            })

        }
    }

    class StockAddButtonHolder : ViewHolder {
        lateinit var btnStockAverageAdd: Button

        constructor(view: View) : super(view) {
            btnStockAverageAdd = view.findViewById(R.id.stockAverageAdd)
        }

        fun attachInterface(onStocksAdapterListener: OnStocksAdapterListener) {
            this.btnStockAverageAdd.setOnClickListener(View.OnClickListener {
                onStocksAdapterListener.onStocksAddButtonClicked();
            })
        }
    }


    lateinit var onStocksAdapterListener: OnStocksAdapterListener;
    lateinit var listStocks: ArrayList<Stock>

    public constructor(listStocks: ArrayList<Stock>, onStocksAdapterListener: OnStocksAdapterListener) : this() {
        this.listStocks = listStocks
        this.onStocksAdapterListener = onStocksAdapterListener
        setHasStableIds(true)

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var itemView = LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.fragment_stock, parent, false)

//        if (viewType == R.layout.fragment_stock) {
        return StockFragmentHolder(itemView)
//        } else {
//
//
//            MobileAds.initialize(itemView.context, "ca-app-pub-1200631640695401~4382253594");
//
//
//            val adRequest = AdRequest.Builder().build()
//
//            itemView.multipleBuyAdView.loadAd(adRequest)
//
//
//            itemView.multipleBuyAdView.setAdListener(object : AdListener() {
//
//                override fun onAdLoaded() {
//                    itemView.multipleBuyAdView.setVisibility(View.VISIBLE)
//                }
//
//                override fun onAdFailedToLoad(error: Int) {
//                    itemView.multipleBuyAdView.setVisibility(View.GONE)
//                }
//
//            })
//            return StockAddButtonHolder(itemView)
//        }
    }

//    override fun getItemViewType(position: Int): Int {
//
////        if (position != itemCount - 1) {
////            return R.layout.fragment_stock
////        } else {
////            return R.layout.fragment_stock_add_button
////        }
//        return position
//    }

    override fun getItemId(position: Int): Long {
        Log.d("POSITIOOOOOOOOOON " , ""+ position + " !!!" + super.getItemId(position) + " @@@ " + this.listStocks.get(position).id)
        return this.listStocks.get(position).id
    }


    override fun getItemCount(): Int {
        return this.listStocks.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        var stock: Stock = listStocks.get(position)

        if (holder is StockFragmentHolder) {
            holder.populateView(stock, this.onStocksAdapterListener, position)

        }
// else if (holder is StockAddButtonHolder) {
//            holder.attachInterface(this.onStocksAdapterListener)
//        }
    }


    public interface OnStocksAdapterListener {
        public fun onStocksAddButtonClicked()

        public fun onStocksComputeButtonClicked(numberOfShares: Long?, averagePricePerShare: Double?, averageTotalAmount: Double?, position: Int)

        public fun onStocksRemoveButtonClicked(position: Int)
    }
}