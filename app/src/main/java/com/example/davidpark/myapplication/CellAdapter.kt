package com.example.davidpark.myapplication

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.davidpark.myapplication.R
import java.lang.ref.WeakReference

class CellAdapter(private val mClickHandler: select_memo, private val mClickHandler2: select_memo)
    : RecyclerView.Adapter<CellAdapter.CellListAdapterViewHolder>() {

    private var mData: Array<Data>? = null

    interface CellListOnClickListener {
        fun cellClicked(position: Int)
    }

    interface CellListOnLongClickListener {
        fun cellLongClicked(position: Int)
    }
    override fun onBindViewHolder(holder: CellListAdapterViewHolder, position: Int) {
        val thisItem = mData!![position]

        holder.mName.text = thisItem.memo_name
        holder.mDate.text = thisItem.memo_date
        holder.mCont.text = thisItem.memo_cont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellListAdapterViewHolder{
        val context = parent.context
        val layoutIdForListItem = R.layout.cell
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately)
        return CellListAdapterViewHolder(view, mClickHandler, mClickHandler2)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    inner class CellListAdapterViewHolder(v: View, listener: CellListOnClickListener, listener2: CellListOnLongClickListener) : RecyclerView.ViewHolder(v), View.OnLongClickListener, View.OnClickListener {

        val listenerRef: WeakReference<CellListOnClickListener>
        val listenerRef2: WeakReference<CellListOnLongClickListener>
        var mName: TextView
        var mDate: TextView
        var mCont: TextView
        var mCell: LinearLayout

        init {
            mName = v.findViewById(R.id.text_name)
            mDate = v.findViewById(R.id.text_date)
            mCont = v.findViewById(R.id.text_content)

            mCell = v.findViewById(R.id.cell)
            mCell.setOnClickListener(this)
            mCell.setOnLongClickListener(this)
            listenerRef = WeakReference<CellListOnClickListener>(listener)
            listenerRef2 = WeakReference<CellListOnLongClickListener>(listener2)
        }

        override fun onClick(v: View) {
            Log.v("Click short","ddddddd")
            listenerRef.get()!!.cellClicked(adapterPosition)
        }


        override fun onLongClick(v: View): Boolean {
            Log.v("Click Long","ddddddd")
            listenerRef2.get()!!.cellLongClicked(adapterPosition)

            return false
        }
    }

    fun getCellData(): Array<Data>? {
        return mData
    }

    fun setCellData(data: Array<Data>?){
        mData = data
        notifyDataSetChanged()
    }
}