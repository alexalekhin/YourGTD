package ru.alexalekhin.todomanager.data.folder

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "Folders"
)
data class DBFolder(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String
) : Parcelable