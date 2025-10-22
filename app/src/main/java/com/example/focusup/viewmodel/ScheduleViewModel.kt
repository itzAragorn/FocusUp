package com.example.focusup.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.focusup.data.db.DatabaseProvider
import com.example.focusup.data.model.ScheduleItemEntity
import com.example.focusup.data.repository.ScheduleRepository
import kotlinx.coroutines.launch


class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduleRepository: ScheduleRepository
    private val _scheduleList = MutableLiveData<List<ScheduleItemEntity>>()
    val scheduleList: LiveData<List<ScheduleItemEntity>> get() = _scheduleList

    init {
        val db = DatabaseProvider.getDatabase(application)
        scheduleRepository = ScheduleRepository(db.scheduleDao())
    }

    fun loadScheduleForUser(userId: Int) {
        viewModelScope.launch {
            val list = scheduleRepository.getScheduleByUser(userId)
            _scheduleList.postValue(list)
        }
    }

    fun addScheduleItem(item: ScheduleItemEntity) {
        viewModelScope.launch {
            scheduleRepository.addScheduleItem(item)
            loadScheduleForUser(item.userId) // refresca lista
        }
    }

    fun updateScheduleItem(item: ScheduleItemEntity) {
        viewModelScope.launch {
            scheduleRepository.updateScheduleItem(item)
            loadScheduleForUser(item.userId)
        }
    }

    fun deleteScheduleItem(item: ScheduleItemEntity) {
        viewModelScope.launch {
            scheduleRepository.deleteScheduleItem(item)
            loadScheduleForUser(item.userId)
        }
    }
}