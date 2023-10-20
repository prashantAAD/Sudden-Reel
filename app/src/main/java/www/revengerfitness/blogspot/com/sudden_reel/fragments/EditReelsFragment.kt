package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentEditReelsBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class EditReelsFragment : Fragment() {

    lateinit var binding: FragmentEditReelsBinding





    private var videoUrl:String= null.toString()
    private var videoTitle:String= null.toString()



    private var videoLength=true

    private var titleLength=0

    lateinit var mp: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_reels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding= FragmentEditReelsBinding.bind(view)

        val dialogView = View.inflate(context, R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(context).setView(dialogView).create()

        val dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

        dialogTxt.text="Reels Updating..."


        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        builder.setCancelable(false)

        val reelsId = requireArguments().getString("reelsId").toString()







        mp= MediaPlayer()



        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                    Glide.with(requireContext()).load(data?.userProfilePhoto).into(binding.editProfile)

                    binding.editProfileName.text=data?.userName

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })



        FirebaseDatabase.getInstance().reference.child("reels").child(reelsId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var data: ReelsModel?=snapshot.getValue(ReelsModel::class.java)

                data?.reelsId=snapshot.key.toString()

                videoUrl= data?.reelsVideo!!

                videoTitle= data?.reelsTitle!!




            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }


        })


        titleLength=videoTitle.length


        binding.editEtCount.text = "${titleLength}/100"

        binding.editTitle.setText(videoTitle)


        binding.editVideo.setVideoPath(videoUrl)



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

            var title:String=binding.editTitle.text.toString()


            val post: ReelsModel = ReelsModel()


            if (videoLength==false){

                Toast.makeText(requireContext(), "Video length is more than 60 seconds", Toast.LENGTH_SHORT).show()

            }

            else if (titleLength>100){

                Toast.makeText(requireContext(), "Title is too long", Toast.LENGTH_SHORT).show()

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

                var fire= FirebaseDatabase.getInstance().reference.child("reels").child(reelsId)

                fire.child("reelsTitle").setValue(post.reelsTitle).addOnSuccessListener {

                    builder.dismiss()

                    Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()

                }

                fire.setValue(post).addOnSuccessListener {



                }




            }


        }



        binding.editVideo.setOnPreparedListener{

            it.start()

            mp= it


            binding.editVideoLength.text=getIntervalTime(it.duration.toLong())


            if (it.duration>60000){

                videoLength=false

            }

            else{

                videoLength=true

            }



        }

        binding.editVideo.setOnCompletionListener {

            it.start()

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