package com.example.davidpark.myapplication.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseInstanceIDService : FirebaseInstanceIdService() {

    private val TAG: String = "MyFirebaseIDService"

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val refreshedToken: String = FirebaseInstanceId.getInstance().getToken()!!
        Log.d(TAG, "Refreshed token: " + refreshedToken)

        // 토큰이 바뀌면 여기에서 이벤트를 받음
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String) {
        // 서버에 저장
    }
}