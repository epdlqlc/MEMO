package com.example.davidpark.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.example.davidpark.myapplication.ACT2
import com.example.davidpark.myapplication.ACT3
import com.example.davidpark.myapplication.R
import com.example.davidpark.myapplication.utilities.NetworkUtils
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Base64.NO_WRAP
import android.provider.SyncStateContract.Helpers.update
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.util.Log
import android.view.inputmethod.InputMethod
import com.kakao.auth.Session.getCurrentSession
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger



class MainActivity : AppCompatActivity() {

    private var callback: SessionCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()

        var pref: SharedPreferences = getSharedPreferences("MEMO", Activity.MODE_PRIVATE)

        switch_.setChecked(pref.getBoolean("Switch", false))

        if (switch_.isChecked) {
            input_id.setText(pref.getString("ID", ""))
            input_pw.setText(pref.getString("PW", ""))
        }
    }

    var id: String? = null
    var pw: String? = null

    fun login(view: View) {
        id = input_id.getText().toString()
        pw = input_pw.getText().toString()

        LoginTask().execute()
    }

    fun next() {
        var pref: SharedPreferences = getSharedPreferences("MEMO", Activity.MODE_PRIVATE)
        var editor: SharedPreferences.Editor = pref.edit()

        Toast.makeText(applicationContext, "로그인 되었습니다.", Toast.LENGTH_LONG).show()
        editor.putString("ID", id)
        editor.putString("PW", pw)
        editor.putBoolean("Switch", switch_.isChecked)
        editor.commit()
        var aaa = Intent(this, select_memo::class.java)
        startActivity(aaa)
        finish()
    }

    fun no() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("로그인 오류")
        builder.setMessage("아이디 혹은 패스워드가 틀렸습니다.")
        builder.setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which -> })
        builder.show()
    }

    fun regis(view: View) {
        var aaa = Intent(this, ACT3::class.java)
        startActivity(aaa)
    }

    inner class LoginTask : AsyncTask<Void, String, String?>() {
        override fun onPreExecute() {
        }

        override fun doInBackground(vararg p0: Void?): String? {
            var map = HashMap<String, String>()
            map["id"] = id!!
            map["pw"] = pw!!

            var url = NetworkUtils.buildUrl("login.php", map)

            var String = NetworkUtils.getResponseFromHttpUrl(url)

            return String
        }

        override fun onPostExecute(result: String?) {
            if (result != "0") {
                next()
            } else {
                no()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
    }

    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
                redirectSignupActivity()
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            if (exception != null) {
                Log.d("error", "Session Fail Error is " + exception.message.toString())
            }
        }
    }

    protected fun redirectSignupActivity() {
        val intent = Intent(this,
                select_memo::class.java)
        startActivity(intent)
        finish()
    }

}

