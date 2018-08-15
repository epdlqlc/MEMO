package com.example.davidpark.myapplication

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.davidpark.myapplication.utilities.NetworkUtils
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.login_memo.*
import kotlinx.android.synthetic.main.register.*
import java.text.SimpleDateFormat
import java.util.*

class ACT2 : AppCompatActivity() {
    var text:String ?= null
    var id:String? = null
    var titl:String?= null
    var date_c:String?= null
    var date:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_memo)

        var calendar = Calendar.getInstance()
        var mdformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        titl = intent.getStringExtra("TITLE")
        date_c = intent.getStringExtra("dtc")
        date = mdformat.format(calendar.getTime())

        var pref: SharedPreferences = getSharedPreferences("MEMO", Activity.MODE_PRIVATE)
        id = pref.getString("ID",null)

        if(date_c != null) DownloadTask().execute()
        else textView4.setText(date)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        UploadTask().execute()
    }


    inner class UploadTask: AsyncTask<Void, String, String?>(){
        override fun onPreExecute() {
        }
        override fun doInBackground(vararg p0: Void?): String? {
            var calendar = Calendar.getInstance()
            var mdformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            var map = HashMap<String, String>()
            map["id"] = id!!
            map["memo"] = editText2.getText().toString()!!
            map["date"] = date!!
            map["title"] = titl!!
            if(date_c == null) map["date_c"] = date!!
            else map["date_c"] = date_c!!

            var url = NetworkUtils.buildUrl("insert.php", map)

            var String = NetworkUtils.getResponseFromHttpUrl(url)

            return String
        }

        override fun onPostExecute(result: String?) {
            if(result == "1") {
                Toast.makeText(applicationContext, "저장되었습니다.", Toast.LENGTH_LONG).show()
                PushNotification().execute()
            }
        }
    }

    inner class PushNotification: AsyncTask<Void, String, String?>(){
        override fun onPreExecute() {
        }
        override fun doInBackground(vararg p0: Void?): String? {
            var map = HashMap<String, String>()
            map["title"] = "'"+titl+"'의 메모가 수정되었습니다."
            map["body"] = "수정 시간 : "+ date!!

            var url = NetworkUtils.buildUrl("push_notification.php", map)

            var String = NetworkUtils.getResponseFromHttpUrl(url)

            return String
        }

        override fun onPostExecute(result: String?) {
            if(result == "1") {
                Toast.makeText(applicationContext, "저장되었습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class DownloadTask: AsyncTask<Void, String, Array<Data>?>(){
        override fun onPreExecute() {
            //
            // swipe_layout_reviewlist.isRefreshing = true
        }
        override fun doInBackground(vararg p0: Void?): Array<Data>? {
            var map = HashMap<String, String>()
            map["id"] = id!!
            map["date_c"] = date_c!!

            var url = NetworkUtils.buildUrl("search.php", map)

            var DataListJSONString = NetworkUtils.getResponseFromHttpUrl(url)
            var ret = DataJSONUtils.getDataDataFromJSON(DataListJSONString)
            Log.v("test", DataListJSONString)
            return ret
        }

        override fun onPostExecute(result: Array<Data>?) {
            if(result != null){
                textView4.setText(result[0].memo_date)
                editText2.setText(result[0].memo_cont)
            }
            else {
                Log.v("null", "NULL")
            }
        }
    }

}
