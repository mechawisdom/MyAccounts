package com.mkdevelopment.myaccounts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.utils.DisclaimerItem

class LegalItemAdapter(private val legalItemList: List<DisclaimerItem>) :
    RecyclerView.Adapter<LegalItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.disclaimer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = legalItemList[position]
        holder.numberText.text = currentItem.id.toString()
        holder.contentText.text = currentItem.content
    }

    override fun getItemCount(): Int {
        return legalItemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var numberText: TextView
        var contentText: TextView

        init {
            numberText = itemView.findViewById(R.id.number_text)
            contentText = itemView.findViewById(R.id.content_text)
        }
    }
}
