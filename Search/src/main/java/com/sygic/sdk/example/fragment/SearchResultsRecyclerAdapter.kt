package com.sygic.sdk.example.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sygic.sdk.example.databinding.LayoutSearchResultItemBinding
import com.sygic.sdk.search.AutocompleteResult

class SearchResultsRecyclerAdapter(private val onItemClick: (AutocompleteResult) -> Unit) :
    RecyclerView.Adapter<SearchResultsRecyclerAdapter.ViewHolder>() {

    private val values: MutableList<AutocompleteResult> = mutableListOf()

    fun setData(newValues: List<AutocompleteResult>) {
        values.clear()
        values.addAll(newValues)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutSearchResultItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: LayoutSearchResultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.title
        val subtitle: TextView = binding.subtitle
    }
}
