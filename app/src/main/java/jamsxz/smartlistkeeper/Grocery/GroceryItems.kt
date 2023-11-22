package jamsxz.smartlistkeeper.Grocery

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Grocery_items")
data class GroceryItems(

    @ColumnInfo(name = "listName")
    var listName: String,

    @ColumnInfo(name = "itemName")
    var itemName: String,

    @ColumnInfo(name = "itemQuantity")
    var itemQuantity: Int,

    @ColumnInfo(name = "itemPrice")
    var itemPrice: Double,

    @ColumnInfo(name = "isChecked")
    var isChecked: Boolean, // Add isChecked property to track the checked state

    @ColumnInfo(name = "notes")
    var notes: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}