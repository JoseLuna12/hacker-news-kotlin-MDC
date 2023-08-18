package com.joseluna.hr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemListReciclerViewAdapter(): RecyclerView.Adapter<ItemListReciclerViewAdapter.ViewHolder>() {

    private lateinit var data: ArrayList<hacker_rank_news>
    private lateinit var listener: RecyclerViewInterface

    class ViewHolder(view: View, listener: RecyclerViewInterface): RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val pointView: TextView
        init {
            titleView = view.findViewById(R.id.item_title)
            pointView = view.findViewById(R.id.item_points)

            view.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }

        }
    }

    fun initData(d: ArrayList<hacker_rank_news>){
        data = d
    }

    private data class rowStatus(val id: String, val index: Int)
    enum class refreshStatus{
        UPDATED,
        NONE
    }

    fun setOnClickListener(l: RecyclerViewInterface) {
        listener = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleView.text = data[position].title
        holder.pointView.text = data[position].points.toString()
    }

    override fun getItemCount() = data.size

    private fun clear() {
        data.clear()
    }

    fun refresh(newData: ArrayList<hacker_rank_news>): refreshStatus {
        val removedItems = ArrayList<rowStatus>()
        val addedItems = ArrayList<rowStatus>()

        newData.forEachIndexed { index, nd ->
            if(!data.contains(nd)){
                addedItems.add(
                    rowStatus(nd.objectID, index)
                )
            }
        }

        data.forEachIndexed { index, hackerRankNews ->
            if(!newData.contains(hackerRankNews)){
                removedItems.add(
                    rowStatus(hackerRankNews.objectID, index)
                )
            }
        }

        clear()
        addAll(newData)
        addedItems.forEach {
            notifyItemInserted(it.index)
        }

        removedItems.forEach {
            notifyItemRemoved(it.index + 1)
        }


        if(removedItems.size > 0 || addedItems.size > 0){
            return refreshStatus.UPDATED
        }else {
            return refreshStatus.NONE
        }
    }

    fun addAll(newData: ArrayList<hacker_rank_news>){
        data.addAll(newData)
    }

}