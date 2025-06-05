package com.design_project.mais_paper.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paper_cycles")
data class PaperCycle(
    @PrimaryKey val date: String, // Format: YYYY-MM-DD
    val count: Int = 1
)
