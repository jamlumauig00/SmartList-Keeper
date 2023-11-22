package jamsxz.smartlistkeeper.Grocery

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import jamsxz.smartlistkeeper.Grocery.GroceryItems
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [GroceryItems::class], version = 2)
abstract class GroceryDatabase : RoomDatabase() {

    abstract fun getGroceryDao(): GroceryDao

    companion object {
        @Volatile
        private var instance: GroceryDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                GroceryDatabase::class.java,
                "GroceryApp.db"
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    GlobalScope.launch(Dispatchers.IO) {
                        instance?.getGroceryDao()?.insert(GroceryItems("Vegetables", "potato", 2, 1.99, true, ""))
                        instance?.getGroceryDao()?.insert(GroceryItems("Vegetables", "carrots", 1, 2.49, false , ""))
                        // Add more items here
                    }
                }
            }).build()
    }
}
