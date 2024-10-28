package com.tifd.projectcomposed.data.model.local

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TugasRepository(application: Application) {
    private val mtugasDAO: TugasDAO
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = TugasDB.getDatabase(application)
        mtugasDAO = db.tugasDao()
    }
    fun getAllTugas(): LiveData<List<Tugas>> = mtugasDAO.getAllTugas()

    fun insert(tugas: Tugas){
        executorService.execute { mtugasDAO.insertTugas(tugas)}
    }

    fun updateTugasCompletion(tugasId: Int, isCompleted: Boolean){
        executorService.execute{
            mtugasDAO.updateTugasCompletion(tugasId, isCompleted)
        }
    }

    fun deleteTugas(id: Int){
        executorService.execute {
            mtugasDAO.deleteTugas(id)
        }
    }
}