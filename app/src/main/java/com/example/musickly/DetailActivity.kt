package com.example.musickly

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import java.util.Timer
import android.renderscript.Element
import kotlin.coroutines.CoroutineContext

class DetailActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private var totalTime: Int = 0
    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateSeekBarRunnable: Runnable
    private lateinit var animator: ObjectAnimator

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        val backgroundView = findViewById<LinearLayout>(R.id.backgroundview)

        // Retrieve intent extras
        val img = intent.getStringExtra("img")
        val songName = intent.getStringExtra("songName")
        val artist = intent.getStringExtra("artist")
        val album = intent.getStringExtra("album")
        val song = intent.getStringExtra("song")

        // Find Views
        val songNameView = findViewById<TextView>(R.id.songname)
        val artistView = findViewById<TextView>(R.id.songwriter)
        val image = findViewById<ImageView>(R.id.imageView)
        val playButton = findViewById<ImageView>(R.id.play)
        val stopButton = findViewById<ImageView>(R.id.stop)
        val loopButton = findViewById<ImageView>(R.id.loop)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        // Set data to views
        songNameView.text = songName
        artistView.text = "$artist / $album"
        Picasso.get()
            .load(img)
            .placeholder(R.drawable.img)
            .error(R.drawable.img)
            .into(image)

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, song!!.toUri())
        totalTime = mediaPlayer.duration
        seekBar.max = totalTime

        // Set up SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.baseline_pause_24)
        // Set up RenderScript
        val rs = RenderScript.create(this)

        // Load the background image as a Bitmap
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_2)

        // Apply blur effect on the Bitmap
        val blurredBitmap = blurBitmap(rs, originalBitmap)

        // Set the blurred bitmap as the background of the LinearLayout
        backgroundView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        backgroundView.background = BitmapDrawable(resources, blurredBitmap)

        // to rotate the frame

        val ringLayout = findViewById<FrameLayout>(R.id.ringLayout)

        val rotation = ObjectAnimator.ofFloat(ringLayout, "rotation", 0f, 360f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        rotation.start()


        // Image rotation animation
        animator = ObjectAnimator.ofFloat(image, "rotation", 0f, 360f).apply {
            duration = 3000 // Duration for one full rotation
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
        }
        animator.start()

        // Play button functionality
        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                animator.pause() // Pause rotation
                stopUpdatingSeekBar()
            } else {
                mediaPlayer.start()
                playButton.setImageResource(R.drawable.baseline_pause_24)
                animator.start() // Start rotation
                startUpdatingSeekBar()
            }
        }

        // Stop button functionality
        stopButton.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, song.toUri())
            mediaPlayer.prepare()
            seekBar.progress = 0
            playButton.setImageResource(R.drawable.baseline_play_arrow_24)
            animator.pause() // Pause rotation
            stopUpdatingSeekBar()
        }

        // Loop button functionality with color change
        loopButton.setOnClickListener {
            mediaPlayer.isLooping = !mediaPlayer.isLooping
            if (mediaPlayer.isLooping) {
                loopButton.setColorFilter(resources.getColor(R.color.loop_active, theme)) // Active color
            } else {
                loopButton.setColorFilter(resources.getColor(R.color.loop_inactive, theme)) // Inactive color
            }
        }

        // Initialize the Runnable for SeekBar updates
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    seekBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    private fun startUpdatingSeekBar() {
        handler.post(updateSeekBarRunnable)
    }

    private fun stopUpdatingSeekBar() {
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer.isPlaying) {
            startUpdatingSeekBar()
        }
    }

    override fun onPause() {
        super.onPause()
        stopUpdatingSeekBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        stopUpdatingSeekBar()
        timer.cancel()
    }

    private fun blurBitmap(rs: RenderScript, original: Bitmap): Bitmap {
        val outputBitmap = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
        val allocationIn = Allocation.createFromBitmap(rs, original)
        val allocationOut = Allocation.createFromBitmap(rs, outputBitmap)
        val blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blur.setRadius(25f) // Blur intensity
        blur.setInput(allocationIn)
        blur.forEach(allocationOut)
        allocationOut.copyTo(outputBitmap)
        return outputBitmap
    }




}


