 package com.example.kotlinmessenger.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.registerlogin.RegisterActivity
import com.example.kotlinmessenger.modules.ChatMessage
import com.example.kotlinmessenger.modules.User
import com.example.kotlinmessenger.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

 class LatestMessagesActivity : AppCompatActivity() {

     companion object {
         var currentUser: User? = null
         val TAG = "LatestMessages"
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)


        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // при нажатии на диалог, переходить в него
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "Clicking on user")
            val intent = Intent(this, ChatLogActivity::class.java)
            val row =  item as LatestMessageRow
            row.chatPartnerUser
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)

        }

        fetchCurrentUser()

        verifyUserIsLoggedIn()

        listenForLatestMessages()


    }



     val latestMessagesMap = HashMap<String, ChatMessage>() // загрузка сообщейний хэш картой

     private fun    refreshRecyclerViewMessages() {
         adapter.clear()
         latestMessagesMap.values.forEach{
             adapter.add(LatestMessageRow(it))

         }

     }


     private fun  listenForLatestMessages() { // получение последних сообщений из БД
         val fromId = FirebaseAuth.getInstance().uid
         val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
         ref.addChildEventListener(object: ChildEventListener {
             override fun onCancelled(p0: DatabaseError) {

             }

             override fun onChildRemoved(p0: DataSnapshot) {

             }

             override fun onChildMoved(p0: DataSnapshot, p1: String?) {

             }

             override fun onChildAdded(p0: DataSnapshot, p1: String?) { // добавление последних сообщений
                 val chatMessage = p0.getValue(ChatMessage::class.java) ?: return // unwrap

                 latestMessagesMap[p0.key!!] = chatMessage  // key относится к пользователю, которому пишут
                 refreshRecyclerViewMessages()




             }

             override fun onChildChanged(p0: DataSnapshot, p1: String?) { // добавление новых последних сообщений
                 val chatMessage = p0.getValue(ChatMessage::class.java) ?: return // unwrap

                 latestMessagesMap[p0.key!!] = chatMessage
                 refreshRecyclerViewMessages()


             }
         })
     }

     val adapter = GroupAdapter<ViewHolder>()



     private fun fetchCurrentUser() { // получения пользователей
         val uid = FirebaseAuth.getInstance().uid
         val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
         ref.addListenerForSingleValueEvent(object: ValueEventListener{
             override fun onCancelled(p0: DatabaseError) {

             }

             override fun onDataChange(p0: DataSnapshot) {
                 currentUser = p0.getValue(User::class.java)
                 Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")

             }
         })
     }

     private fun verifyUserIsLoggedIn(){ //Проверить, залогинен ли пользователь в firebase
         val uid = FirebaseAuth.getInstance().uid
         if (uid == null) {
             val intent = Intent(this, RegisterActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // очистить предыдущие активности
             startActivity(intent)
         }

     }

     override fun onOptionsItemSelected(item: MenuItem?): Boolean { // метод запускается при нажатии кнопок
         when (item?.itemId) { // switch cases
             R.id.menu_new_message -> {
                 val intent = Intent(this, NewMessageActivity::class.java)
                 startActivity(intent)

           }
             R.id.menu_sign_out -> {
              FirebaseAuth.getInstance().signOut()
                 val intent = Intent(this, RegisterActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) // очистить предыдущие активности
                 startActivity(intent)
             }
         }
         return super.onOptionsItemSelected(item)
     }

     override fun onCreateOptionsMenu(menu: Menu?): Boolean { // создание меню (кнопка назад и новое смс)
         menuInflater.inflate(R.menu.nav_menu, menu)
         return super.onCreateOptionsMenu(menu)
     }
}
