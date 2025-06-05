package com.design_project.mais_paper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.design_project.mais_paper.ui.theme.Mais_pulp_paperTheme

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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