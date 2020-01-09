package com.example.kotlinmessenger.modules

class ChatMessage(val id:String, val text: String, val fromId: String, val toId: String, val timestamp: Long) { // то, что хранит сообщение
    constructor(): this("","", "", "", -1)
}