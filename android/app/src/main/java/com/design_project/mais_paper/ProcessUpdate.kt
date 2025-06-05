package com.design_project.mais_paper

import androidx.lifecycle.MutableLiveData

object ProcessUpdate {
    val text = MutableLiveData("")
    val doneFlag = MutableLiveData(false)
    val grindingCurrentValue = MutableLiveData(0L)
    val grindingMaxValue = MutableLiveData(0L)
    val boilingCurrentValue = MutableLiveData(0L)
    val boilingMaxValue = MutableLiveData(0L)
    val augerCurrentValue = MutableLiveData(0L)
    val augerMaxValue = MutableLiveData(0L)
    val pulpingCurrentValue = MutableLiveData(0L)
    val pulpingMaxValue = MutableLiveData(0L)
    val conveyorCurrentValue = MutableLiveData(0L)
    val conveyorMaxValue = MutableLiveData(0L)
    val dryingCurrentValue = MutableLiveData(0L)
    val dryingMaxValue = MutableLiveData(0L)
    val current = MutableLiveData(0L)
    val maxValue = MutableLiveData(0L)
}