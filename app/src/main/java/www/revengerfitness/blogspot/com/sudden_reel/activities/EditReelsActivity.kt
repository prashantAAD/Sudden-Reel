package www.revengerfitness.blogspot.com.sudden_reel.activities

import android.app.AlertDialog
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel


import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.ActivityEditReelsBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditReelsActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditReelsBinding

    lateinit var videoUrl:String

    lateinit var videoTitle:String


    private var videoLength=true

    private var titleLength=0

    lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_reels)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_edit_reels)

        videoUrl=""
        videoTitle=""

        supportActionBar?.hide()


        this.theme?.applyStyle(R.style.FullScreen,false)
        this.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)



        val dialogView = View.inflate(this, R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(this).setView(dialogView).create()

        val dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

        dialogTxt.text="Reels Updating..."


        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        builder.setCancelable(false)

        val reelsId = intent.getStringExtra("reelsId").toString()






        mp= MediaPlayer()



        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                    Glide.with(this@EditReelsActivity).load(data?.userProfilePhoto).into(binding.editProfile)

                    binding.editProfileName.text=data?.userName

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditReelsActivity, error.message, Toast.LENGTH_SHORT).show()
                }

            })



        FirebaseDatabase.getInstance().reference.child("reels").child(reelsId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val data: ReelsModel?=snapshot.getValue(ReelsModel::class.java)

                data?.reelsId=snapshot.key.toString()

                videoUrl= data?.reelsVideo.toString()

                videoTitle= data?.reelsTitle.toString()

                titleLength=videoTitle.length


                binding.editEtCount.text = "${titleLength}/100"

                binding.editTitle.setText(videoTitle)


                binding.editVideo.setVideoPath(videoUrl)

                binding.editVideo.setOnPreparedListener{

                    it.start()

                    mp= it


                    binding.editVideoLength.text=getIntervalTime(it.duration.toLong())

                    binding.progressBar.visibility=View.GONE


                    videoLength = it.duration <= 60000



                }

                binding.editVideo.setOnCompletionListener {

                    it.start()

                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditReelsActivity, error.message, Toast.LENGTH_SHORT).show()
            }


        })







        val mTextEditorWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                binding.editEtCount.text = "${s.length}/100"

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                binding.editEtCount.text = "${s.length}/100"

                titleLength=s.length

            }

            override fun afterTextChanged(s: Editable) {

                binding.editEtCount.text = "${s.length}/100"

            }
        }

        binding.editTitle.addTextChangedListener(mTextEditorWatcher)






        binding.editUpload.setOnClickListener {

            val title:String=binding.editTitle.text.toString()


            val post: ReelsModel = ReelsModel()


            if (!videoLength){

                Toast.makeText(this, "Video length is more than 60 seconds", Toast.LENGTH_SHORT).show()

            }

            else if (titleLength>100){

                Toast.makeText(this, "Title is too long", Toast.LENGTH_SHORT).show()

            }

            else{

                builder.show()

                post.reelsVideo=videoUrl

                post.reelsBy= FirebaseAuth.getInstance().uid.toString()

                post.reelsAt= Date().time

                if (title.isEmpty()){

                    post.reelsTitle= SimpleDateFormat.getInstance().format(Date()).toString()

                }
                else{

                    post.reelsTitle=title

                }

                val fire= FirebaseDatabase.getInstance().reference.child("reels").child(reelsId)

                fire.child("reelsTitle").setValue(post.reelsTitle).addOnSuccessListener {

                    builder.dismiss()

                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show()



                    this.finish()

                }





            }


        }









    }


    private fun getIntervalTime(longInterval: Long): String {
        var intMillis = longInterval

        val mm: Long = TimeUnit.MILLISECONDS.toMinutes(intMillis)
        intMillis -= TimeUnit.MINUTES.toMillis(mm)
        val ss: Long = TimeUnit.MILLISECONDS.toSeconds(intMillis)
        intMillis -= TimeUnit.SECONDS.toMillis(ss)
        val stringInterval = "%02d:%02d"
        return String.format(stringInterval, mm, ss)
    }






}