package db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import main.ApplicationClass
import model.TemplateRoom

@Database(entities = [TemplateRoom::class], version = 1)
abstract class TemplateDatabase : RoomDatabase() {

    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var instance: TemplateDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: ApplicationClass) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: ApplicationClass) =
            Room.databaseBuilder(context.applicationContext, TemplateDatabase::class.java, "db_template.db").build()
    }
}