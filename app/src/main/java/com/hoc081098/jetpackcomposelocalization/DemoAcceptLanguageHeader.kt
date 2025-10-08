package com.hoc081098.jetpackcomposelocalization

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hoc081098.jetpackcomposelocalization.data.NetworkServiceLocator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
internal sealed interface DemoAcceptLanguageUiState {
  data object Idle : DemoAcceptLanguageUiState
  data object Loading : DemoAcceptLanguageUiState
  data class Success(val data: Map<String, Any>) : DemoAcceptLanguageUiState
  data class Error(val message: String?) : DemoAcceptLanguageUiState
}

internal class DemoAcceptLanguageViewModel : ViewModel() {
  private val apiService get() = NetworkServiceLocator.apiService

  private val _uiState = MutableStateFlow<DemoAcceptLanguageUiState>(DemoAcceptLanguageUiState.Idle)
  val uiState: StateFlow<DemoAcceptLanguageUiState> = _uiState.asStateFlow()

  private var job: Job? = null

  fun get() {
    job?.cancel()
    job = viewModelScope.launch {
      _uiState.value = DemoAcceptLanguageUiState.Loading
      try {
        val response = apiService.getLocalizedData()
        _uiState.value = DemoAcceptLanguageUiState.Success(response)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e("DemoAcceptLanguageVM", "Error while fetching data", e)
        _uiState.value = DemoAcceptLanguageUiState.Error(e.message)
      }
    }
  }

  fun reset() {
    job?.cancel()
    _uiState.value = DemoAcceptLanguageUiState.Idle
  }
}

@Composable
internal fun DemoAcceptLanguageHeader(
  modifier: Modifier = Modifier,
  viewModel: DemoAcceptLanguageViewModel = viewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = viewModel::get) { Text("GET") }
      OutlinedButton(onClick = viewModel::reset) { Text("Reset") }
    }

    Spacer(modifier = Modifier.height(8.dp))

    when (val currentState = state) {
      DemoAcceptLanguageUiState.Idle ->
        Text(text = "Press GET to call httpbin.org/get")

      DemoAcceptLanguageUiState.Loading ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CircularProgressIndicator()
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Loading…")
        }

      is DemoAcceptLanguageUiState.Success -> {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(text = "Response success", style = MaterialTheme.typography.titleSmall)
          Text(text = "Response: ${currentState.data}")
        }
      }

      is DemoAcceptLanguageUiState.Error ->
        Text(
          text = "Error: ${currentState.message ?: "unknown"}",
          color = MaterialTheme.colorScheme.error,
        )
    }
  }
}