package com.example.examsos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examsos.dataValue.NotificationDataClass
import com.example.examsos.R

class NotificationAdapter(private val dataList: ArrayList<NotificationDataClass>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolderClass>() {

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val announcementHeader: TextView = itemView.findViewById(R.id.notification_header)
        private val announcementText: TextView = itemView.findViewById(R.id.notification_text)

        fun bind(notification: NotificationDataClass) {
            announcementHeader.text = notification.dataTitle
            announcementText.text = notification.dataDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val view =  LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item_layout, parent, false)
        return ViewHolderClass(view)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val notification = dataList[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}