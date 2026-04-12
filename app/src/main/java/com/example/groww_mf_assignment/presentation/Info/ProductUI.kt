package com.example.groww_mf_assignment.presentation.Info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groww_mf_assignment.Resource_Class
import com.example.groww_mf_assignment.data.remote.dto.FundDetailsResponseDto
import com.example.groww_mf_assignment.data.remote.dto.NavDataDto
import com.example.groww_mf_assignment.presentation.WatchList.WatchlistBottomSheet
import kotlin.math.roundToInt


enum class ChartRange(val label: String) {
    SIX_MONTHS("6M"),
    ONE_YEAR("1Y"),
    ALL("All")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val fundDetailsState by viewModel.fundDetails.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fund Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showBottomSheet = true },
                        enabled = fundDetailsState is Resource_Class.Success
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) "In Watchlist" else "Add to Watchlist"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = fundDetailsState) {
                is Resource_Class.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource_Class.Error -> {
                    Text(
                        text = state.message ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is Resource_Class.Success -> {
                    state.data?.let { details ->
                        FundDetailContent(details = details)
                    }
                }
            }
        }
    }


    if (showBottomSheet) {
        val details = (fundDetailsState as? Resource_Class.Success)?.data
        if (details != null) {
            WatchlistBottomSheet(
                schemeCode = details.meta.schemeCode,
                schemeName = details.meta.schemeName,
                amcName = details.meta.fundHouse,
                latestNav = details.data.firstOrNull()?.nav ?: "0.0",
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}
@Composable
fun FundDetailContent(details: FundDetailsResponseDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = details.meta.fundHouse,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))


        Text(
            text = details.meta.schemeName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))


        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = details.meta.schemeCategory,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Latest NAV",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "₹${details.data.firstOrNull()?.nav ?: "N/A"}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))


        Text(
            text = "NAV History",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        NavLineChart(navData = details.data)
    }
}


@Composable
fun NavLineChart(navData: List<NavDataDto>) {
    if (navData.isEmpty()) return

    var selectedRange by remember { mutableStateOf(ChartRange.ONE_YEAR) }
    var brushIndex by remember { mutableStateOf<Int?>(null) }


    val filteredData = remember(navData, selectedRange) {
        val reversed = navData.reversed() // oldest → newest
        when (selectedRange) {
            ChartRange.SIX_MONTHS -> reversed.takeLast(180)
            ChartRange.ONE_YEAR   -> reversed.takeLast(365)
            ChartRange.ALL        -> reversed
        }
    }


    val chartData = remember(filteredData) {
        val target = 300
        val step = (filteredData.size / target).coerceAtLeast(1)
        filteredData
            .filterIndexed { i, _ -> i % step == 0 }
            .mapNotNull { dto ->
                dto.nav.toFloatOrNull()?.let { Pair(dto.date, it) }
            }
    }

    if (chartData.isEmpty()) return

    val navValues = chartData.map { it.second }
    val maxNav = navValues.maxOrNull() ?: 0f
    val minNav = navValues.minOrNull() ?: 0f
    val isPositive = (navValues.lastOrNull() ?: 0f) >= (navValues.firstOrNull() ?: 0f)
    val lineColor = if (isPositive) Color(0xFF1B8A3E) else Color(0xFFD32F2F)


    val brushedPoint = brushIndex?.let { chartData.getOrNull(it) }

    Column(modifier = Modifier.fillMaxWidth()) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (brushedPoint != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = brushedPoint.first,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${"%.4f".format(brushedPoint.second)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = lineColor
                    )
                }
            } else {
                Text(
                    text = "Touch chart to inspect",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }


        val chartDataState = rememberUpdatedState(chartData)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(chartData) {
                    detectHorizontalDragGestures(
                        onDragEnd = { brushIndex = null },
                        onDragCancel = { brushIndex = null }
                    ) { change, _ ->
                        val data = chartDataState.value
                        if (data.isEmpty()) return@detectHorizontalDragGestures
                        val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        brushIndex = (fraction * (data.size - 1)).roundToInt()
                    }
                }
                .pointerInput(chartData) {
                    detectTapGestures(
                        onPress = { offset ->
                            val data = chartDataState.value
                            if (data.isEmpty()) return@detectTapGestures
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            brushIndex = (fraction * (data.size - 1)).roundToInt()
                            tryAwaitRelease()
                            brushIndex = null
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val xStep = width / (chartData.size - 1).coerceAtLeast(1)
            val range = (maxNav - minNav).coerceAtLeast(0.01f)

            fun navToY(nav: Float) = (1f - (nav - minNav) / range) * height
            fun indexToX(i: Int) = i * xStep

            val points = chartData.mapIndexed { i, (_, nav) ->
                Offset(indexToX(i), navToY(nav))
            }


            val fillPath = Path().apply {
                moveTo(points.first().x, height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.2f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )


            val linePath = Path().apply {
                points.forEachIndexed { i, p ->
                    if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
            }
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )


            repeat(5) { i ->
                val y = height * i / 4f
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.35f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
            }


            brushIndex?.let { idx ->
                val point = points.getOrNull(idx) ?: return@let


                drawLine(
                    color = lineColor.copy(alpha = 0.7f),
                    start = Offset(point.x, 0f),
                    end = Offset(point.x, height),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )


                drawCircle(
                    color = lineColor.copy(alpha = 0.2f),
                    radius = 12f,
                    center = point
                )

                drawCircle(
                    color = lineColor,
                    radius = 6f,
                    center = point
                )

                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = point
                )
            }
        }


        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Low  ₹${"%.2f".format(minNav)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFD32F2F)
            )
            Text(
                text = "High  ₹${"%.2f".format(maxNav)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1B8A3E)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            ChartRange.entries.forEach { range ->
                val selected = range == selectedRange
                Surface(
                    onClick = { selectedRange = range },
                    shape = MaterialTheme.shapes.small,
                    color = if (selected) lineColor else Color.Transparent,
                    border = if (!selected) BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ) else null,
                    modifier = Modifier.width(56.dp)
                ) {
                    Text(
                        text = range.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}