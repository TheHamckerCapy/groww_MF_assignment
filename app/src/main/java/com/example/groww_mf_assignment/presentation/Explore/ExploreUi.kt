package com.example.groww_mf_assignment.presentation.Explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.local.ExploreCacheEntity

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    onNavigateToViewAll: (String, String) -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val indexFunds by viewModel.indexFunds.collectAsState()
    val bluechipFunds by viewModel.bluechipFunds.collectAsState()
    val taxSaverFunds by viewModel.taxSaverFunds.collectAsState()
    val largeCapFunds by viewModel.largeCapFunds.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CategorySection(
            title = "Index Funds",
            resource = indexFunds,
            onViewAllClick = { onNavigateToViewAll("Index Funds", "index") },
            onItemClick = onNavigateToDetails
        )
        CategorySection(
            title = "Bluechip Funds",
            resource = bluechipFunds,
            onViewAllClick = { onNavigateToViewAll("Bluechip Funds", "bluechip") },
            onItemClick = onNavigateToDetails
        )
        CategorySection(
            title = "Tax Saver",
            resource = taxSaverFunds,
            onViewAllClick = { onNavigateToViewAll("Tax Saver", "elss") },
            onItemClick = onNavigateToDetails
        )
        CategorySection(
            title = "Large Cap",
            resource = largeCapFunds,
            onViewAllClick = { onNavigateToViewAll("Large Cap Funds", "large cap") },
            onItemClick = onNavigateToDetails
        )
    }
}

@Composable
fun CategorySection(
    title: String,
    resource: Resource_Class<List<ExploreCacheEntity>>,
    onViewAllClick: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onViewAllClick) {
                Text("View All")
            }
        }

        when (resource) {
            is Resource_Class.Loading -> {

                if (resource.data.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    FundGrid(funds = resource.data, onItemClick = onItemClick)
                }
            }

            is Resource_Class.Error -> {
                if (!resource.data.isNullOrEmpty()) {

                    FundGrid(funds = resource.data, onItemClick = onItemClick)
                } else {
                    Text(
                        text = resource.message ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            is Resource_Class.Success -> {
                if (resource.data.isNullOrEmpty()) {
                    Text(
                        text = "No funds available",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    FundGrid(funds = resource.data, onItemClick = onItemClick)
                }
            }
        }
    }
}

@Composable
fun FundGrid(
    funds: List<ExploreCacheEntity>,
    onItemClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(350.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false // Parent column handles scrolling
    ) {
        items(funds) { fund ->
            FundCard(fund = fund, onClick = onItemClick)
        }
    }
}

@Composable
fun FundCard(
    fund: ExploreCacheEntity,
    onClick: (Int) -> Unit
) {
    val darkCharcoal = Color(0xFF1A1E25)
    val accentGreen = Color(0xFF00C781)

    val parsedName = remember(fund.schemeName) {
        parseMutualFundName(fund.schemeName)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = darkCharcoal.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth()
            .clickable { onClick(fund.schemeCode) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = parsedName.mainName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    parsedName.planInfo?.let { plan ->
                        Text(
                            text = plan,
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    parsedName.optionType?.let { option ->
                        Box(
                            modifier = Modifier
                                .background(accentGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = option,
                                color = accentGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }


                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "NAV",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (fund.latestNav != null)
                            "₹${"%.2f".format(fund.latestNav.toDoubleOrNull() ?: 0.0)}"
                        else "---",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}