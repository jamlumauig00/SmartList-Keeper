package jamsxz.smartlistkeeper.Grocery


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroceryViewModel(private val repository: GroceryRepository) : ViewModel() {

    val groceryItems: LiveData<List<GroceryItems>> = repository.getAllItems()

    fun updateGroceryItem(groceryItem: GroceryItems) {
        // Update the item in the database
        // Example update operation using Room (simplified)
        viewModelScope.launch {
            repository.updateGroceryItem(groceryItem)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun insert(items: GroceryItems) = GlobalScope.launch {
        repository.insert(items)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(items: GroceryItems) = GlobalScope.launch {
        repository.delete(items)
    }

    fun getAllGroceryItems() = repository.getAllItems()

    fun getItemsByCategory(category: String) = repository.getItemsByCategory(category)

    fun getItemsByItemName(itemName: String) = repository.getItemsByItemName(itemName)

    suspend fun checkIfListExists(itemName: String): Boolean {
        val existingItems = repository.getListByName(itemName)
        return existingItems.isNotEmpty()
    }

    suspend fun checkIfItemExists(itemName: String, category: String): Boolean {
        val existingItems = repository.getItemsByName(itemName, category)
        return existingItems.isNotEmpty()
    }

    suspend fun deleteItemsByCategory(category: String) {
        repository.deleteItemsByCategory(category)
    }

    suspend fun updateItemsByCategory(category: String, newCategory: String) {
        repository.updateItemsByCategory(category, newCategory)
    }

    fun updateAllByName(
        itemName: String,
        newName: String,
        newQuantity: Int,
        newPrice: Double,
        newNotes: String
    ) {
        viewModelScope.launch {
            repository.updateAllByName(itemName, newName, newQuantity, newPrice, newNotes)
        }
    }

    fun getCountOfItemsInCategory(categoryName: String): LiveData<List<Int>> {
        return repository.getCountOfItemsInCategory(categoryName)
    }

    fun getCountOfCheckedItemsInCategory(categoryName: String): LiveData<List<Int>> {
        return repository.countCheckedItemsInCategory(categoryName)
    }


    private val _price = MutableLiveData<Double>()
    private val _quantity = MutableLiveData<Int>()

    // Total as a LiveData
    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    fun setPrice(price: Double) {
        _price.value = price
        updateTotal()
    }

    fun setQuantity(quantity: Int) {
        _quantity.value = quantity
        updateTotal()
    }

    private fun updateTotal() {
        val priceValue = _price.value ?: 0.0
        val quantityValue = _quantity.value ?: 0
        _total.value = priceValue * quantityValue
    }

}