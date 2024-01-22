package com.animesearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.animesearch.model.ResultModel

class SearchViewModel : ViewModel() {
  private val _resultList = MutableLiveData(listOf<ResultModel>())
  val resultList: LiveData<List<ResultModel>> = _resultList

  fun addResultItem(item: ResultModel) {
    resultList.value?.let {
      val tempList = mutableListOf<ResultModel>()
      tempList.addAll(it)
      tempList.add(item)
      _resultList.value = tempList
    }
  }

  fun clearResults() {
    _resultList.value = listOf()
  }
}