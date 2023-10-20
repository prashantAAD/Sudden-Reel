package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentAddBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AddFragment : Fragment() {

    lateinit var binding: FragmentAddBinding

    private val VIDEO_REQUEST_CODE=100

    private lateinit var uri:Uri

    private var videoUri=false

    private var videoLength=true

    private var titleLength=0

    private lateinit var mp:MediaPlayer




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentAddBinding.bind(view)

        val dialogView = View.inflate(context, R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(context).setView(dialogView).create()

        val dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

        dialogTxt.text="Reel Uploading..."


        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        builder.setCancelable(false)


        binding.addEtCount.text = "${titleLength}/100"


        mp= MediaPlayer()



        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                    Glide.with(requireContext()).load(data?.userProfilePhoto).into(binding.addProfile)

                    binding.addProfileName.text=data?.userName

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })


        val mTextEditorWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                binding.addEtCount.text = "${s.length}/100"

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                binding.addEtCount.text = "${s.length}/100"

                titleLength=s.length

            }

            override fun afterTextChanged(s: Editable) {

                binding.addEtCount.text = "${s.length}/100"

            }
        }

        binding.addTitle.addTextChangedListener(mTextEditorWatcher)





        binding.addUpload.setOnClickListener {

            val title:String=binding.addTitle.text.toString()

            val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("posts")
                .child(FirebaseAuth.getInstance().uid.toString())
                .child(Date().time.toString())

            val post: ReelsModel = ReelsModel()

            if (!videoUri){

                Toast.makeText(requireContext(), "Please select video", Toast.LENGTH_SHORT).show()

            }

            else if (!videoLength){

                Toast.makeText(requireContext(), "Video length is more than 60 seconds", Toast.LENGTH_SHORT).show()

            }

            else if (titleLength>100){

                Toast.makeText(requireContext(), "Title is too long", Toast.LENGTH_SHORT).show()

            }

            else{

                builder.show()

                storageReference.putFile(uri).addOnSuccessListener{

                    storageReference.downloadUrl.addOnSuccessListener { uri->

                        post.reelsVideo=uri.toString()

                        post.reelsStorageId=uri.lastPathSegment.toString()

                        post.reelsBy=FirebaseAuth.getInstance().uid.toString()

                        post.reelsAt=Date().time

                        if (title.isEmpty()){

                            post.reelsTitle=SimpleDateFormat.getInstance().format(Date()).toString()



                        }
                        else{

                            post.reelsTitle=title

                        }

                        val fire=FirebaseDatabase.getInstance().reference.child("reels").push()

                        post.reelsId=fire.key.toString()

//                        FirebaseDatabase.getInstance().reference.child(FirebaseAuth.getInstance().uid.toString()).child("followers")
//                            .addListenerForSingleValueEvent(object :ValueEventListener{
//                                override fun onDataChange(snapshot: DataSnapshot) {
//
//                                    Toast.makeText(requireContext(), snapshot.key.toString(), Toast.LENGTH_SHORT).show()
//
//
//                                    for (dataSnapshot in snapshot.children){
//
//                                        val data:FollowModel?=dataSnapshot.getValue(FollowModel::class.java)
//
//                                        data?.followedBy=dataSnapshot?.key.toString()
//
//                                        Toast.makeText(requireContext(), dataSnapshot?.key.toString(), Toast.LENGTH_SHORT).show()
//
//                                        var notification: NotificationModel = NotificationModel()
//
//                                        notification.notificationBy = FirebaseAuth.getInstance().uid.toString()
//                                        notification.notificationAt = Date().time
//                                        notification.reelsId = post.reelsId
//                                        notification.reelsBy = post.reelsBy
//                                        notification.type = "post"
//
//                                        var fire = FirebaseDatabase.getInstance().reference
//                                            .child("notification")
//                                            .child(data?.followedBy.toString())
//                                            .push()
//
//                                        var notifyId = fire.key.toString()
//
//
//                                        fire.setValue(notification)
//
//                                        fire.child("notificationId")
//                                            .setValue(notifyId)
//
//                                        notification.notificationId = notifyId
//
//
//                                    }
//
//
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
//                                }
//
//
//                            })



                        fire.setValue(post).addOnSuccessListener {

                            builder.dismiss()

                            Toast.makeText(requireContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show()




                       }







                    }


                }




            }


        }



        binding.addBtn.setOnClickListener {

                    val intent = Intent()
                    intent.type = "video/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(intent,VIDEO_REQUEST_CODE)

        }

        binding.addVideo.setOnPreparedListener{

            it.start()

            mp= it


            binding.addVideoLength.text=getIntervalTime(it.duration.toLong())


            videoLength = it.duration <= 60000



        }

        binding.addVideo.setOnCompletionListener {

            it.start()

        }



    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==VIDEO_REQUEST_CODE){

            if (data?.data != null) {
                uri = data.data!!

                videoUri=true

                binding.addVideo.setVideoURI(uri)

            } else {

                Toast.makeText(requireContext(), "Video not selected", Toast.LENGTH_SHORT).show()

            }


        }

        else{



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

    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context

    }

    override fun onDetach() {
        super.onDetach()

        mContext = null

    }


}