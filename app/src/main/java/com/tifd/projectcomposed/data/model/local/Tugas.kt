package com.tifd.projectcomposed.data.model.local

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
class Tugas (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "matkul")
    var matkul: String,
    @ColumnInfo(name = "detail_tugas")
    var detail_tugas: String,
    @ColumnInfo(name = "selesai")
    var selesai: Boolean,
    @ColumnInfo(name = "deadline")
    val deadline: String,
    @ColumnInfo(name = "foto_tugas")
    val imageUri: String

) : Parcelable