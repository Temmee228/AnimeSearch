package com.animesearch.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.animesearch.databinding.ItemResultBinding
import com.animesearch.model.ResultModel
import com.bumptech.glide.Glide

class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.ItemHolder>() {
  private var resultsList = listOf<ResultModel>()

  class ItemHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    return ItemHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun getItemCount(): Int {
    return resultsList.size
  }

  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    val item = resultsList[position]
    with(holder.binding) {
      itemEpisode.text = "Эпизод: ${item.episode}"
      itemTitle.text = item.filename
      Glide.with(holder.itemView.context).load(item.preview).into(itemPreview)
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  fun setList(list: List<ResultModel>) {
    resultsList = list
    notifyDataSetChanged()
  }
}