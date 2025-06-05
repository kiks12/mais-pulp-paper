package com.design_project.mais_paper.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PaperCycleDao {

    @Query("SELECT * FROM paper_cycles ORDER BY date ASC")
    suspend fun getAllCycles(): List<PaperCycle>

    @Query("SELECT * FROM paper_cycles WHERE date = :date")
    suspend fun getCycleByDate(date: String): PaperCycle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cycle: PaperCycle)
}
