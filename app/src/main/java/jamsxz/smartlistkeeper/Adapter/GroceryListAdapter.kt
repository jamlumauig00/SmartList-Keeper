package jamsxz.smartlistkeeper.Adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jamsxz.smartlistkeeper.Grocery.GroceryItems
import jamsxz.smartlistkeeper.databinding.GroceryListItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroceryListAdapter(
    var list: List<GroceryItems>,
    val groceryItemClickInterface: GroceryItemClickInterface,
    var totalCount: MutableMap<String, List<Int>>,
    var checkedCount: MutableMap<String, List<Int>>
) : RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder>() {


    inner class GroceryViewHolder(private val binding: GroceryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: GroceryItems, totalcounts: List<Int>, checkedcounts: List<Int>) {

            val total = totalcounts.firstOrNull() ?: 0
            val checked = checkedcounts.firstOrNull() ?: 0

            binding.listname.text = item.listName
            binding.cardView.setOnClickListenerWithCoroutine {
                groceryItemClickInterface.onItemClick(item, "add")
            }
            binding.idDelete.setOnClickListenerWithCoroutine {
                groceryItemClickInterface.onItemClick(item, "delete")
            }
            binding.idEdit.setOnClickListenerWithCoroutine {
                groceryItemClickInterface.onItemClick(item, "edit")
            }
            binding.pbbar.text = "$checked/$total"

            val completionPercentage = (checked.toFloat() / total.toFloat()) * 100
            binding.simpleProgressBar.max = 100
            binding.simpleProgressBar.progress = completionPercentage.toInt()
        }
    }

    fun View.setOnClickListenerWithCoroutine(action: suspend () -> Unit) {
        setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                action()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val binding =
            GroceryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroceryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val item = list[position]
        val category = item.listName
        val totalCount = totalCount[category] ?: listOf() // Get counts for this category
        val checkedCount = checkedCount[category] ?: listOf() // Get checked counts for this category
        Log.e("totalCountMap", "$totalCount")
        Log.e("checkedCountMap", "$checkedCount")
        holder.bind(item, totalCount, checkedCount)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface GroceryItemClickInterface {
        suspend fun onItemClick(groceryItems: GroceryItems, type: String)
    }
}
