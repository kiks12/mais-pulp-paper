package com.design_project.mais_paper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import com.design_project.mais_paper.services.TimerService
import com.design_project.mais_paper.ui.theme.Mais_pulp_paperTheme
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.design_project.mais_paper.room.AppDatabase
import com.design_project.mais_paper.room.PaperCycleDao
import com.design_project.mais_paper.room.PaperCycleRepository
import com.design_project.mais_paper.websocket.AppWebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val startProcessEnabled = MutableLiveData(false)

    private lateinit var db : AppDatabase
    private lateinit var dao : PaperCycleDao
    private lateinit var repository: PaperCycleRepository
    private val startProcessFlag = MutableLiveData(false)

    private var timerService: TimerService? = null
    private var serviceBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            serviceBound = false
        }
    }

    private fun startHistoryActivity() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getInstance(applicationContext)
        dao = db.paperCycleDao()
        repository = PaperCycleRepository(dao)

        setContent {
            val startProcessEnabledState by startProcessEnabled.observeAsState(false)
            val processText by ProcessUpdate.text.observeAsState("")
            val grindingCurrentValue by ProcessUpdate.grindingCurrentValue.observeAsState()
            val grindingMaxValue by ProcessUpdate.grindingMaxValue.observeAsState()
            val boilingCurrentValue by ProcessUpdate.boilingCurrentValue.observeAsState()
            val boilingMaxValue by ProcessUpdate.boilingMaxValue.observeAsState()
            val augerCurrentValue by ProcessUpdate.augerCurrentValue.observeAsState()
            val augerMaxValue by ProcessUpdate.augerMaxValue.observeAsState()
            val pulpingCurrentValue by ProcessUpdate.pulpingCurrentValue.observeAsState()
            val pulpingMaxValue by ProcessUpdate.pulpingMaxValue.observeAsState()
            val conveyorCurrentValue by ProcessUpdate.conveyorCurrentValue.observeAsState()
            val conveyorMaxValue by ProcessUpdate.conveyorMaxValue.observeAsState()
            val dryingCurrentValue by ProcessUpdate.dryingCurrentValue.observeAsState()
            val dryingMaxValue by ProcessUpdate.dryingMaxValue.observeAsState()

            val connected by AppWebSocketManager.isConnected.observeAsState(false)
            var url by remember { mutableStateOf("") }

            Mais_pulp_paperTheme {
                NotificationPermissionRequest()
                if (connected) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = "Mais Paper") },
                                actions = {  TextButton(onClick = ::startHistoryActivity) {
                                    Text(text = "History")
                                }}
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButtonPosition = FabPosition.Center,
                        floatingActionButton = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Button(
                                    onClick = {
                                        startProcessFlag.postValue(true)
                                    },
                                    enabled = startProcessEnabledState
                                ) {
                                    Text(
                                        text = if (processText == "") "Start Process" else processText
                                    )
                                }
                                FilledTonalButton(onClick = {
                                    if (timerService?.stepManager == null) {
                                        Log.w("TimerService",  "Step Manager is Null")
                                    }
                                    timerService?.stepManager?.continueAfterUserAction()
                                }) {
                                    Text(text = "Continue with User Action")
                                }
                            }
                        }
                    ) { innerPadding ->
                        LazyVerticalGrid(
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(top = 20.dp)
                                .fillMaxSize(),
                            columns = GridCells.Fixed(2)
                        ) {
                            item {
                                ProcessProgress(name = "Grinding", progress = grindingCurrentValue?.toFloat()!! / grindingMaxValue?.toFloat()!!)
                            }
                            item {
                                ProcessProgress(name = "Boiling", progress = boilingCurrentValue?.toFloat()!! / boilingMaxValue?.toFloat()!!)
                            }
                            item {
                                ProcessProgress(name = "Auger Feeder", progress = augerCurrentValue?.toFloat()!! / augerMaxValue?.toFloat()!!)
                            }
                            item {
                                ProcessProgress(name = "Pulping", progress = pulpingCurrentValue?.toFloat()!! / pulpingMaxValue?.toFloat()!!)
                            }
                            item {
                                ProcessProgress(name = "Conveyor", progress = conveyorCurrentValue?.toFloat()!! / conveyorMaxValue?.toFloat()!!)
                            }
                            item {
                                ProcessProgress(name = "Drying", progress = dryingCurrentValue?.toFloat()!! / dryingMaxValue?.toFloat()!!)
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Text(text = "500", fontWeight = FontWeight.Black, fontSize = 80.sp)
                        Text(text = "Not connected to websocket. Please enter websocket url")
                        OutlinedTextField(
                            modifier = Modifier.padding(top = 16.dp),
                            label = { Text("Websocket Server URL") },
                            value = url,
                            onValueChange = { url = it }
                        )
                        Button(onClick = { AppWebSocketManager.init(url) }, modifier = Modifier.padding(top = 16.dp)) {
                            Text(text = "Connect")
                        }
                    }
                }
            }
        }

        startProcessFlag.observe(this) {
            if (it) {
                val intent = Intent(this, TimerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bindService(intent, connection, Context.BIND_AUTO_CREATE)
                }
            }
        }

        ProcessUpdate.doneFlag.observe(this) {
            if (!it) return@observe
            ProcessUpdate.grindingCurrentValue.postValue(0L)
            ProcessUpdate.grindingMaxValue.postValue(0L)
            ProcessUpdate.boilingCurrentValue.postValue(0L)
            ProcessUpdate.boilingMaxValue.postValue(0L)
            ProcessUpdate.augerCurrentValue.postValue(0L)
            ProcessUpdate.augerMaxValue.postValue(0L)
            ProcessUpdate.pulpingCurrentValue.postValue(0L)
            ProcessUpdate.pulpingMaxValue.postValue(0L)
            ProcessUpdate.conveyorCurrentValue.postValue(0L)
            ProcessUpdate.conveyorMaxValue.postValue(0L)
            ProcessUpdate.dryingCurrentValue.postValue(0L)
            ProcessUpdate.dryingMaxValue.postValue(0L)
            ProcessUpdate.text.postValue("Start Process")
            startProcessFlag.postValue(false)
            startProcessEnabled.postValue(false)

            lifecycleScope.launch(Dispatchers.IO) {
                repository.incrementTodayCycle()
            }
        }

        AppWebSocketManager.incomingMessage.observe(this) {
            when (it) {
                "ir-data: true" -> {
                    startProcessEnabled.postValue(true)
                }
                "ir-data: false" -> {
                    startProcessEnabled.postValue(false)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(connection)
            serviceBound = false
        }
    }
}
