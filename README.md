# Kotlin Messenger

Fully functional chat messaging application for Android

**Technologies used:**
- Kotlin
- Firebase (User authentication and file storage are implemented through Firebase, using the capabilities of Storage and Realtime Database)

**Libraries used:**
- Groupie - simple, flexible library for complex RecyclerView layouts.
- Picasso - powerful image download and caching library for Android.

**Description of the logic for registering users, loading data, users and their messages into an online database:**
- To register and authenticate users, the application uses the Firebase Authentication tool. To add users, the registration method using email and the user password was chosen (possible to add registration parameters using Google, Facebook, Twitter accounts). In the project console at console.firebase.com, it's possible to view and manage user data.
- Data loading is carried out using the Firebase Storage tool, this is useful for the project 'cause online storage stores user profile images.
- Uploading users and their messages takes place through the Firebase Realtime Database tool. Three sections are stored in the online database: user, user messages, and last sent user messages. Each message and user has their own identifier.
- The user has three fields in the database: user profile image, unique user identification number and user username.
- Storing user messages in the database: each user has a chat tab with another user, and there are folders with information about each message in it.
- Information about the message stores: message sending time, message text, unique message number, unique message sender number and unique message recipient number.
- The Recent Messages section exists to display the latest messages from it on the user's last chat screen. Information about the last message is also stored: the time the message was sent, the message text, the unique message number, the unique number of the message sender and the unique number of the message recipient.

**Demo**

![](demo/demos.png)

![](demo/demos1.png)
