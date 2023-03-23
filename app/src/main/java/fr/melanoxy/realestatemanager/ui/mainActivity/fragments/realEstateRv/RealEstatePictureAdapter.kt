package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateRv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
                .apply(RequestOptions.centerCropTransform())
                .into(binding.realEstatePictureItemImage)
            binding.realEstatePictureItemName.text = item.realEstatePictureName
            binding.realEstatePictureItemStored.visibility = if(item.isStored) View.VISIBLE else View.INVISIBLE
            binding.realEstatePictureItemEdited.visibility = if(item.isEdited) View.VISIBLE else View.GONE
            binding.realEstatePictureItemDelete.visibility = if(item.toDelete) View.VISIBLE else View.GONE
            binding.realEstatePictureItemImage.setOnClickListener { item.onRealEstatePictureClicked.invoke() }
            binding.realEstatePictureItemImage.setOnLongClickListener {
                item.onRealEstatePictureLongPress.invoke()
                true
            }
            binding.realEstatePictureItemDelete.setOnClickListener {item.onRealEstatePictureDeleteClicked.invoke()}
            binding.realEstatePictureItemDelete.setOnLongClickListener {
                item.onRealEstatePictureDeleteLongPress.invoke()
                true
            }
            binding.realEstatePictureItemName.setOnClickListener { item.onRealEstatePictureNameClicked.invoke() }
        }
    }

    object RealEstateDiffCallback : DiffUtil.ItemCallback<RealEstatePictureViewStateItem>() {
        override fun areItemsTheSame(oldItem: RealEstatePictureViewStateItem, newItem: RealEstatePictureViewStateItem): Boolean = oldItem.pictureUri == newItem.pictureUri

        override fun areContentsTheSame(oldItem: RealEstatePictureViewStateItem, newItem: RealEstatePictureViewStateItem): Boolean = oldItem == newItem
    }
}