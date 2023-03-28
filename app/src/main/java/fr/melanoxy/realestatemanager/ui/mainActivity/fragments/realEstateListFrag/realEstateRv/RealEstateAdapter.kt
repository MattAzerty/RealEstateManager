package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateListFrag.realEstateRv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
                .apply(RequestOptions.centerCropTransform())
                .into(binding.realEstateItemIvPic1)
            binding.realEstateItemTvType.text = item.realEstateType
            binding.realEstateItemTvCity.text = item.realEstateCity
            binding.realEstateItemTvPrice.text = item.realEstatePrice
            binding.realEstateItemCl.setOnClickListener {
                item.onRealEstateClicked.invoke()
            }
        }
    }

    object RealEstateDiffCallback : DiffUtil.ItemCallback<RealEstateViewStateItem>() {
        override fun areItemsTheSame(oldItem: RealEstateViewStateItem, newItem: RealEstateViewStateItem): Boolean = oldItem.realEstateId == newItem.realEstateId

        override fun areContentsTheSame(oldItem: RealEstateViewStateItem, newItem: RealEstateViewStateItem): Boolean = oldItem == newItem
    }
}