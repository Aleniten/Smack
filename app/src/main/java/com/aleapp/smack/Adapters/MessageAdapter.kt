package com.aleapp.smack.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aleapp.smack.Model.Message
import com.aleapp.smack.R
import com.aleapp.smack.Services.UserDataService

class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view,p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(p0: ViewHolder, position: Int) {
        p0.bindMessage(context, messages[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val userName = itemView?.findViewById<TextView>(R.id.messageUserNameLbl)
        val timestamp = itemView?.findViewById<TextView>(R.id.timestampLbl)
        val messageBody = itemView?.findViewById<TextView>(R.id.messageBodyLbl)

        fun bindMessage(context: Context, message: Message){
            val resourceId = context.resources.getIdentifier(message.userAvatar,"drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timestamp?.text = message.timeStamp
            messageBody?.text = message.message
        }

    }
}