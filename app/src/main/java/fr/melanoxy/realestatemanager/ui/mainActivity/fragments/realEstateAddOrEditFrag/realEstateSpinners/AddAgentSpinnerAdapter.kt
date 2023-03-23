package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateAddOrEditFrag.realEstateSpinners

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import fr.melanoxy.realestatemanager.databinding.AddAgentSpinnerItemBinding

class AddAgentSpinnerAdapter : ListAdapter, Filterable {

    private val dataSetObservable = DataSetObservable()
    private var items = emptyList<AddAgentViewStateItem>()

    override fun registerDataSetObserver(observer: DataSetObserver) {
        dataSetObservable.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        dataSetObservable.unregisterObserver(observer)
    }

    override fun areAllItemsEnabled(): Boolean = true

    override fun isEnabled(position: Int): Boolean = true

    override fun getItemViewType(position: Int): Int = 0

    override fun getViewTypeCount(): Int = 1

    override fun isEmpty(): Boolean = count == 0

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): AddAgentViewStateItem? = items.getOrNull(position)

    override fun getItemId(position: Int): Long = getItem(position)?.agentId ?: -1

    override fun hasStableIds(): Boolean = true

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null) {
            AddAgentSpinnerItemBinding.bind(convertView)
        } else {
            AddAgentSpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        getItem(position)?.let {
            Glide.with(binding.addAgentItemImageViewPfp)
                .load(it.agentPfpUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.addAgentItemImageViewPfp)
            binding.addAgentItemTextViewName.text = it.agentName
        }
        return binding.root
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence) = FilterResults()
        override fun publishResults(constraint: CharSequence, results: FilterResults) {}
        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as AddAgentViewStateItem).agentName
        }
    }

    fun setData(items: List<AddAgentViewStateItem>) {
        this.items = items
        dataSetObservable.notifyChanged()
    }

}