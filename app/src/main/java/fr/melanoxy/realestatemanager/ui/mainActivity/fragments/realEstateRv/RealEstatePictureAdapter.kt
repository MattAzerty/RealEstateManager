package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.melanoxy.realestatemanager.databinding.PictureItemBinding

class RealEstatePictureAdapter : ListAdapter<RealEstatePictureViewStateItem, RealEstatePictureAdapter.ViewHolder>(RealEstateDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        PictureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: PictureItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RealEstatePictureViewStateItem) {
            Glide.with(binding.realEstatePictureItemImage)
                .load(item.pictureUri)
                .fitCenter()
                .into(binding.realEstatePictureItemImage)
            binding.realEstatePictureItemName.text = item.realEstatePictureName
            binding.realEstatePictureItemStored.visibility = if(item.isStored) View.VISIBLE else View.INVISIBLE
            binding.realEstatePictureItemImage.setOnClickListener {
                item.onRealEstatePictureClicked.invoke()
            }
        }
    }

    object RealEstateDiffCallback : DiffUtil.ItemCallback<RealEstatePictureViewStateItem>() {
        override fun areItemsTheSame(oldItem: RealEstatePictureViewStateItem, newItem: RealEstatePictureViewStateItem): Boolean = oldItem.realEstateId == newItem.realEstateId

        override fun areContentsTheSame(oldItem: RealEstatePictureViewStateItem, newItem: RealEstatePictureViewStateItem): Boolean = oldItem == newItem
    }
}