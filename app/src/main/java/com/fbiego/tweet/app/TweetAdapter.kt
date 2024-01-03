package com.fbiego.tweet.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fbiego.tweet.R

class TweetAdapter(myData: ArrayList<TweetData>): RecyclerView.Adapter<TweetAdapter.DataHolder>(){
    private val data = mutableListOf<TweetData>()
    init {
        data.addAll(myData)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.tweet_item, parent, false)
        return DataHolder(view)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun update(dataList: ArrayList<TweetData>){
        this.data.clear()
        this.data.addAll(dataList)
        this.notifyDataSetChanged()
    }

    class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val mOrigin: TextView = itemView.findViewById(R.id.originText)
        private val mTime: TextView = itemView.findViewById(R.id.timeText)
        private val mRetweet: TextView = itemView.findViewById(R.id.retweetText)

        fun bind(data: TweetData){
            mOrigin.text = data.origin
            mTime.text = time(data.time)
            mRetweet.text = data.retweet
        }

    }
}