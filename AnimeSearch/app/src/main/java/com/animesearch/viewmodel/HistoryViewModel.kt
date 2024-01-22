package com.animesearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.animesearch.model.HistoryModel

class HistoryViewModel : ViewModel() {
  private val _historyList = MutableLiveData(listOf<HistoryModel>())
  val historyList: LiveData<List<HistoryModel>> = _historyList

  fun addHistoryItem(item: HistoryModel) {
    historyList.value?.let {
      val tempList = mutableListOf<HistoryModel>()
      tempList.addAll(it)
      tempList.add(item)
      _historyList.value = tempList
    }
  }
}