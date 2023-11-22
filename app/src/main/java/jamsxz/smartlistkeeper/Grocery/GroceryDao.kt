package jamsxz.smartlistkeeper.Grocery

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao

interface GroceryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GroceryItems)
    @Delete
    suspend fun delete(item: GroceryItems)

    @Query("SELECT * FROM Grocery_items")
    fun getAllGroceryItems(): LiveData<List<GroceryItems>>

    @Query("SELECT * FROM Grocery_items WHERE listName = :listName")
    fun getItemsByCategory(listName: String): LiveData<List<GroceryItems>>

    @Query("SELECT * FROM Grocery_items WHERE itemName = :itemName")
    fun getItemsByItemName(itemName: String): LiveData<List<GroceryItems>>

    @Query("SELECT * FROM Grocery_items WHERE listName = :listName")
    suspend fun getListByName(listName: String): List<GroceryItems>

    @Query("SELECT * FROM Grocery_items WHERE itemName = :itemName AND listName = :category")
    suspend fun getItemsByName(itemName: String, category: String): List<GroceryItems>

    @Query("DELETE FROM Grocery_items WHERE listName = :listName")
    suspend fun deleteItemsByCategory(listName: String)

    @Query("UPDATE Grocery_items SET listName = :newCategory WHERE listName = :listName")
    suspend fun updateItemsByCategory(listName: String, newCategory: String)

    @Update
    suspend fun updateGroceryItem(groceryItem: GroceryItems) // Update method for changing isChecked field

    @Query("UPDATE Grocery_items SET itemName = :newName , itemQuantity = :newQuantity,  itemPrice = :newPrice, notes = :newNotes  WHERE itemName = :itemName")
    suspend fun updateAllByName(itemName: String, newName: String, newQuantity: Int, newPrice: Double , newNotes: String)

    @Query("SELECT COUNT(*) FROM Grocery_items WHERE listName = :categoryName AND itemName != ''")
    fun countItemsWithCategory(categoryName: String): LiveData<List<Int>>

    @Query("SELECT COUNT(*) FROM Grocery_items WHERE listName = :categoryName AND itemName != '' AND isChecked = 1")
    fun countCheckedItemsWithCategory(categoryName: String): LiveData<List<Int>>

}

