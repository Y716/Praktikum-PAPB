package com.tifd.projectcomposed.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tifd.projectcomposed.data.model.local.Tugas
import com.tifd.projectcomposed.data.model.local.TugasRepository
import kotlinx.coroutines.launch

class TugasViewModel(private val tugasRepository: TugasRepository) : ViewModel() {

    val listTugas: LiveData<List<Tugas>> = tugasRepository.getAllTugas()

    // Modify this method to accept a deadline parameter
    fun addTugas(matkul: String, detailTugas: String, deadline: String, imageUri: Uri) {
        val tugas = Tugas(matkul = matkul, detail_tugas = detailTugas, deadline = deadline, imageUri = imageUri.toString(), selesai = false)
        viewModelScope.launch {
            tugasRepository.insert(tugas)
        }
    }

    fun updateTugasCompletion(tugasId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            tugasRepository.updateTugasCompletion(tugasId, isCompleted)
        }
    }

    fun deleteTugas(id: Int) {
        viewModelScope.launch {
            tugasRepository.deleteTugas(id)
        }
    }
}