package fr.melanoxy.realestatemanager.ui.mainActivity.fragments.realEstateSearchBar

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListAdapter
import androidx.core.text.bold
import androidx.core.text.color
import fr.melanoxy.realestatemanager.databinding.SearchTagDropDownBinding

class RealEstateSearchBarSpinnerAdapter : ListAdapter, Filterable {

    private val dataSetObservable = DataSetObservable()
    private var items = emptyList<RealEstateSearchBarStateItem>()

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

    override fun getItem(position: Int): RealEstateSearchBarStateItem? = items.getOrNull(position)

    override fun getItemId(position: Int): Long = getItem(position)?.id ?: -1

    override fun hasStableIds(): Boolean = true

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null) {
            SearchTagDropDownBinding.bind(convertView)
        } else {
            SearchTagDropDownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        getItem(position)?.let {
            binding.tagItemTvTag.text = it.tag
            binding.tagItemTvDescription.text = it.criteriaType
        }
        return binding.root
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults()
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
        override fun convertResultToString(resultValue: Any): CharSequence {
            val tag = (resultValue as RealEstateSearchBarStateItem).tag
            //https://android.github.io/android-ktx/core-ktx/androidx.text/android.text.-spannable-string-builder/index.html
            return SpannableStringBuilder()
                .color(0xFFffa600.toInt()) { bold { append(tag) } }
        }
    }

    fun setData(items: List<RealEstateSearchBarStateItem>) {
        this.items = items
        dataSetObservable.notifyChanged()
    }


}