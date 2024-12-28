package com.example.musickly
import android.app.Activity
import android.media.MediaPlayer
import android.provider.MediaStore.Audio.Media
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyAdapter(val context: Activity, val dataList: List<Data>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private lateinit var myListsner : onItemClickListener
    interface onItemClickListener
    {
        fun onItemClick(position: Int)
    }
    fun setItemClickListener(listener: onItemClickListener)
    {
        myListsner = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.each_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentData = dataList[position]
        val mediaPlayer = MediaPlayer.create(context, currentData.preview.toUri())

        // Set index
        holder.index.text = (position + 1).toString()+"."

        // Populate data into the view
        holder.title.text = currentData.title
        holder.writer.text = currentData.artist.name
        holder.duration.text = formatDuration(currentData.duration)
        Picasso.get().load(currentData.album.cover).into(holder.image)

//        // Handle play/pause actions
//        holder.play.setOnClickListener {
//            mediaPlayer.start()
//        }
//        holder.pause.setOnClickListener {
//            mediaPlayer.stop()
//        }
        holder.itemView.setOnClickListener {
            myListsner.onItemClick(position) // Trigger the listener
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.musicImage)
        val title: TextView = itemView.findViewById(R.id.musicTitle)
        val index: TextView = itemView.findViewById(R.id.itemIndex)
        val writer: TextView = itemView.findViewById(R.id.musicWriter)
        val duration: TextView = itemView.findViewById(R.id.musicTime)
    }
    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }


}
