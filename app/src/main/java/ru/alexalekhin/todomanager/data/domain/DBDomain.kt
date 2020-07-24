package ru.alexalekhin.todomanager.data.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Domains")
data class DBDomain(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String,
    var weight: Int?
): Parcelable