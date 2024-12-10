package com.example.examsos.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.examsos.R
import com.example.examsos.dataValue.QuizRecord

class QuizRecordsAdapter(private val quizRecords: List<QuizRecord>) :
    RecyclerView.Adapter<QuizRecordsAdapter.QuizRecordViewHolder>() {

    // Create a ViewHolder class
    inner class QuizRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val difficultyTextView: TextView = itemView.findViewById(R.id.difficultyTextView)
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val winTextView: TextView = itemView.findViewById(R.id.winTextView)
        val attemptedQuestionsTextView: TextView = itemView.findViewById(R.id.attemptedQuestionNumberTextView)
        val livesRemainingTextView: TextView = itemView.findViewById(R.id.livesRemainingTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestamp)
        val notificationIcon: ImageView = itemView.findViewById(R.id.note_item_icon)

    }

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizRecordViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.quiz_record_item, parent, false)
        return QuizRecordViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: QuizRecordViewHolder, position: Int) {
        val record = quizRecords[position]
        holder.difficultyTextView.text = "Difficulty: ${record.difficulty}"
        holder.scoreTextView.text = "Score: ${record.score}"
        holder.categoryTextView.text = "${record.category}"
        holder.winTextView.text = if (record.isWin) "COMPLETED" else "LOSS"
        holder.attemptedQuestionsTextView.text = "Attempted Questions: ${record.totalQuestions}"
        holder.livesRemainingTextView.text = "Lives Remaining: ${record.livesRemaining}"
        val formattedTimestamp = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", record.timestamp)
        holder.timestampTextView.text = "Accessed Date: $formattedTimestamp"

        if (record.isWin) {
            holder.notificationIcon.setImageResource(R.drawable.good_leaf)
            holder.winTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.theme_primary))
        } else {
            holder.notificationIcon.setImageResource(R.drawable.bad_leaf)
            holder.winTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))

        }
    }

    override fun getItemCount(): Int = quizRecords.size
}
