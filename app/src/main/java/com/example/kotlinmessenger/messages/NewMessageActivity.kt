package com.example.kotlinmessenger.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinmessenger.R
import android.util.Log
import com.example.kotlinmessenger.modules.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
/*
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(UserItem())

        recyclerview_newmessage.adapter = adapter
*/

     fetchUsers()

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }


    private fun fetchUsers() { // добавить пользователя из firebase database
       val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) { // вызывается каждый раз когда берётся пользователь из БД

              val adapter = GroupAdapter<ViewHolder>() // для создания списка пользователей



               p0.children.forEach{
                 Log.d("NewMessasge", it.toString())
                   val user = it.getValue(User::class.java)
                   if (user != null) {
                       adapter.add(UserItem(user))
                   }
               }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java) // открываем чат лог
                   // intent.putExtra(USER_KEY,userItem.user.username ) // добавление имени пользвателя в топ
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish() // возврат в главное меню после выхода из чата
                }
                
                recyclerview_newmessage.adapter = adapter //вывод пользователей в новом окне
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) { // вызывается для кажлого объекта пользователя в списке
         viewHolder.itemView.username_textview_new_message.text = user.username //отображение имени пользваотеля в списке

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message) // загрузка фото профиля пользователей
    }
    override fun getLayout(): Int {
          return R.layout.user_row_new_message
    }
}
