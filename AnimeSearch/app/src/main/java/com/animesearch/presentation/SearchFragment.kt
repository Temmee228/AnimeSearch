package com.animesearch.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.animesearch.adapter.ResultsAdapter
import com.animesearch.database.AppDatabase
import com.animesearch.databinding.FragmentSearchBinding
import com.animesearch.model.HistoryModel
import com.animesearch.model.ResultModel
import com.animesearch.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream

class SearchFragment : Fragment() {
  private val binding by lazy { FragmentSearchBinding.inflate(layoutInflater) }
  private val pickPhoto =
    registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      uri?.let {
        sendImage(uri)
      }
    }
  private val searchViewModel: SearchViewModel by viewModels()
  private val resultsAdapter by lazy { ResultsAdapter() }
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
    initRecyclerView()
    initListeners()
  }

  private fun initRecyclerView() {
    binding.resultsRv.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = resultsAdapter
    }
    searchViewModel.resultList.observe(viewLifecycleOwner) { list ->
      resultsAdapter.setList(list)
    }
  }

  private fun initListeners() {
    binding.btnPhoto.setOnClickListener {
      pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
  }

  private fun sendImage(uri: Uri) {
    binding.btnPhoto.isEnabled = false
    val contentResolver = requireContext().contentResolver
    val inputStream = contentResolver.openInputStream(uri)
    if (inputStream != null) {
      lifecycleScope.launch(Dispatchers.IO) {
        try {
          val file = saveFileFromInputStream(inputStream, "image.jpg")
          val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
            "file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
          ).build()
          val request =
            Request.Builder().url("https://api.trace.moe/search").post(requestBody).build()
          val client = OkHttpClient()
          val response = client.newCall(request).execute()
          val responseBody = response.body?.string()
          withContext(Dispatchers.Main) {
            handleResponseBody(responseBody)
          }
        } catch (e: IOException) {
          binding.btnPhoto.isEnabled = true
          Log.e("log1", "Error: ${e.message}")
        }
      }
    } else {
      binding.btnPhoto.isEnabled = true
      Toast.makeText(requireContext(), "Попробуйте еще раз", Toast.LENGTH_SHORT).show()
    }
  }

  private fun handleResponseBody(responseBody: String?) {
    if (responseBody != null) {
      val resultsArray = JSONObject(responseBody).getJSONArray("result")
      searchViewModel.clearResults()
      for (index in 0 until resultsArray.length()) {
        val result = resultsArray.getJSONObject(index)
        val fileName = result.optString("filename")
        val episode = result.optString("episode")
        val image = result.optString("image")
        val newItem = ResultModel(preview = image, filename = fileName, episode = episode)
        searchViewModel.addResultItem(newItem)
        if ((searchViewModel.resultList.value ?: listOf()).size == 1)
          break
      }
      saveResultToDatabase()
    } else {
      binding.btnPhoto.isEnabled = true
      Toast.makeText(requireContext(), "Нет результатов", Toast.LENGTH_SHORT).show()
    }
  }

  private fun saveResultToDatabase() {
    val resultList = searchViewModel.resultList.value ?: listOf()
    if (resultList.isNotEmpty()) {
      lifecycleScope.launch(Dispatchers.IO) {
        resultList.forEach { item ->
          val historyItem =
            HistoryModel(filename = item.filename, preview = item.preview, episode = item.episode)
          database.insertHistory(historyItem)
        }
        withContext(Dispatchers.Main) {
          binding.btnPhoto.isEnabled = true
        }
      }
    } else {
      binding.btnPhoto.isEnabled = true
    }
  }

  private fun saveFileFromInputStream(inputStream: InputStream, fileName: String): File {
    val file = File(requireContext().cacheDir, fileName)
    file.outputStream().use { output ->
      inputStream.copyTo(output)
    }
    return file
  }
}