package db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import model.TemplateRoom

@Dao
interface TemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TemplateRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: ArrayList<TemplateRoom>)

    @Query(value = "SELECT * FROM tbl_template_room ORDER BY _templateString ASC")
    suspend fun selectAll(): List<TemplateRoom>

    @Query("DELETE FROM tbl_template_room")
    suspend fun deleteAll()

    @Query("DELETE FROM tbl_template_room WHERE _templateInt = :itemId")
    suspend fun delete(itemId : Long)
}