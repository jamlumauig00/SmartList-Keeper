package jamsxz.smartlistkeeper.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import jamsxz.smartlistkeeper.Adapter.GroceryRVAdapter
import jamsxz.smartlistkeeper.Grocery.GroceryDatabase
import jamsxz.smartlistkeeper.Grocery.GroceryItems
import jamsxz.smartlistkeeper.Grocery.GroceryRepository
import jamsxz.smartlistkeeper.Grocery.GroceryViewModel
import jamsxz.smartlistkeeper.Grocery.GroceryViewModelFactory
import jamsxz.smartlistkeeper.databinding.ActivityMainBinding
import jamsxz.smartlistkeeper.databinding.GroceryAddDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), GroceryRVAdapter.GroceryItemClickInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var itemRV: RecyclerView
    private lateinit var list: List<GroceryItems>
    private lateinit var groceryRVAdapter: GroceryRVAdapter
    private lateinit var groceryViewModel: GroceryViewModel


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemRV = binding.rvitems
        itemRV.layoutManager = LinearLayoutManager(this)

        val categorys = intent.getStringExtra("category") ?: ""
        with(binding) {
            category.text = categorys

            val groceryRepository = GroceryRepository(GroceryDatabase(this@MainActivity))
            val factory = GroceryViewModelFactory(groceryRepository)
            groceryViewModel = ViewModelProvider(this@MainActivity, factory)[GroceryViewModel::class.java]

            groceryRVAdapter = GroceryRVAdapter(emptyList(), this@MainActivity, this@MainActivity) // Initialize the adapter with an empty list
            itemRV.adapter = groceryRVAdapter //

            groceryViewModel.getItemsByCategory(categorys).observe(this@MainActivity) { items ->
                val filteredItems = items.filter { it.itemName.isNotEmpty() }
                val sortedList =
                    filteredItems.sortedByDescending { !it.isChecked } // Sort the list by isChecked status
                groceryRVAdapter.updateList(sortedList) // Update the RecyclerView's adapter
                setupRecyclerView(sortedList)

                val checkedItems = items.filter { it.isChecked }
                val notcheckedItems = items.filter { !it.isChecked }
                val sumOfCheckedPrices = checkedItems.sumByDouble { it.itemPrice * it.itemQuantity }
                val sumOfNotCheckedPrices = notcheckedItems.sumByDouble { it.itemPrice * it.itemQuantity }

                Log.e("sumOfCheckedPrices", sumOfCheckedPrices.toString())
                Log.e("sumOfNotCheckedPrices", sumOfNotCheckedPrices.toString())

                // Assuming userValue is the input from the user, replace this with your actual input
               // val userValue = 10.0 // Replace this with the user's input

                val decimalFormat = DecimalFormat("0.00")
                decimalFormat.roundingMode = RoundingMode.DOWN


                unchecked.text = "₱ " + decimalFormat.format(sumOfNotCheckedPrices)
                checked.text = "₱ " + decimalFormat.format(sumOfCheckedPrices)
                total.text = "₱ " + decimalFormat.format((sumOfNotCheckedPrices + sumOfCheckedPrices))

            }

            fabAdd.setOnClickListener { openDialog() }
            back.setOnClickListener { onBackPressed() }
        }
    }

    private fun setupRecyclerView(items: List<GroceryItems>) {
        groceryRVAdapter = GroceryRVAdapter(items, this, this)
        itemRV.adapter = groceryRVAdapter
    }


    private fun openDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = GroceryAddDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.idbtncancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.idbtnadd.setOnClickListener {
            val itemname = dialogBinding.idEdtitemname.text.toString()
            val itemprice = dialogBinding.idEdtitemprice.text.toString()
            val itemquantity = dialogBinding.idEdtitemquantity.text.toString()

            if (itemname.isNotEmpty() && itemprice.isNotEmpty() && itemquantity.isNotEmpty()) {
                val qty = itemquantity.toIntOrNull()
                val pr = itemprice.toDoubleOrNull()

                if (qty != null && pr != null) {
                    addItemToDatabase(itemname, qty, pr)
                    dialog.dismiss()
                } else {
                    showToastOnMainThread("Please enter valid quantity and price")
                }
            } else {
                showToastOnMainThread("Please fill all details properly")
            }
        }
        dialog.show()
    }

    private fun addItemToDatabase(itemname: String, qty: Int, pr: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val categoryName= intent.getStringExtra("category")!!
            val itemAlreadyExists = groceryViewModel.checkIfItemExists(itemname, categoryName)
            if (!itemAlreadyExists) {
                intent.getStringExtra("category")?.let { category ->
                    val item = GroceryItems(category, itemname, qty, pr, false, "")
                    groceryViewModel.insert(item)
                }
                showToastOnMainThread("Item Added")
            } else {
                showToastOnMainThread("Item already exists")
            }
        }
    }

    private fun showToastOnMainThread(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(groceryItems: GroceryItems, data: String) {
        Log.e("data", data)

        when (data) {
            "isChecked" -> {
                groceryItems.isChecked = !groceryItems.isChecked
                groceryViewModel.updateGroceryItem(groceryItems)
            }

            "clicked" -> {
                startActivity(Intent(this, GroceryEditActivity::class.java).apply {
                    putExtra("itemName", groceryItems.itemName)
                })
            }

            else -> {
                groceryViewModel.delete(groceryItems)
                Toast.makeText(applicationContext, "Item Deleted Successfully.", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }
}