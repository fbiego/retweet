package com.fbiego.tweet.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fbiego.tweet.R

class ActionAdapter (actionData: ArrayList<ActionData>, private val callback: (ActionData) -> Unit)
    : RecyclerView.Adapter<ActionAdapter.DataHolder>() {

    private val data = mutableListOf<ActionData>()
    init {
        data.addAll(actionData)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DataHolder {
        val inflater = LayoutInflater.from(p0.context)
        val view = inflater.inflate(R.layout.action_item, p0, false)
        return DataHolder(view, p0.context)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(p0: DataHolder, p1: Int) {
        p0.bind(data[p1], callback)
    }

    fun update(actionData: ArrayList<ActionData>){
        this.data.clear()
        this.data.addAll(actionData)
        this.notifyDataSetChanged()
    }

    class DataHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView){
        private val mText: TextView = itemView.findViewById(R.id.action_text)
        private val mBtext: TextView = itemView.findViewById(R.id.action_button_text)
        private val mBicon: ImageView = itemView.findViewById(R.id.action_button_icon)
        private val mButton: LinearLayout = itemView.findViewById(R.id.action_button)

        fun bind (action: ActionData, callback: (ActionData) -> Unit){

            mText.text = action.text
            mBtext.text = action.buttonText

            if (action.actionable){
                mButton.visibility = View.VISIBLE
            } else {
                mButton.visibility = View.GONE
            }
            mButton.setOnClickListener {
                callback(action)
            }
            if (action.complete) {
                mBicon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_done))
            } else {
                mBicon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_next))
            }


        }

    }
}