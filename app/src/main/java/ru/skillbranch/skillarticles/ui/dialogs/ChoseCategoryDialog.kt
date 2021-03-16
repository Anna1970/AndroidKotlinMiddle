package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel


class ChoseCategoryDialog : DialogFragment() {
    private val viewModel: ArticlesViewModel by activityViewModels()
    private val selected = mutableListOf<String>()
    private val args : ChoseCategoryDialogArgs by  navArgs()
    //custom choose category dialog
    private val categoryAdapter = CategoryAdapter { categoryId:String, isChecked:Boolean ->
        if (isChecked) selected.add(categoryId)
        else selected.remove(categoryId)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        selected.clear()
        selected.addAll(savedInstanceState?.getStringArray("checked") ?: args.selectedCategories)

        val categoryItems = args.categories.map {it.toItem((selected.contains(it.categoryId)))}

        categoryAdapter.submitList(categoryItems)

        val listView = layoutInflater.inflate(R.layout.fragment_chose_category_dialog, null) as RecyclerView

        listView.layoutManager = LinearLayoutManager(requireContext())
        listView.adapter = categoryAdapter

        return AlertDialog.Builder(requireContext())
            .setTitle("Choose category")
            .setPositiveButton("Apply") { dialog: DialogInterface?, which: Int ->
                viewModel.applyCategories(selected)
            }
            .setNegativeButton("Reset") { _, _ ->
                viewModel.applyCategories(emptyList())
            }
            .setView(listView)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray("checked", selected.toTypedArray())
        super.onSaveInstanceState(outState)
    }
}