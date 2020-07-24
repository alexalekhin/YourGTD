package ru.alexalekhin.todomanager.data.task

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import ru.alexalekhin.todomanager.data.domain.DBDomain
import ru.alexalekhin.todomanager.data.project.DBProject

@Parcelize
@Entity(
    tableName = "Tasks",
    foreignKeys = [
        ForeignKey(
            entity = DBProject::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DBDomain::class,
            parentColumns = ["id"],
            childColumns = ["domainId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DBTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String,
    var checked: Boolean,
    var projectId: Int?,
    var domainId: Int?,
    var folderId: Int?,
    var weight: Int
) : Parcelable