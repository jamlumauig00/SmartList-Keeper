package jamsxz.smartlistkeeper.Activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import jamsxz.smartlistkeeper.Adapter.GroceryListAdapter
import jamsxz.smartlistkeeper.Grocery.GroceryDatabase
import jamsxz.smartlistkeeper.Grocery.GroceryItems
import jamsxz.smartlistkeeper.Grocery.GroceryRepository
import jamsxz.smartlistkeeper.Grocery.GroceryViewModel
import jamsxz.smartlistkeeper.Grocery.GroceryViewModelFactory
import jamsxz.smartlistkeeper.databinding.ActivityMainmainBinding
import jamsxz.smartlistkeeper.databinding.CategoryAddDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainmainActivity : AppCompatActivity(), GroceryListAdapter.GroceryItemClickInterface {
    private lateinit var binding: ActivityMainmainBinding
    private lateinit var itemRV: RecyclerView
    private lateinit var groceryRVAdapter: GroceryListAdapter
    private lateinit var groceryViewModel: GroceryViewModel
    private lateinit var list: List<GroceryItems>

    var listName: String = ""
    private val totalCountMap: MutableMap<String, List<Int>> = mutableMapOf()
    private val checkedCountMap: MutableMap<String, List<Int>> = mutableMapOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainmainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemRV = binding.listItems
        list = ArrayList()

        val groceryRepository = GroceryRepository(GroceryDatabase(this))
        val factory = GroceryViewModelFactory(groceryRepository)
        groceryViewModel = ViewModelProvider(this, factory)[GroceryViewModel::class.java]

        groceryRVAdapter = GroceryListAdapter(listOf(), this, mutableMapOf(), mutableMapOf())
        itemRV.layoutManager = LinearLayoutManager(this)
        itemRV.adapter = groceryRVAdapter

        groceryViewModel.getAllGroceryItems().observe(this) { items ->
            val uniqueItemsMap = mutableMapOf<String, GroceryItems>()

            if(items.isEmpty()){
                itemRV.visibility = View.GONE
                binding.listEmpty.visibility = View.VISIBLE
            }else{
                itemRV.visibility = View.VISIBLE
                binding.listEmpty.visibility = View.GONE
            }
            for (item in items) {
                uniqueItemsMap[item.listName] = item
                listName = item.listName
            }
            val uniqueItems = uniqueItemsMap.values.toList()
            val sortedUniqueItems = uniqueItems.sortedBy { it.listName }

            groceryRVAdapter.list = sortedUniqueItems
            groceryRVAdapter.notifyDataSetChanged()


            val uniqueCategories = items.map { it.listName }.distinct()

            // Observe counts for each category separately
            uniqueCategories.forEach { category ->
                // Observe the count for items in the category
                groceryViewModel.getCountOfItemsInCategory(category).observe(this) { counts ->
                    // Update counts for this category
                    totalCountMap[category] = counts
                    Log.e("ItemsInCategory", counts.toString())
                    updateAdapterIfCountsReady()
                }

                // Observe the counts for checked items in the category
                groceryViewModel.getCountOfCheckedItemsInCategory(category).observe(this) { countsa ->
                    // Update checked counts for this category
                    checkedCountMap[category] = countsa
                    Log.e("CheckedItemsInCategory", countsa.toString())
                    updateAdapterIfCountsReady()
                }
            }
        }


        binding.fabAddList.setOnClickListener {
            openDialog("add", "")
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterIfCountsReady() {
        val allCountsReady = totalCountMap.keys.containsAll(checkedCountMap.keys)
                && totalCountMap.all { it.value.isNotEmpty() }
                && checkedCountMap.all { it.value.isNotEmpty() }

        if (allCountsReady) {
            // Pass the entire totalCountMap and checkedCountMap to adapter
            groceryRVAdapter.totalCount = totalCountMap
            groceryRVAdapter.checkedCount = checkedCountMap


            groceryRVAdapter.notifyDataSetChanged()
        }
    }

    private fun openDialog(function: String, dataCategory: String) {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = CategoryAddDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.idbtncancel.setOnClickListener {
            dialog.dismiss()
        }

        if (function == "edit") {
            dialogBinding.idListname.setText(dataCategory)
        }

        dialogBinding.idbtnadd.setOnClickListener {
            val listName: String = dialogBinding.idListname.text.toString()
            if (listName.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    handleListAction(function, dataCategory, listName)
                    dialog.dismiss()
                }
            } else {
                showToast("Please fill all details properly")
            }
        }

        if (!isFinishing) {
            dialog.show()
        }
    }

    private suspend fun handleListAction(
        function: String,
        dataCategory: String,
        listName: String
    ) {
        val itemAlreadyExists = groceryViewModel.checkIfListExists(listName)
        if (!itemAlreadyExists) {
            if (function == "edit") {
                groceryViewModel.updateItemsByCategory(dataCategory, listName)
                showToast("List Name Updated Successfully.")
            } else {
                groceryViewModel.insert(GroceryItems(listName, "", 0, 0.0, false, ""))
                showToast("List Added")
            }
        } else {
            showToast("Item already exists")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override suspend fun onItemClick(groceryItems: GroceryItems, type: String) {
        when (type) {
            "add" -> {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra("category", groceryItems.listName)
                })
            }

            "edit" -> openDialog("edit", groceryItems.listName)
            "delete" -> {
                groceryViewModel.deleteItemsByCategory(groceryItems.listName)
                showToast("List Deleted Successfully.")
            }
        }
    }
}
