package com.design_project.mais_paper.room


import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class PaperCycleRepository(private val dao: PaperCycleDao) {

    // Get all cycles sorted by date
    suspend fun getAllCycles(): List<PaperCycle> = withContext(Dispatchers.IO) {
        dao.getAllCycles()
    }

    // Increment paper count for today
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun incrementTodayCycle() = withContext(Dispatchers.IO) {
        val today = LocalDate.now().toString()
        val existing = dao.getCycleByDate(today)
        val updated = if (existing != null) {
            existing.copy(count = existing.count + 1)
        } else {
            PaperCycle(today, 1)
        }
        dao.insertOrUpdate(updated)
    }

    // Get today's cycle (optional, useful for UI display)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodayCycle(): PaperCycle? = withContext(Dispatchers.IO) {
        dao.getCycleByDate(LocalDate.now().toString())
    }
}
