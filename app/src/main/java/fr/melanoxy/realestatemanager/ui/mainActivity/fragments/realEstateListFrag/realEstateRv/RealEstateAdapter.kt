package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import fr.melanoxy.realestatemanager.databinding.RealEstateItemBinding

class RealEstateAdapter : ListAdapter<RealEstateViewStateItem, RealEstateAdapter.ViewHolder>(RealEstateDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        RealEstateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: RealEstateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RealEstateViewStateItem) {
            Glide.with(binding.realEstateItemIvPic1)
                .load(item.pictureUri)
                .transform(RoundedCorners(30))
                .into(binding.realEstateItemIvPic1)
            binding.realEstateItemTvType.text = item.realEstateType
            binding.realEstateItemTvCity.text = item.realEstateCity
            binding.realEstateItemTvPrice.text = item.realEstatePrice
            if(item.isSelected) {
                binding.realEstateItemCl.setBackgroundColor(0xFFffa600.toInt())
                binding.realEstateItemTvPrice.setTextColor(0xFFFFFFFF.toInt())
                binding.realEstateItemTvPrice.setBackgroundColor(Color.BLACK)
                binding.realEstateItemDividerEnd.dividerColor = 0xFFF39E02.toInt()
            }
            else {
                binding.realEstateItemCl.setBackgroundColor(0xFFFFFFFF.toInt())
                binding.realEstateItemTvPrice.setTextColor(0xFFffa600.toInt())
                binding.realEstateItemTvPrice.setBackgroundColor(0xFFF4F4F4.toInt())
                binding.realEstateItemDividerEnd.dividerColor = 0xFFe3e3e3.toInt()
            }
            binding.realEstateItemCl.setOnClickListener {
                item.onRealEstateClicked.invoke()
            }
            binding.realEstateItemCl.setOnLongClickListener {
                item.onRealEstateLongClick.invoke()
                true
            }
        }
    }

    object RealEstateDiffCallback : DiffUtil.ItemCallback<RealEstateViewStateItem>() {
        override fun areItemsTheSame(oldItem: RealEstateViewStateItem, newItem: RealEstateViewStateItem): Boolean = oldItem.realEstateId == newItem.realEstateId

        override fun areContentsTheSame(oldItem: RealEstateViewStateItem, newItem: RealEstateViewStateItem): Boolean = oldItem == newItem
    }
}