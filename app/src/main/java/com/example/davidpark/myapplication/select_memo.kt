package com.example.davidpark.myapplication

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.davidpark.myapplication.utilities.NetworkUtils
import kotlinx.android.synthetic.main.select_memo.*
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.kakao.kakaotalk.callback.TalkResponseCallback
import com.kakao.kakaotalk.response.KakaoTalkProfile
import com.kakao.kakaotalk.v2.KakaoTalkService
import kotlinx.android.synthetic.main.edit_text_alert.*
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import java.net.URL


class select_memo : AppCompatActivity() , CellAdapter.CellListOnClickListener, CellAdapter.CellListOnLongClickListener {


    private var mRecyclerView: RecyclerView? = null
    private var mCellAdapter: CellAdapter? = null

    var id:String? = null
    var date_c2:String?= null
    var Titl:String?=null
    var ti:String?= null
    var Name:String?=null
    var imageUrl:String?  = null

    internal var dlDrawer: DrawerLayout? = null
    internal var dtToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = "https://k.kakaocdn.net/dn/brbCHP/btqnvlCzQqL/mOb0iHmfIMKJNmNkpWmtlk/profile_640x640s.jpg"
        requestProfile()
        setContentView(R.layout.select_memo)

        var pref: SharedPreferences = getSharedPreferences("MEMO", Activity.MODE_PRIVATE)
        id = pref.getString("ID",null)

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mCellAdapter = CellAdapter(this, this)
        mRecyclerView!!.layoutManager = layoutManager
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.adapter = mCellAdapter

        ShowMemoTask().execute()

