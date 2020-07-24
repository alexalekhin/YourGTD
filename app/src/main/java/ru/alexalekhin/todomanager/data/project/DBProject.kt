package ru.alexalekhin.todomanager.data.project

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "Projects",
    foreignKeys = [
//        ForeignKey(
//            entity = DBFolder::class,
//            parentColumns = ["id"],
//            childColumns = ["folderId"],
//            onDelete = ForeignKey.NO_ACTION
//        ),
//        ForeignKey(
//            entity = DBDomain::class,
//            parentColumns = ["id"],
//            childColumns = ["domainId"],
//            onDelete = ForeignKey.NO_ACTION
//        )
    ]
)
data class DBProject(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String,
    var description: String,
    var deadline: String,
    var domainId: Int?,
    var folderId: Int?,
    var weight: Int?
) : Parcelable