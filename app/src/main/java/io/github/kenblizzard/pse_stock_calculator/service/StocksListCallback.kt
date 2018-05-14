package io.github.kenblizzard.pse_stock_calculator.service

import android.support.v7.util.DiffUtil
import io.github.kenblizzard.pse_stock_calculator.model.Stock

class StocksListCallback : DiffUtil.Callback() {

    private var oldItems: List<Stock>? = null
    private var newItems: List<Stock>? = null

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems!!.size == newItems!!.size
    }

    override fun getOldListSize(): Int {
        return oldItems!!.size
    }

    override fun getNewListSize(): Int {
        return newItems!!.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems!!.size == newItems!!.size

    }

    fun setOldItems(oldItems: List<Stock>) {
        this.oldItems = oldItems
    }

    fun setNewItems(newItems: List<Stock>) {
        this.newItems = newItems
    }

    fun getOldItems(): List<Stock> {
        return this.oldItems!!
    }

    fun getNewItems(): List<Stock> {
        return this.newItems!!
    }


}