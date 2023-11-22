package jamsxz.smartlistkeeper.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import jamsxz.smartlistkeeper.Grocery.GroceryItems
import jamsxz.smartlistkeeper.R
import jamsxz.smartlistkeeper.databinding.GroceryRvItemBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class GroceryRVAdapter(
    var list: List<GroceryItems>,
    val groceryItemClickInterface: GroceryItemClickInterface,
    var context: Context,
) : RecyclerView.Adapter<GroceryRVAdapter.GroceryViewHolder>() {

    inner class GroceryViewHolder(
        private val binding: GroceryRvItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")

        fun bind(item: GroceryItems) {
            binding.idtvitemname.text = item.itemName
            binding.idtvquantity.text = item.itemQuantity.toString()
            val decimalFormat = DecimalFormat("0.00")
            decimalFormat.roundingMode = RoundingMode.DOWN

            binding.idtvrate.text = "₱ " + decimalFormat.format(item.itemPrice)

            binding.idivdelete.setOnClickListener {
                groceryItemClickInterface.onItemClick(item, "delete")
            }

            binding.rlayoutsxz.setOnClickListener {
                groceryItemClickInterface.onItemClick(item, "clicked")
            }

            binding.selectionBox.isChecked = item.isChecked

            if (item.isChecked) {
                val color = ContextCompat.getColor(context, R.color.blue)
                binding.rlayoutsxz.setBackgroundColor(color)
                binding.idtvitemname.paintFlags =
                    binding.idtvitemname.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                val color = ContextCompat.getColor(context, R.color.white)
                binding.rlayoutsxz.setBackgroundColor(color)
                binding.idtvitemname.paintFlags =
                    binding.idtvitemname.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.selectionBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != item.isChecked) {
                    // item.isChecked = isChecked
                    if (isChecked) {
                        binding.idtvitemname.paintFlags =
                            binding.idtvitemname.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        binding.idtvitemname.paintFlags =
                            binding.idtvitemname.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    groceryItemClickInterface.onItemClick(item, "isChecked")
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val binding =
            GroceryRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroceryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<GroceryItems>) {
        list = newList
        notifyDataSetChanged()
    }

    interface GroceryItemClickInterface {
        fun onItemClick(groceryItems: GroceryItems, data: String)
    }


}
