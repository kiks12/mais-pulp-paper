package com.design_project.mais_paper

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.design_project.mais_paper.chart.PaperCycleLineChart
import com.design_project.mais_paper.room.AppDatabase
import com.design_project.mais_paper.room.PaperCycle
import com.design_project.mais_paper.room.PaperCycleDao
import com.design_project.mais_paper.room.PaperCycleRepository
import com.design_project.mais_paper.ui.theme.Mais_pulp_paperTheme
import java.time.LocalDate

class HistoryActivity : ComponentActivity() {

    private lateinit var db : AppDatabase
    private lateinit var dao : PaperCycleDao
    private lateinit var repository: PaperCycleRepository

    companion object {
        const val TAG = "HistoryActivity"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getInstance(applicationContext)
        dao = db.paperCycleDao()
        repository = PaperCycleRepository(dao)

        setContent {
            var list by remember { mutableStateOf<List<PaperCycle>>(emptyList()) }
            var cyclesToday by remember { mutableStateOf<PaperCycle?>(null) }

            LaunchedEffect(true) {
                list = repository.getAllCycles()
                cyclesToday = repository.getTodayCycle()
                Log.w(TAG, list.toString())
                Log.w(TAG, cyclesToday.toString())
            }

            Mais_pulp_paperTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "History") },
                            navigationIcon = { IconButton(onClick = ::finish) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                                }
                            }
                        )
                    }
                ){ innerPadding  ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        ElevatedCard(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "Date: ${LocalDate.now()}")
                                Row(
                                    modifier = Modifier.padding(top=8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ){
                                    Text(text = "Count: ", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                                    Text(text = if (cyclesToday == null) "0" else "${cyclesToday?.count} papers", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ){
                            PaperCycleLineChart(paperCycles = list)
                        }
                    }
                }
            }
        }
    }
}