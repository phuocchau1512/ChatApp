package com.example.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.Utils
import com.example.chatapp.modal.Messages

class MessageAdapter: RecyclerView.Adapter<MessageHolder>() {

    private val left = 0
    private val right = 1
    private val listMessage = AsyncListDiffer(this, MessDiffCallBack())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {

        val inflater = LayoutInflater.from(parent.context)
        val layoutSource = if ( viewType == right ) R.layout.chatitemright else R.layout.chatitemleft
        val view = inflater.inflate(layoutSource,parent,false)
        return MessageHolder(view)
    }

    override fun getItemCount(): Int {
        return listMessage.currentList.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {

        val message = listMessage.currentList[position]

        holder.messageText.visibility = View.VISIBLE
        holder.messageText.visibility = View.VISIBLE

        holder.messageText.text = message.content
        holder.timeOfSend.text = Utils.getDisplayTime(message.time!!)

    }

    override fun getItemViewType(position: Int): Int {
        return if ( listMessage.currentList[position].sender == Utils.getUiLoggedIn() ){
            1
        } else 0
    }

    fun setMessageList(newList:List<Messages>){
        listMessage.submitList(newList)
    }


}

private class MessDiffCallBack : DiffUtil.ItemCallback<Messages>() {
    override fun areItemsTheSame(oldItem: Messages, newItem: Messages): Boolean {
        return oldItem.time == newItem.time && oldItem.sender == newItem.sender
                && oldItem.receiver == newItem.receiver
    }

    override fun areContentsTheSame(oldItem: Messages, newItem: Messages): Boolean {
        return oldItem.content == newItem.content
    }


}

class MessageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val messageText : TextView = itemView.findViewById(R.id.show_message)
    val timeOfSend: TextView = itemView.findViewById(R.id.timeView)
}