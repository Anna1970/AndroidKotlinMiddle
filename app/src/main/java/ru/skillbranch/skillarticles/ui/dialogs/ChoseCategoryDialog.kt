package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chose_category_dialog.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel


class ChoseCategoryDialog : DialogFragment() {
    private val viewModel: ArticlesViewModel by activityViewModels()
    private val selectedCategories = mutableListOf<String>()
    private val args : ChoseCategoryDialogArgs by  navArgs()
    //custom choose category dialog
    private val categoryAdapter = CategoryAdapter { categoryId:String, isChecked:Boolean ->
        toggleCategory(categoryId, isChecked)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //TODO save checked state and implement custom items

        val categories = args.categories.toList()/*.map {
            "${it.title} (${it.articlesCount})"
        }.toTypedArray()*/

        selectedCategories.clear()
        selectedCategories.addAll(savedInstanceState?.getStringArray("checked") ?: args.selectedCategories)

        categoryAdapter.submitList(
            categories.map { it.toCategoryDataItem(selectedCategories.contains(it.categoryId)) }
        )

        /*val checked = BooleanArray(args.categories.size){
            args.selectedCategories.contains(args.categories[it].categoryId)
        }*/

        val listView = layoutInflater.inflate(R.layout.fragment_chose_category_dialog, null) as RecyclerView
        listView.adapter = categoryAdapter

        val adb = AlertDialog.Builder(requireContext())
            .setView(listView)
            .setTitle("Choose category")
            .setPositiveButton("Applay"){_,_ ->
                viewModel.applyCategories(selectedCategories)
            }
            .setNegativeButton("Reset"){_,_ ->
                viewModel.applyCategories(emptyList())
            }
            /*.setMultiChoiceItems(categories, checked){dialog, which, isChecked ->
                if (isChecked) selectedCategories.add(args.categories[which].categoryId)
                else selectedCategories.remove(args.categories[which].categoryId)
            }*/
        return adb.create()
    }

    private fun toggleCategory(categoryId: String, isChecked: Boolean) {

        if (isChecked) selectedCategories.add(categoryId)
        else selectedCategories.remove(categoryId)

        val categories = args.categories.toList()

        categoryAdapter.submitList(
            categories.map {
                it.toCategoryDataItem(selectedCategories.contains(it.categoryId))
            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray("checked", selectedCategories.toTypedArray())
        super.onSaveInstanceState(outState)
    }
}

class CategoryAdapter(
    private val listener: (String,Boolean) -> Unit
): ListAdapter<CategoryDataItem, CategoryVH>(CategoryDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        return CategoryVH(LayoutInflater
                                .from(parent.context)
                                .inflate(R.layout.item_chose_category_dialog,parent,false), listener)
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.bind(getItem(position))
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryDataItem>(){
    override fun areItemsTheSame(oldItem: CategoryDataItem, newItem: CategoryDataItem): Boolean {
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: CategoryDataItem, newItem: CategoryDataItem): Boolean {
        return oldItem == newItem
    }
}

class CategoryVH(
    override val containerView: View,
    val listener: (String, Boolean) -> Unit
) : RecyclerView.ViewHolder(containerView),LayoutContainer{

    fun bind(item: CategoryDataItem) {
        containerView.ch_select.isChecked = item.isChecked
        Glide.with(containerView.context)
            .load(item.icon)
            .apply(RequestOptions.circleCropTransform())
            .override(containerView.iv_icon.width)
            .into(containerView.iv_icon)
        containerView.tv_category.text = item.title
        containerView.tv_count.text = "${item.articlesCount}"
        containerView.ch_select.setOnCheckedChangeListener { _, checked ->
            listener(item.categoryId, checked)
        }
        itemView.setOnClickListener { containerView.ch_select.toggle() }
    }
}

data class CategoryDataItem(
    val categoryId: String,
    val icon: String,
    val title: String,
    val articlesCount: Int = 0,
    val isChecked: Boolean = false
)

private fun CategoryData.toCategoryDataItem(isChecked: Boolean = false) = CategoryDataItem(
    categoryId = categoryId,
    icon = icon,
    title = title,
    articlesCount = articlesCount,
    isChecked = isChecked
)
