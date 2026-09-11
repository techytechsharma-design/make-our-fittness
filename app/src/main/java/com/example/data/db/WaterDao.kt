package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.WaterLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_logs WHERE dateString = :dateString ORDER BY timestamp ASC")
    fun getWaterLogsForDate(dateString: String): Flow<List<WaterLogEntity>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE dateString = :dateString")
    fun getTotalWaterForDate(dateString: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(water: WaterLogEntity): Long

    @Query("DELETE FROM water_logs WHERE id = (SELECT id FROM water_logs WHERE dateString = :dateString ORDER BY timestamp DESC LIMIT 1)")
    suspend fun removeLastWaterForDate(dateString: String)
}
