 package com.example.musickly

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

 class MainActivity : AppCompatActivity() {

     lateinit var myRecyclerView : RecyclerView
     lateinit var myAdapter : MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val name = intent.getStringExtra(SignInPage.KEY2) ?: ""
        val firstName = name.split(" ")[0] // Get the first part (first name)
        val welcomesTex = findViewById<TextView>(R.id.tvWelcome)
        welcomesTex.text = "Welcome $firstName ,"



        myRecyclerView = findViewById(R.id.recyclerView)
        val retrofitBuilder  = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getDate("eminem")
        retrofitData.enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>, response: Response<MyData>) {
                //if API call is success then this method is executed
                val dataList = response.body()?.data!!
//                val textView = findViewById<TextView>(R.id.helloText)
//                textView.text = dataList.toString()
                myAdapter = MyAdapter(this@MainActivity,dataList)
                myRecyclerView.adapter = myAdapter
                myRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                myAdapter.setItemClickListener(object : MyAdapter.onItemClickListener{
                    override fun onItemClick(position: Int) {
                        Log.d("TAG: onItemClick","onItemClick: $position")
                        val intent = Intent(this@MainActivity,DetailActivity::class.java)
                        intent.putExtra("img",dataList[position].album.cover)
                        intent.putExtra("songName",dataList[position].title)
                        intent.putExtra("artist",dataList[position].artist.name)
                        intent.putExtra("album",dataList[position].album.title)
                        intent.putExtra("rank",dataList[position].rank.toString())
                        intent.putExtra("song",dataList[position].preview)
                        startActivity(intent)
                    }
                })
//                Log.d("TAG: onResponse","onResponse: "+response.body())
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                //if API call is failure then this method is executed
                Log.d("TAG: onFailure","onFailure: "+t.message)
            }


        })
    }
     @SuppressLint("MissingSuperCall")
     override fun onBackPressed() {
         val builder1 = AlertDialog.Builder(this)
         builder1.setTitle("Are you sure")
         builder1.setMessage("Do you want to exit")
         builder1.setIcon(R.drawable.baseline_exit_to_app_24)
         builder1.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
             finishAffinity() // Closes all activities in the task and exits the app
         })
         builder1.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->  })
         builder1.show()
     }
}