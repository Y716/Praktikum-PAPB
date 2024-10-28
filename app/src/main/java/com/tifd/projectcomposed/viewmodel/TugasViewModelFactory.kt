package com.tifd.projectcomposed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tifd.projectcomposed.data.model.local.TugasRepository

class TugasViewModelFactory(private val tugasRepository: TugasRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(TugasViewModel::class.java)) {
            return TugasViewModel(tugasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}