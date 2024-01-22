package com.animesearch.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.animesearch.model.HistoryModel

@Dao
interface DatabaseRepository {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertHistory(historyModel: HistoryModel)

  @Query("SELECT * FROM table_history")
  fun getHistory(): List<HistoryModel>

}