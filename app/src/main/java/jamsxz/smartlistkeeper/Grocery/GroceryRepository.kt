package jamsxz.smartlistkeeper.Grocery

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GroceryRepository(private val db: GroceryDatabase) {
    suspend fun insert(items: GroceryItems) = db.getGroceryDao().insert(items)
    suspend fun delete(items: GroceryItems) = db.getGroceryDao().delete(items)
    suspend fun updateGroceryItem(items: GroceryItems) = db.getGroceryDao().updateGroceryItem(items)

    fun getAllItems() = db.getGroceryDao().getAllGroceryItems()
    fun getItemsByCategory(items: String) = db.getGroceryDao().getItemsByCategory(items)
    fun getItemsByItemName(itemName: String) = db.getGroceryDao().getItemsByItemName(itemName)
    suspend fun getListByName(items: String) = db.getGroceryDao().getListByName(items)
    suspend fun getItemsByName(itemName: String, category: String) = db.getGroceryDao().getItemsByName(itemName, category)
    suspend fun deleteItemsByCategory(category: String) {
        db.getGroceryDao().deleteItemsByCategory(category)
    }

    suspend fun updateItemsByCategory(category: String, newCategory: String) {
        db.getGroceryDao().updateItemsByCategory(category, newCategory)
    }

    suspend fun updateAllByName(
        itemName: String,
        newName: String,
        newQuantity: Int,
        newPrice: Double,
        newNotes: String
    ) {
        withContext(Dispatchers.IO) {
            db.getGroceryDao().updateAllByName(itemName, newName, newQuantity, newPrice, newNotes)
        }
    }

     fun getCountOfItemsInCategory(categoryName: String): LiveData<List<Int>>{
        return db.getGroceryDao().countItemsWithCategory(categoryName)
    }

     fun countCheckedItemsInCategory(categoryName: String): LiveData<List<Int>> {
        return db.getGroceryDao().countCheckedItemsWithCategory(categoryName)
    }


}