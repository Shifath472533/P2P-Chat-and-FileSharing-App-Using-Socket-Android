# P2P-Chat-and-FileSharing-App-Using-Socket-Android
A peer to peer chat & File Sharing app using Sockets in Android.


# Description

<h2> Peer to Peer chat and file sharing system using Socket Programming </h2>

<h3> Features: </h3>


1. As it is a peer to peer chatting and file sharing application, we decided to create both server and client features each 
   side.
  
2. In the application, there is an portion <b> Your IP Address </b> where you can see your ip address. Basically it is the 
   address your router gave to you. There are 4 other options which you have to fill. 

3. <b> Your Port No. </b> requires the port number of your server that you want others to connect to your server. <b> Receiver's
   IP Address </b> requires the ip address of the other person. <b> Receiver's Port No. </b> requires the port number of the 
   server of other person s/he wants you to connect. In <b> Your Name </b>, you can give your name.

4. By clicking the <b> Connect </b> button, a pop up will occur. That will ask foe storage permission. It will occur only once.
   You have to permit for storage.

5. Now you will enter in the chat section. If you can connect to your port, you can see that on the upper part of the page, a 
   green line showing your ip address and port. If you can't then you will see a red line showing socket initialization failure 
   because the port is busy in other application. 
  
6. In the chat setion, there is a portion in the bottom to write messages. We used a listview to show the exchanged messages.
   There are two buttons. One on the left to select files and send. The other on the right to send messages. You can send any 
   type of message (any font or character or emojis). You can also send any type of files. The more the file size, the more time
   it takes to send and receive.
  
7. There is a <b> Menu </b> button on the top right corner. There is an option <b> Change Background Color </b> using which one
   can change the background of both user. Whenever a user leave the application, the current chat is being saved. All these
   saved files and the files that are received from other user is located in /Android/obb/com.example.p2pchatandfiletransfer .
