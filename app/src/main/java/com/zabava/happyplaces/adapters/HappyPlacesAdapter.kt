package com.zabava.happyplaces.adapters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zabava.happyplaces.activities.AddHappyPlaceActivity
import com.zabava.happyplaces.activities.MainActivity
import com.zabava.happyplaces.databinding.ItemHappyPlaceBinding
import com.zabava.happyplaces.models.HappyPlaceModel


class HappyPlacesAdapter(private var list: ArrayList<HappyPlaceModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class ViewHolder(binding: ItemHappyPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivPlaceName = binding.ivPlaceImage
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemHappyPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val happyPlaceModel = list[position]

        if (holder is ViewHolder) {
            holder.ivPlaceName.setImageURI(Uri.parse(happyPlaceModel.image))
            holder.tvTitle.text = happyPlaceModel.title
            holder.tvDescription.text = happyPlaceModel.description

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, happyPlaceModel)
                }
            }
        }
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(activity.applicationContext, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }
}
