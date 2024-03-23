package com.example.chatapp.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.modal.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter: RecyclerView.Adapter<UserHolder>() {

    private val onlineStatus = "Online"
    private val offlineStatus = "Offline"

    private var listener: OnUserClickListener? = null

    private val listUser = AsyncListDiffer(this, UserDiffCallBack())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.userlistitem,parent,false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return  listUser.currentList.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {

        val user = listUser.currentList[position]

        val name = user.username!!.split("\\s".toRegex())[0]

        holder.name.text = name

        if ( user.status.equals(onlineStatus) ){
            holder.statusImageview.setImageResource(R.drawable.onlinestatus)
        }
        else{
            holder.imageProfile.setImageResource(R.drawable.offlinestatus)
        }

        Glide.with(holder.itemView.context).load(user.imageUrl).into(holder.imageProfile)

        holder.itemView.setOnClickListener{
            listener?.onUserSelected(position,user)
        }
    }

    fun setUserList( list: List<Users> ){
        listUser.submitList(list)
    }

    fun setOnUserClickListener(listener: OnUserClickListener){
        this.listener = listener
    }
}

class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val name :TextView = itemView.findViewById(R.id.userName)
    val imageProfile : CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageview: ImageView = itemView.findViewById(R.id.statusOnline)

}


private class UserDiffCallBack : DiffUtil.ItemCallback<Users>() {
    override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
        return oldItem.userid == newItem.userid
    }

    override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
        return true
    }

}


interface OnUserClickListener : OnRecentChatClicked {

    fun onUserSelected(position:Int, users: Users)

}