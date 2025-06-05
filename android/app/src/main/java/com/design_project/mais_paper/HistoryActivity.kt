package com.design_project.mais_paper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.design_project.mais_paper.room.AppDatabase
import com.design_project.mais_paper.room.PaperCycleDao
import com.design_project.mais_paper.room.PaperCycleRepository
import com.design_project.mais_paper.ui.theme.Mais_pulp_paperTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : ComponentActivity() {

    private lateinit var db : AppDatabase
    private lateinit var dao : PaperCycleDao
    private lateinit var repository: PaperCycleRepository

    companion object {
        const val TAG = "HistoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getInstance(applicationContext)
        dao = db.paperCycleDao()
        repository = PaperCycleRepository(dao)

        lifecycleScope.launch(Dispatchers.IO) {
            val list = repository.getAllCycles()
            Log.w(TAG, list.toString())
        }

        setContent {
            Mais_pulp_paperTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ){ innerPadding  ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        Text(text = "History")
                    }
                }
            }
        }
    }
}