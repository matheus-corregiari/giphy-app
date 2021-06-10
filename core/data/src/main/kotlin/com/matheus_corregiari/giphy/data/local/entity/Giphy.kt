package com.matheus_corregiari.giphy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.matheus_corregiari.giphy.data.model.GiphyImageDTO
import com.matheus_corregiari.giphy.data.model.GiphyImageUrlDTO
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO

@Entity(tableName = "giphy")
internal class Giphy(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image_url") val imageUrl: String
) {
    fun asGiphyItemDTO() = GiphyItemDTO(id, title, GiphyImageDTO(GiphyImageUrlDTO(imageUrl))).also {
        it.favorite = true
    }
}