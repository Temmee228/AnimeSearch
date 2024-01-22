package com.animesearch.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.animesearch.adapter.HistoryAdapter
import com.animesearch.database.AppDatabase
import com.animesearch.databinding.FragmentHistoryBinding
import com.animesearch.viewmodel.HistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {
  private val binding by lazy { FragmentHistoryBinding.inflate(layoutInflater) }
  private val historyViewModel: HistoryViewModel by viewModels()
  private val historyAdapter by lazy { HistoryAdapter() }
  private val database by lazy { AppDatabase.getInstance(requireContext()).databaseDao() }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initialize()
  }

  private fun initialize() {
    getHistory()
    initRecyclerView()
    initListeners()
  }

  private fun initListeners() {
    binding.btnBack.setOnClickListener {
      findNavController().navigateUp()
    }
  }

  private fun getHistory() {
    lifecycleScope.launch(Dispatchers.IO) {
      val history = database.getHistory()
      withContext(Dispatchers.Main) {
        history.forEach { item ->
          historyViewModel.addHistoryItem(item)
        }
      }
    }
  }

  private fun initRecyclerView() {
    binding.historyRv.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = historyAdapter
    }
    historyViewModel.historyList.observe(viewLifecycleOwner) { list ->
      historyAdapter.setList(list)
    }
  }
}