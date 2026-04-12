package com.example.groww_mf_assignment.presentation.Explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groww_mf_assignment.Resource_Class

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllScreen(
    viewModel: ViewAllViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    val fundsState by viewModel.funds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.categoryTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = fundsState) {
                is Resource_Class.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is Resource_Class.Error -> Text(state.message ?: "Error", modifier = Modifier.align(
                    Alignment.Center))
                is Resource_Class.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.data.orEmpty()) { fund ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToDetails(fund.schemeCode) }
                                    .padding(16.dp)
                            ) {
                                Text(text = fund.schemeName, style = MaterialTheme.typography.bodyLarge)
                                Divider(modifier = Modifier.padding(top = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}