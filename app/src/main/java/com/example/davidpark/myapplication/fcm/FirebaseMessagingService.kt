package com.example.davidpark.myapplication.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        // 메세지가 올때 여기서 처리
        // 앱이 실행중일때 여기서 이벤트를 받음
        val bundle: Map<String, String> = p0!!.getData()
    }
}