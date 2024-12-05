package com.example.examsos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examsos.dataValue.LevelDataClass
import com.example.examsos.R


class LevelAdapter(
    private val dataList: ArrayList<LevelDataClass>,
    private val clickListener: (LevelDataClass) -> Unit
) : RecyclerView.Adapter<LevelAdapter.ViewHolderClass>() {

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val levelHeader: TextView = itemView.findViewById(R.id.level_header)

        fun bind(levels: LevelDataClass, clickListener: (LevelDataClass) -> Unit) {
            levelHeader.text = levels.dataTitle
            itemView.setOnClickListener { clickListener(levels) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.level_item_layout, parent, false)
        return ViewHolderClass(view)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val notification = dataList[position]
        holder.bind(notification, clickListener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}