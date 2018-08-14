package com.example.davidpark.myapplication

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.register.*
import android.widget.Toast
import com.example.davidpark.myapplication.utilities.NetworkUtils


class ACT3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
    }

    var name: String? = null
    var id: String? = null
    var pw: String? = null

    fun regis(view: View) {
        name = input_name.getText().toString()
        id = input_id.getText().toString()
        pw = input_pw.getText().toString()

        JoinTask().execute()
    }

    inner class JoinTask: AsyncTask<Void, String, String?>(){
        override fun onPreExecute() {
            progressBar.visibility=View.VISIBLE
        }
        override fun doInBackground(vararg p0: Void?): String? {
            var map = HashMap<String, String>()
            map["id"] = id!!
            map["pw"] = pw!!
            map["name"] = name!!

            var url = NetworkUtils.buildUrl("join.php", map)

            var String = NetworkUtils.getResponseFromHttpUrl(url)

            return String
        }

        override fun onPostExecute(result: String?) {
            progressBar.visibility=View.INVISIBLE
            if(result == "1") {
                Toast.makeText(applicationContext, "회원가입 되었습니다.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

}
