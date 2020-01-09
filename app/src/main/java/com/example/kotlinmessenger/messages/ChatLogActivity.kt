package com.example.kotlinmessenger.messages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.modules.ChatMessage
import com.xwray.groupie.GroupAdapter
import com.example.kotlinmessenger.modules.User
import com.example.kotlinmessenger.views.ChatFromItem
import com.example.kotlinmessenger.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>() // для отображения сообщений в чате

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recycleview_chat_log.adapter = adapter // добавление объектов адаптером


        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)  // передаём пользователя, чтобы взять имя для топ бара
        supportActionBar?.title = toUser?.username // имя пользователя в топ баре



        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG,"Attempt to send message")
            performSendMessage()
        }
    }

    private fun listenForMessages() { // загрузка предыдущих сообщений из БД
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
               val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                Log.d (TAG, chatMessage.text)

                    // проверка на id пользователя, чтобы выставить сообщения по сторонам

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser
                            ?: return // ?: retuen всё равно что !!
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {

                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

            }
                recycleview_chat_log.scrollToPosition(adapter.itemCount -1) // скролить к последним сообщениям
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }



    private fun  performSendMessage(){ // отправка сообщений, Firebase


        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid // id отправителя
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid // id получателя

        if (fromId == null) return

       // val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push() // отправитель
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push() // получатель


        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000 )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved chat message: ${reference.key}")
                edittext_chat_log.text.clear() // очистка поля ввода сообщений
                recycleview_chat_log.scrollToPosition(adapter.itemCount -1) // при отправке скролиться к последнему сообщению
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId") // сохранение последних сообщений
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId") // сохранение последних сообщений
        latestMessageRef.setValue(chatMessage)

    }


}


