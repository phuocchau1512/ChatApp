package com.example.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.modal.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter: RecyclerView.Adapter<RecentChatHolder>() {

    private var listener: OnRecentChatClicked? = null
    private var recentModel = RecentChats()

    private val listRecentChat = AsyncListDiffer(this, RecentChatDiffCallBack())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist,parent,false)
        return RecentChatHolder(view)
    }

    override fun getItemCount(): Int {
        return listRecentChat.currentList.size
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {

        val recentChat = listRecentChat.currentList[position]

        recentModel = recentChat

        holder.userName.text = recentChat.name

        val message = recentChat.message!!.split(" ").take(4).joinToString(" ")
        val makeLastMessage = "${recentChat.person}: $message"

        holder.lastMessage.text = makeLastMessage

        Glide.with(holder.itemView.context).load(recentChat.friendImage).into(holder.imageView)

        holder.timeView.text = recentChat.time!!.substring(0,5)

        holder.itemView.setOnClickListener{
            listener!!.getOnRecentChatClick(position,listRecentChat.currentList)
        }
    }

    fun setRecentChatList(list:List<RecentChats>){
        listRecentChat.submitList(list)
    }

    fun setOnRecentChatListener(listener: OnUserClickListener){
        this.listener=listener
    }
}

class RecentChatHolder(itemView: View): ViewHolder(itemView) {
    val imageView:CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName:TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage:TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView:TextView = itemView.findViewById(R.id.recentChatTextTime)
}

class RecentChatDiffCallBack: DiffUtil.ItemCallback<RecentChats>() {
    override fun areItemsTheSame(oldItem: RecentChats, newItem: RecentChats): Boolean {
       return oldItem.friendId == newItem.friendId
    }

    override fun areContentsTheSame(oldItem: RecentChats, newItem: RecentChats): Boolean {
        return true
    }

}

interface OnRecentChatClicked{
    fun getOnRecentChatClick(position:Int, recentChats: MutableList<RecentChats>)
}