package jamsxz.smartlistkeeper.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import jamsxz.smartlistkeeper.Grocery.GroceryDatabase
import jamsxz.smartlistkeeper.Grocery.GroceryRepository
import jamsxz.smartlistkeeper.Grocery.GroceryViewModel
import jamsxz.smartlistkeeper.Grocery.GroceryViewModelFactory
import jamsxz.smartlistkeeper.databinding.ActivityGroceryEditBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class GroceryEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroceryEditBinding
    private lateinit var groceryViewModel: GroceryViewModel
    private val lifecycleOwner: LifecycleOwner =
        this // Assuming 'this' refers to the current LifecycleOwner
    var newPrice: Double = 0.0
    var newQuantity: Int = 0
    var newNotes: String = ""
    var newItemName: String = ""
    private var itemName: String = ""
    val decimalFormat = DecimalFormat("0.00")

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroceryEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        decimalFormat.roundingMode = RoundingMode.DOWN
        itemName = intent.getStringExtra("itemName") ?: ""
        Log.e("itemName", itemName)

        val groceryRepository = GroceryRepository(GroceryDatabase(this))
        val factory = GroceryViewModelFactory(groceryRepository)
        groceryViewModel = ViewModelProvider(this, factory)[GroceryViewModel::class.java]

        groceryViewModel.getItemsByItemName(itemName).observe(this) { items ->
            if (items.isNotEmpty()) {
                val item = items.first() // Assuming you're handling a single item here

                with(binding) {

                    newPrice = decimalFormat.format(item.itemPrice).toDouble()
                    newQuantity = item.itemQuantity
                    newNotes = item.notes
                    newItemName = item.itemName
                    itemName = item.itemName

                    category.text = item.listName
                    itemEditText.setText(newItemName)
                    note.setText(newNotes)
                    priceEdittext.setText(decimalFormat.format(newPrice))
                    qty.setText(newQuantity.toString())

                    itemEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            newItemName = s.toString()
                        }
                    })

                    note.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            newNotes = s.toString()
                        }
                    })


                    // TextChangedListeners for price and quantity
                    priceEdittext.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            val price = s.toString().toDoubleOrNull() ?: 0.00
                            groceryViewModel.setPrice(decimalFormat.format(price).toDouble())
                        }

                        override fun afterTextChanged(s: Editable?) {
                            newPrice = s.toString().toDoubleOrNull() ?: 0.0
                            /* val newPrice =
                             groceryViewModel.updateAllByName(item.itemName, item.itemName, item.itemQuantity, newPrice, item.notes)*/
                        }
                    })

                    qty.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            val quantity = s.toString().toIntOrNull() ?: 0
                            groceryViewModel.setQuantity(quantity)
                        }

                        override fun afterTextChanged(s: Editable?) {
                            newQuantity = s.toString().toIntOrNull() ?: 0

                        }
                    })


                    groceryViewModel.total.observe(lifecycleOwner) { total ->
                        // Update the UI with the new total

                        totalEdittext.text = "₱ " + decimalFormat.format(total)
                    }

                    back.setOnClickListener {
                        onBackPressed()
                        groceryViewModel.updateAllByName(
                            item.itemName,
                            newItemName,
                            newQuantity,
                            newPrice,
                            newNotes
                        )
                    }
                }

                // For demonstration, setting values (you can replace this with user inputs)
                groceryViewModel.setPrice(decimalFormat.format(item.itemPrice).toDouble())
                groceryViewModel.setQuantity(item.itemQuantity)

            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        groceryViewModel.updateAllByName(itemName, newItemName, newQuantity, newPrice, newNotes)

    }
}