package com.example.myapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.SmallHeadingStyle
import com.example.myapplication.ui.theme.White
import com.example.myapplication.ui.theme.Yellow


@Composable
fun  JetLaggedHeaderTabs(
    onTabSelected: (WeekTab) -> Unit,
    selectedTab: WeekTab,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        modifier = modifier,
        edgePadding = 12.dp,
        selectedTabIndex = selectedTab.ordinal,
        containerColor = White,
        indicator = { tabPositions: List<TabPosition> ->
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
                    .fillMaxSize()
                    .padding(horizontal = 2.dp)
                    //.border(BorderStroke(2.dp, Yellow), RoundedCornerShape(10.dp))
            )
        },
        divider = { }
    ) {
        WeekTab.values().forEachIndexed { index, weekTab ->
            val selected = index == selectedTab.ordinal
            DDLTabText(
                weekTab = weekTab,
                selected = selected,
                onTabSelected = onTabSelected,
                index = index
            )
        }
    }
}

@Composable
fun  JetLaggedHeaderTabs(
    onTabSelected: (MoonTab) -> Unit,
    selectedTab: MoonTab,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        modifier = modifier,
        edgePadding = 12.dp,
        selectedTabIndex = selectedTab.ordinal,
        containerColor = White,
        indicator = { tabPositions: List<TabPosition> ->
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
                    .fillMaxSize()
                    .padding(horizontal = 2.dp)
                    .border(BorderStroke(2.dp, Yellow), RoundedCornerShape(10.dp))
            )
        },
        divider = { }
    ) {
        MoonTab.values().forEachIndexed { index, weekTab ->
            val selected = index == selectedTab.ordinal
            DDLTabText(
                weekTab = weekTab,
                selected = selected,
                onTabSelected = onTabSelected,
                index = index
            )
        }
    }
}

private val textModifier = Modifier
    .padding(vertical = 6.dp, horizontal = 4.dp)
@Composable
private fun DDLTabText(
    weekTab: WeekTab,
    selected: Boolean,
    index: Int,
    onTabSelected: (WeekTab) -> Unit,
) {
    Tab(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(16.dp)),
        selected = selected,
        unselectedContentColor = Color.Black,
        selectedContentColor = Color.Black,
        onClick = {
            onTabSelected(WeekTab.values()[index])
        }
    ) {
        Text(
            modifier = textModifier,
            text = stringResource(id = weekTab.title),
            style = SmallHeadingStyle
        )
    }
}

@Composable
private fun DDLTabText(
    weekTab: MoonTab,
    selected: Boolean,
    index: Int,
    onTabSelected: (MoonTab) -> Unit,
) {
    Tab(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(16.dp)),
        selected = selected,
        unselectedContentColor = Color.Black,
        selectedContentColor = Color.Black,
        onClick = {
            onTabSelected(MoonTab.values()[index])
        }
    ) {
        Text(
            modifier = textModifier,
            text = weekTab.name.toString(),
            style = SmallHeadingStyle
        )
    }
}