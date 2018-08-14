package com.example.davidpark.myapplication

import org.json.JSONArray

object DataJSONUtils {
    fun getDataDataFromJSON(dataJSONstr: String?): Array<Data>?{
        var ret: Array<Data>? = null

        val dataArray = JSONArray(dataJSONstr)

        ret = Array<Data>(dataArray.length(), {Data("","", "", "")})

        for(i in 0 until dataArray.length()){
            val storedata = dataArray.getJSONObject(i)

            ret[i].memo_name = storedata.getString("title")
            ret[i].memo_date = storedata.getString("date")
            ret[i].memo_date_c = storedata.getString("date_c")
            ret[i].memo_cont = storedata.getString("memo")
        }

        return ret
    }
}