        fab.setOnClickListener {
            val ad = AlertDialog.Builder(this)
            ti = null
            ad.setTitle("제목을 정해주세요.")       // 제목 설정

            ad.setView(R.layout.edit_text_alert)

            ad.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                var f: Dialog = dialog as Dialog
                var input: EditText = f.findViewById(R.id.eText)
                ti = input.getText().toString()

                if(ti == "") Toast.makeText(getApplicationContext(),"제목을 입력해 주세요.",Toast.LENGTH_LONG).show()
                else {
                    dialog.dismiss()
                    var aaa = Intent(this, ACT2::class.java)
                    aaa.putExtra("TITLE",ti)
                    startActivity(aaa)
                }
            })

            ad.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            ad.show()
        }

        swipe_layout.setOnRefreshListener {
            ShowMemoTask().execute()
        }

        this.log_out.setOnClickListener {
            onClickLogout()
        }

        dlDrawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        dtToggle = ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name)
        dlDrawer!!.setDrawerListener(dtToggle)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (dtToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dtToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dtToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        ShowMemoTask().execute()
    }

    override fun cellClicked(position: Int) {
        var mCellData = mCellAdapter!!.getCellData()
        var aaa = Intent(this, ACT2::class.java)
        aaa.putExtra("TITLE",mCellData!![position].memo_name)
        aaa.putExtra("dtc",mCellData!![position].memo_date_c)
        startActivity(aaa)
    }

    override fun cellLongClicked(position: Int) {
        var mCellData = mCellAdapter!!.getCellData()
        date_c2 = mCellData!![position].memo_date_c
        Titl = mCellData!![position].memo_name

        val items = arrayOf<CharSequence>("제목 변경","삭제","정보")
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("'"+Titl+"'"+"에 수행할 작업을 선택해주세요.")

        alertDialogBuilder.setItems(items
        ) { dialog, id ->
            if(id == 0) {
                val ad = AlertDialog.Builder(this)
                ad.setTitle("새로운 제목을 정해주세요.")       // 제목 설정
                ad.setView(R.layout.edit_text_alert)

                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.edit_text_alert, null)
                ad.setView(dialogView)

                val input = dialogView.findViewById<EditText>(R.id.eText)
                input.setText(Titl)
                ti = null

                ad.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    ti = input.getText().toString()

                    if(ti == "") Toast.makeText(getApplicationContext(),"제목을 입력해 주세요.",Toast.LENGTH_LONG).show()
                    else {
                        dialog.dismiss()
                        Rename_Task().execute()
                    }
                })

                ad.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                ad.show()
            }
            if(id == 1) {
                DeleteTask().execute()
                Toast.makeText(applicationContext,"삭제되었습니다.",Toast.LENGTH_SHORT).show()
            }
            if(id == 2) {
                val ad = AlertDialog.Builder(this)
                ad.setTitle("정보")
                ad.setMessage("생성된 날짜 : "+date_c2)
                ad.show()
            }
        }

        alertDialogBuilder.show()

    }


    inner class ShowMemoTask: AsyncTask<Void, String, Array<Data>?>(){
        override fun onPreExecute() {
            swipe_layout.isRefreshing = true
        }
        override fun doInBackground(vararg p0: Void?): Array<Data>? {
            var map = HashMap<String, String>()
            map["id"] = id!!
            var url = NetworkUtils.buildUrl("show_memo.php", map)

            var DataListJSONString = NetworkUtils.getResponseFromHttpUrl(url)
            var ret = DataJSONUtils.getDataDataFromJSON(DataListJSONString)

            return ret
        }

        override fun onPostExecute(result: Array<Data>?) {
            if(result != null){
                mCellAdapter!!.setCellData(result)
            }
            else {
                Log.v("null", "NULL")
            }
            swipe_layout.isRefreshing = false
        }
    }


    inner class DeleteTask: AsyncTask<Void, String, String>(){
        override fun onPreExecute() {
            swipe_layout.isRefreshing = true
        }
        override fun doInBackground(vararg p0: Void?): String? {
            var map = HashMap<String, String>()
            map["id"] = id!!
            map["date_c"] = date_c2!!
            var url = NetworkUtils.buildUrl("delete.php", map)

            var ret = NetworkUtils.getResponseFromHttpUrl(url)

            return ret.toString()
        }

        override fun onPostExecute(result: String) {
            if(result != null){
                ShowMemoTask().execute()
            }
            else {
                Log.v("null", "NULL")
            }
            swipe_layout.isRefreshing = false
        }
    }

    inner class Rename_Task: AsyncTask<Void, String, String>(){
        override fun onPreExecute() {
            swipe_layout.isRefreshing = true
        }
        override fun doInBackground(vararg p0: Void?): String? {
            var map = HashMap<String, String>()
            map["id"] = id!!
            map["date_c"] = date_c2!!
            map["title"] = ti!!
            var url = NetworkUtils.buildUrl("update_title.php", map)

            var ret = NetworkUtils.getResponseFromHttpUrl(url)

            return ret.toString()
        }

        override fun onPostExecute(result: String) {
            if(result != null){
                ShowMemoTask().execute()
            }
            else {
                Log.v("null", "NULL")
            }
            swipe_layout.isRefreshing = false
        }
    }

    private fun requestProfile() {
        Log.v("aaaaaaaa","aaaaa")
        KakaoTalkService.getInstance().requestProfile(object :  KakaoTalkResponseCallback<KakaoTalkProfile>() {
            override fun onSuccess(talkProfile: KakaoTalkProfile) {
                Log.v("aaaaaa",""+imageUrl)
                Name = talkProfile.nickName.toString()
                Log.v("aaaaaa",""+Name)
                if ( talkProfile.thumbnailUrl.toString() != "" ) {
                    imageUrl = talkProfile.thumbnailUrl.toString()
                }
                Log.v("aaa","바보"+imageUrl)
                a()
            }
        })
    }




    fun onClickLogout() {
        UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
            override fun onCompleteLogout() {
                redirectLoginActivity()
            }
        })
    }

    private fun redirectLoginActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun a() {
        Thread(Runnable {
            // performing some dummy time taking operation
            var ret = getImage()

            // try to touch View of UI thread
            this.runOnUiThread(java.lang.Runnable {
                this.Profile_image.setBackground(BitmapDrawable(ret))
            })
        }).start()
        var ab: android.support.v7.app.ActionBar = this!!.supportActionBar!!
        ab!!.setTitle("'"+Name+"' 님의 메모장")
        this.Nick_name.setText(Name)
    }

    fun getImage(): Bitmap? {
        var ret: Bitmap? = null
        var sendURL = URL(imageUrl)
        ret = BitmapFactory.decodeStream(sendURL.openConnection().getInputStream())
        return ret
    }

    private abstract inner class KakaoTalkResponseCallback<T> : TalkResponseCallback<T>() {
        override fun onNotKakaoTalkUser() {
            Log.w("aaaaa","not a KakaoTalk user")
        }

        override fun onFailure(errorResult: ErrorResult?) {
            Log.e("aaaaa", "failure : " + errorResult!!)
        }

        override fun onSessionClosed(errorResult: ErrorResult) {
            Log.e("aaaaa", "failure on session closed: " + errorResult!!)
           // redirectLoginActivity()
        }

        override fun onNotSignedUp() {
            Log.e("aaaaa", "failure on not signedup")
            //redirectSignupActivity()
        }
    }

}
