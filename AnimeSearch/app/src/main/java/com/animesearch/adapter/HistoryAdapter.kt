package com.animesearch.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.animesearch.databinding.ItemResultBinding
import com.animesearch.model.HistoryModel
import com.bumptech.glide.Glide

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ItemHolder>() {
  private var historyList = listOf<HistoryModel>()

  class ItemHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    return ItemHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun getItemCount(): Int {
    return historyList.size
  }

  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    val item = historyList[position]
    with(holder.binding) {
      itemEpisode.text = "Эпизод: ${item.episode}"
      itemTitle.text = item.filename
      Glide.with(holder.itemView.context).load(item.preview).into(itemPreview)
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  fun setList(list: List<HistoryModel>) {
    historyList = list.reversed()
    notifyDataSetChanged()
  }
}