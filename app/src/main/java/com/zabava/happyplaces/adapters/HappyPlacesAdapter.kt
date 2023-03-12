package com.zabava.happyplaces.adapters


import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zabava.happyplaces.databinding.ItemHappyPlaceBinding
import com.zabava.happyplaces.models.HappyPlaceModel

class HappyPlacesAdapter(private var list: ArrayList<HappyPlaceModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is ViewHolder) {
            holder.ivPlaceName.setImageURI(Uri.parse(model.image))
            holder.tvTitle.text = model.title
            holder.tvDescription.text = model.description
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
