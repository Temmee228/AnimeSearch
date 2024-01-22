package com.animesearch.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.animesearch.R
import com.animesearch.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
  private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initialize()
  }

  private fun initialize() {
    initListeners()
  }

  private fun initListeners() {
    binding.btnSearch.setOnClickListener {
      findNavController().navigate(R.id.searchFragment)
    }
    binding.btnHistory.setOnClickListener {
      findNavController().navigate(R.id.historyFragment)
    }
  }
}