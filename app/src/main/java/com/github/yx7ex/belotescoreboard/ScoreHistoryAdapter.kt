package com.github.yx7ex.belotescoreboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScoreHistoryAdapter(
    private val roundScores: List<RoundScore>
) : RecyclerView.Adapter<ScoreHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roundNumberTextView: TextView = view.findViewById(R.id.textview_item_round_number)
        val team1ScoreTextView: TextView = view.findViewById(R.id.textview_item_team1_score)
        val team2ScoreTextView: TextView = view.findViewById(R.id.textview_item_team2_score)
        val bidTextView: TextView = view.findViewById(R.id.textview_item_bid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_round_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val roundScore = roundScores[position]
        holder.roundNumberTextView.text = "${roundScore.roundNumber}."
        holder.team1ScoreTextView.text = roundScore.team1Score.toString()
        holder.team2ScoreTextView.text = roundScore.team2Score.toString()
        holder.bidTextView.text = roundScore.bid // Changed from roundScore.bid.toString()
    }

    override fun getItemCount(): Int {
        return roundScores.size
    }
}
