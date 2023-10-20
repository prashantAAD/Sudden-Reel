package www.revengerfitness.blogspot.com.sudden_reel.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import www.revengerfitness.blogspot.com.sudden_reel.model.FollowModel
import www.revengerfitness.blogspot.com.sudden_reel.model.FollowingModel
import www.revengerfitness.blogspot.com.sudden_reel.activities.EditReelsActivity
import www.revengerfitness.blogspot.com.sudden_reel.activities.ViewProfileActivity
import www.revengerfitness.blogspot.com.sudden_reel.model.CommentModel
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.SavedModel
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.EditProfileDialogLayoutBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.NotificationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel
import java.io.FileNotFoundException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ReelsAdapter(var list: List<ReelsModel>, var context: Context):RecyclerView.Adapter<ReelsAdapter.ViewHolder>() {

    private lateinit var mp:MediaPlayer

    private var islike=false

    lateinit var likeList:ArrayList<String>

    lateinit var commentListAdt:ArrayList<CommentModel>

    lateinit var shareList:ArrayList<String>

    lateinit var commentList: ArrayList<CommentModel>


    private var savedActive:Boolean=false

    lateinit var saved: SavedModel


    lateinit var follow: FollowModel
    lateinit var following: FollowingModel

    private var isFollow=false


    lateinit var epdBinding : EditProfileDialogLayoutBinding

    lateinit var finalProfileUri:Uri

    private var finalData: String? =null


    private var previousUrl=""

    private val appLink=""


    private lateinit var followersList:ArrayList<FollowModel>

    private lateinit var followingList:ArrayList<FollowingModel>

    private lateinit var savedList: ArrayList<SavedModel>



    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        var reelsVideoLayout:ConstraintLayout=itemView.findViewById(R.id.reels_video_layout)

        var reelsVideo:VideoView=itemView.findViewById(R.id.reels_video)
        var reelsProfile:ImageView=itemView.findViewById(R.id.reels_profile)
        var reelsProfileName:TextView=itemView.findViewById(R.id.reels_profile_name)
        var reelsTitle:TextView=itemView.findViewById(R.id.reels_title)
        var reelsDate:TextView=itemView.findViewById(R.id.reels_date)
        var reelsLike:TextView=itemView.findViewById(R.id.reels_like)
        var reelsComment:TextView=itemView.findViewById(R.id.reels_comment)
        var reelsShare:TextView=itemView.findViewById(R.id.reels_share)
        var reelsOptions:ImageView=itemView.findViewById(R.id.reels_options)
        var progressBar:ProgressBar=itemView.findViewById(R.id.progress_bar)
        var reelsVideoPlay:ImageView=itemView.findViewById(R.id.reels_video_play)
        var reelsVideoLike:ImageView=itemView.findViewById(R.id.reels_video_like)
        var searchFollow:Button=itemView.findViewById(R.id.searchFollow)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reels_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = list[position]

        holder.reelsVideoPlay.visibility=View.GONE

        likeList = ArrayList()

        commentListAdt = ArrayList()

        shareList = ArrayList()




        followersList= ArrayList()
        followingList= ArrayList()
        savedList=ArrayList()

        mp = MediaPlayer()

        holder.reelsVideo.setVideoPath(currentItem.reelsVideo)

        holder.reelsVideo.setOnPreparedListener {

            mp = it

            it.start()
            holder.progressBar.visibility = View.GONE

        }

        holder.reelsVideo.setOnCompletionListener {

            mp = it

            it.start()

            holder.progressBar.visibility = View.GONE


        }

        holder.reelsVideoLayout.setOnClickListener {


            if (mp.isPlaying) {

                mp.pause()

                holder.reelsVideoPlay.visibility=View.VISIBLE


            } else {

                mp.start()

                holder.reelsVideoPlay.visibility=View.GONE

            }


        }



        FirebaseDatabase.getInstance().reference
            .child("reels")
            .child(currentItem.reelsId)
            .child("likes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    likeList.clear()

                    for (dataSnapshot in snapshot.children) {

                        val data: String = dataSnapshot.value.toString()


                        likeList.add(data)

                    }

                    holder.reelsLike.text = likeList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {


                }

            })




        FirebaseDatabase.getInstance().reference
            .child("reels")
            .child(currentItem.reelsId)
            .child("likes")
            .child(FirebaseAuth.getInstance().uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {

                        holder.reelsLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_round_thumb_up_24_blue,
                            0,
                            0
                        )

                        islike = true


                    } else {

                        holder.reelsLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_round_thumb_up_24,
                            0,
                            0
                        )

                        islike = false

                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        FirebaseDatabase.getInstance().reference.child("Users").child(currentItem.reelsBy)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: InformationModel? = snapshot.getValue(InformationModel::class.java)

                    Glide.with(context).load(user?.userProfilePhoto).into(holder.reelsProfile)

                    holder.reelsProfileName.text = user?.userName

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }


            })

        holder.reelsTitle.text = currentItem.reelsTitle


        val time: String = SimpleDateFormat.getInstance().format(currentItem.reelsAt).toString()

        holder.reelsDate.text = time





        holder.reelsLike.setOnClickListener {



            if (!islike) {

                holder.reelsLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    R.drawable.ic_round_thumb_up_24_blue,
                    0,
                    0
                )

                holder.reelsVideoLike.visibility=View.VISIBLE

                val popup:Animation = AnimationUtils.loadAnimation(context, R.anim.pop_up)

                holder.reelsVideoLike.startAnimation(popup)



                val timer = object: CountDownTimer(2000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        
                    }

                    override fun onFinish() {

                        holder.reelsVideoLike.visibility=View.GONE
                        
                    }
                }
                timer.start()






                FirebaseDatabase.getInstance().reference
                    .child("reels")
                    .child(currentItem.reelsId)
                    .child("likes")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .setValue(true).addOnSuccessListener {

                        islike = true




                        FirebaseDatabase.getInstance().reference
                            .child("reels")
                            .child(currentItem.reelsId)
                            .child("likes")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    likeList.clear()

                                    for (dataSnapshot in snapshot.children) {

                                        val data: String = dataSnapshot.value.toString()


                                        likeList.add(data)

                                    }

                                    holder.reelsLike.text = likeList.size.toString()

                                }

                                override fun onCancelled(error: DatabaseError) {


                                }

                            })


                        val notification: NotificationModel = NotificationModel()

                        notification.notificationBy = FirebaseAuth.getInstance().uid.toString()
                        notification.notificationAt = Date().time
                        notification.reelsId = currentItem.reelsId
                        notification.reelsBy = currentItem.reelsBy
                        notification.type = "like"

                        val fire = FirebaseDatabase.getInstance().reference
                            .child("notification")
                            .child(currentItem.reelsBy)
                            .push()

                        val notifyId = fire.key.toString()


                        fire.setValue(notification)

                        fire.child("notificationId")
                            .setValue(notifyId)

                        notification.notificationId = notifyId


                    }


            } else {

                holder.reelsLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    R.drawable.ic_round_thumb_up_24,
                    0,
                    0
                )


                FirebaseDatabase.getInstance().reference
                    .child("reels")
                    .child(currentItem.reelsId)
                    .child("likes")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .setValue(false).addOnSuccessListener {


                        islike = false

                        FirebaseDatabase.getInstance().reference
                            .child("reels")
                            .child(currentItem.reelsId)
                            .child("likes")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    likeList.clear()

                                    for (dataSnapshot in snapshot.children) {

                                        val data: String = dataSnapshot.value.toString()


                                        likeList.add(data)

                                    }

                                    holder.reelsLike.text = likeList.size.toString()

                                }

                                override fun onCancelled(error: DatabaseError) {


                                }

                            })


                    }
                FirebaseDatabase.getInstance().reference
                    .child("reels")
                    .child(currentItem.reelsId)
                    .child("likes")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .removeValue()


            }


        }

        FirebaseDatabase.getInstance().reference.child("reels").child(currentItem.reelsId)
            .child("comments").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                commentListAdt.clear()

                for (dataSnapshot in snapshot.children) {

                    val data: CommentModel? = dataSnapshot.getValue(CommentModel::class.java)

                    data?.commentId = dataSnapshot.key.toString()

                    commentListAdt.add(data!!)


                }

                holder.reelsComment.text = commentListAdt.size.toString()


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

            }


        })


        FirebaseDatabase.getInstance().reference.child("reels").child(currentItem.reelsId)
            .child("comments").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                commentListAdt.clear()

                for (dataSnapshot in snapshot.children) {

                    val data: CommentModel? = dataSnapshot.getValue(CommentModel::class.java)

                    data?.commentId = dataSnapshot.key.toString()

                    if (data?.commentBy == FirebaseAuth.getInstance().uid.toString()) {


                        holder.reelsComment.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_round_chat_bubble_24_blue,
                            0,
                            0
                        )


                    } else {


                        holder.reelsComment.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_round_chat_bubble_24,
                            0,
                            0
                        )

                    }


                }


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

            }


        })






        holder.reelsComment.setOnClickListener {


            val dialog = BottomSheetDialog(context,R.style.BottomSheetDialogTheme)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.comment_bottomsheet_layout)

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            dialog.show()


            val commentProfile: ImageView? = dialog.findViewById(R.id.comment_profile)
            val commentProfileName: TextView? = dialog.findViewById(R.id.comment_profile_name)
            val reelsDate: TextView? = dialog.findViewById(R.id.reels_date)
            val reelsTitle: TextView? = dialog.findViewById(R.id.reels_title)
            val commentRecyclerView: RecyclerView? = dialog.findViewById(R.id.comment_recyclerView)
            val commentOwnProfile: ImageView? = dialog.findViewById(R.id.comment_own_profile)
            val commentContent: EditText? = dialog.findViewById(R.id.comment_content)
            val commentPost: TextView? = dialog.findViewById(R.id.comment_post)



            commentList = ArrayList()


            showKeyboard(commentContent)

            val adapter = CommentAdapter(commentList, context)

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)

            commentRecyclerView?.layoutManager = layoutManager

            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val data: InformationModel? =
                            snapshot.getValue(InformationModel::class.java)

                        data?.userId = snapshot.key.toString()

                        Glide.with(context).load(data?.userProfilePhoto).into(commentOwnProfile!!)


                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

                    }


                })

            commentContent?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {


                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val title: String = commentContent.text.toString()

                    if (title.isNotEmpty()) {

                        commentPost?.visibility = View.VISIBLE

                    } else {

                        commentPost?.visibility = View.GONE

                    }

                }

                override fun afterTextChanged(s: Editable?) {

                }

            })





            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val data: InformationModel? =
                            snapshot.getValue(InformationModel::class.java)

                        data?.userId = snapshot.key.toString()

                        Glide.with(context).load(data?.userProfilePhoto).into(commentOwnProfile!!)


                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

                    }


                })



            FirebaseDatabase.getInstance().reference.child("reels").child(currentItem.reelsId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val post: ReelsModel? = snapshot.getValue(ReelsModel::class.java)

                        post?.reelsId = snapshot.key.toString()

                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(post?.reelsBy.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val data: InformationModel? =
                                        snapshot.getValue(InformationModel::class.java)

                                    data?.userId = snapshot.key.toString()

                                    Glide.with(context).load(data?.userProfilePhoto)
                                        .into(commentProfile!!)

                                    commentProfileName?.text = data?.userName


                                }

                                override fun onCancelled(error: DatabaseError) {

                                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT)
                                        .show()

                                }


                            })

                        reelsTitle?.text = post?.reelsTitle

                        reelsDate?.text =
                            SimpleDateFormat.getInstance().format(post?.reelsAt).toString()


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    }


                })


            FirebaseDatabase.getInstance().reference.child("reels").child(currentItem.reelsId)
                .child("comments").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    commentList.clear()

                    for (dataSnapshot in snapshot.children) {

                        val data: CommentModel? = dataSnapshot.getValue(CommentModel::class.java)

                        data?.commentId = dataSnapshot.key.toString()

                        commentList.add(data!!)

                    }

                    adapter.notifyDataSetChanged()

                    commentRecyclerView?.adapter = adapter


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            })


            commentPost?.setOnClickListener {

                val comment = CommentModel()

                comment.commentBody = commentContent?.text.toString()
                comment.commentAt = Date().time
                comment.commentBy = FirebaseAuth.getInstance().uid.toString()

                val fire = FirebaseDatabase.getInstance().reference
                    .child("reels")
                    .child(currentItem.reelsId)
                    .child("comments")
                    .push()

                val commId = fire.key.toString()




                comment.commentId = commId

                comment.commentPostId = currentItem.reelsId


                fire.setValue(comment).addOnSuccessListener {


                    FirebaseDatabase.getInstance().reference.child("reels")
                        .child(currentItem.reelsId).child("comments")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                commentList.clear()

                                for (dataSnapshot in snapshot.children) {

                                    val data: CommentModel? =
                                        dataSnapshot.getValue(CommentModel::class.java)

                                    data?.commentId = dataSnapshot.key.toString()

                                    commentList.add(data!!)

                                }

                                adapter.notifyDataSetChanged()

                                commentRecyclerView?.adapter = adapter


                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                            }

                        })

                    val notification: NotificationModel = NotificationModel()

                    notification.notificationBy = FirebaseAuth.getInstance().uid.toString()
                    notification.notificationAt = Date().time
                    notification.reelsId = currentItem.reelsId
                    notification.reelsBy = currentItem.reelsBy
                    notification.type = "comment"

                    var fire = FirebaseDatabase.getInstance().reference
                        .child("notification")
                        .child(currentItem.reelsBy)
                        .push()

                    val notifyId = fire.key.toString()


                    fire.setValue(notification)

                    fire.child("notificationId")
                        .setValue(notifyId)

                    notification.notificationId = notifyId

                    commentContent?.text?.clear()

                    hideKeyboard(commentContent)


                }



                FirebaseDatabase.getInstance().reference.child("reels").child(currentItem.reelsId)
                    .child("comments").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        commentListAdt.clear()

                        for (dataSnapshot in snapshot.children) {

                            val data: CommentModel? =
                                dataSnapshot.getValue(CommentModel::class.java)

                            data?.commentId = dataSnapshot.key.toString()

                            commentListAdt.add(data!!)


                        }

                        holder.reelsComment.text = commentListAdt.size.toString()


                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

                    }


                })

                FirebaseDatabase.getInstance().reference.child("reels").child(currentItem.reelsId)
                    .child("comments").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        commentListAdt.clear()

                        for (dataSnapshot in snapshot.children) {

                            val data: CommentModel? =
                                dataSnapshot.getValue(CommentModel::class.java)

                            data?.commentId = dataSnapshot.key.toString()

                            if (data?.commentBy == FirebaseAuth.getInstance().uid.toString()) {


                                holder.reelsComment.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    0,
                                    R.drawable.ic_round_chat_bubble_24_blue,
                                    0,
                                    0
                                )


                            } else {


                                holder.reelsComment.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    0,
                                    R.drawable.ic_round_chat_bubble_24,
                                    0,
                                    0
                                )

                            }


                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

                    }


                })

            }


        }

        FirebaseDatabase.getInstance().reference.child("reels")
            .child(currentItem.reelsId)
            .child("shares")
            .child(FirebaseAuth.getInstance().uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {

                        holder.reelsShare.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_round_share_24_blue,
                            0,
                            0
                        )

                    } else {

                        holder.reelsShare.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_round_share_24,
                            0,
                            0
                        )


                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            })

        FirebaseDatabase.getInstance().reference.child("reels")
            .child(currentItem.reelsId)
            .child("shares")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    shareList.clear()

                    for (dataSnapshot in snapshot.children) {

                        val data: String = dataSnapshot.value.toString()

                        shareList.add(data)

                    }

                    holder.reelsShare.text = shareList.size.toString()


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            })



        holder.reelsShare.setOnClickListener {

            holder.reelsShare.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.drawable.ic_round_share_24_blue,
                0,
                0
            )

            downloadForLessThan11ForSharing(position)


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//                // download for android 11
//
////                    downloadAndroid11ForSharing(position,documentFile)
//
//
//
//            }else{
//                // download for android less than 11
//
//                downloadForLessThan11ForSharing(position)
//
//
//            }



            FirebaseDatabase.getInstance().reference
                .child("reels")
                .child(currentItem.reelsId)
                .child("shares")
                .child(FirebaseAuth.getInstance().uid.toString())
                .setValue(true).addOnSuccessListener {

                    FirebaseDatabase.getInstance().reference.child("reels")
                        .child(currentItem.reelsId)
                        .child("shares")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                shareList.clear()

                                for (dataSnapshot in snapshot.children) {

                                    val data: String = dataSnapshot.value.toString()

                                    shareList.add(data!!)

                                }

                                holder.reelsShare.text = shareList.size.toString()


                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                            }

                        })


                }


        }



        holder.reelsProfile.setOnClickListener {

            val intent=Intent(context, ViewProfileActivity::class.java)

            intent.putExtra("userId",currentItem.reelsBy)

            context.startActivity(intent)

            (context as Activity).overridePendingTransition(
                R.anim.slide_in_right,0)


        }

        holder.reelsProfileName.setOnClickListener {

            val intent=Intent(context, ViewProfileActivity::class.java)

            intent.putExtra("userId",currentItem.reelsBy)

            context.startActivity(intent)


            (context as Activity).overridePendingTransition(
                R.anim.slide_in_right,0)


        }

        holder.reelsOptions.setOnClickListener {

            val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.options_bottomsheet_layout)

            val oblDownloadCard: CardView? = dialog.findViewById(R.id.obl_download_card)
            val oblBookMarkCard: CardView? = dialog.findViewById(R.id.obl_bookmark_card)
            val oblEditCard: CardView? = dialog.findViewById(R.id.obl_edit_card)
            val oblDeleteCard: CardView? = dialog.findViewById(R.id.obl_delete_card)

            val oblBookMark:ImageView?=dialog.findViewById(R.id.obl_bookmark)

            dialog.show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (currentItem.reelsBy == FirebaseAuth.getInstance().uid.toString()) {

                oblEditCard?.visibility = View.VISIBLE
                oblDeleteCard?.visibility = View.VISIBLE

            } else {

                oblEditCard?.visibility = View.GONE
                oblDeleteCard?.visibility = View.GONE

            }

            oblDownloadCard?.setOnClickListener {



                Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();

                downloadForLessThan11(position)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // download for android 11

//                    downloadAndroid11(position,documentFile);



                }else{
                    // download for android less than 11

                    downloadForLessThan11(position)


                }




                dialog.dismiss()

            }


            oblBookMarkCard?.setOnClickListener {

                dialog.dismiss()



            }


            FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(FirebaseAuth.getInstance().uid.toString())
            .child("Saved")
            .child(currentItem.reelsId).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    saved= SavedModel(currentItem.reelsId,Date().time)

                    savedActive = if (snapshot.exists()){

                        oblBookMark?.setImageResource(R.drawable.ic_round_bookmark_added_24)

                        true

                    } else{

                        oblBookMark?.setImageResource(R.drawable.ic_round_bookmark_24)

                        false

                    }



                }

                override fun onCancelled(error: DatabaseError) {



                }

            })


        oblBookMarkCard?.setOnClickListener {

            dialog.dismiss()

            if (savedActive){

                oblBookMark?.setImageResource(R.drawable.ic_round_bookmark_24)



                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .child("Saved")
                    .child(currentItem.reelsId).removeValue().addOnSuccessListener {

                        Toast.makeText(context, "Removed bookmark", Toast.LENGTH_SHORT).show()

                    }

                savedActive=false





            }

            else{

                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .child("Saved")
                    .child(currentItem.reelsId).setValue(saved).addOnSuccessListener {

                        Toast.makeText(context, "Added bookmark", Toast.LENGTH_SHORT).show()

                    }

                oblBookMark?.setImageResource(R.drawable.ic_round_bookmark_added_24)

                savedActive=true

            }




        }







            oblEditCard?.setOnClickListener {

                dialog.dismiss()

                val intent=Intent(context, EditReelsActivity::class.java)

                intent.putExtra("reelsId",currentItem.reelsId)

                context.startActivity(intent)


            }

            oblDeleteCard?.setOnClickListener {

                dialog.dismiss()


                val dialogView = View.inflate(context, R.layout.pop_up_dialog_layout, null)

                val builder = AlertDialog.Builder(context).setView(dialogView).create()

                builder.show()

                builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

                val dialogCancel = dialogView.findViewById(R.id.dialog_cancel) as ImageView
                var dialogYes=dialogView.findViewById(R.id.dialog_yes) as Button
                var dialogNo=dialogView.findViewById(R.id.dialog_no) as Button
                var dialogLogo=dialogView.findViewById(R.id.dialog_logo) as ImageView
                var dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

                dialogTxt.text="Delete this reel?"


                FirebaseDatabase.getInstance().reference
                    .child("reels")
                    .child(currentItem.reelsId)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val post: ReelsModel?=snapshot.getValue(ReelsModel::class.java)

                            Glide.with(context)
                                .load(post?.reelsVideo)
                                .into(dialogLogo)


                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })


                dialogCancel.setOnClickListener {

                    builder.dismiss()

                }

                dialogNo.setOnClickListener {

                    builder.dismiss()

                }

                dialogYes.setOnClickListener {

                    builder.dismiss()

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                for (dataSnapshot in dataSnapshot.children) {


                                    var user: InformationModel? =
                                        dataSnapshot.getValue(InformationModel::class.java)
                                    user?.userId = dataSnapshot.key.toString()

                                    if (user != null) {
                                        FirebaseDatabase.getInstance().reference
                                            .child("Users")
                                            .child(user.userId)
                                            .child("Saved")
                                            .child(currentItem.reelsId).removeValue()
                                    }


                                }

                            }

                            override fun onCancelled(error: DatabaseError) {


                            }
                        })



                    FirebaseDatabase.getInstance().reference.child("notification")
                        .child(currentItem.reelsBy)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                for (dataSnapshot in dataSnapshot.children) {


                                    var notification: NotificationModel? =
                                        dataSnapshot.getValue(NotificationModel::class.java)

                                    notification?.notificationId = dataSnapshot.key.toString()

                                    if (notification?.reelsId == currentItem.reelsId) {

                                        FirebaseDatabase.getInstance().reference.child("notification")
                                            .child(currentItem.reelsBy)
                                            .child(notification.notificationId)
                                            .removeValue()

                                    }

                                }

                            }

                            override fun onCancelled(error: DatabaseError) {


                            }
                        })



                    if (currentItem.reelsId != null) {

                        val reference =
                            FirebaseDatabase.getInstance().reference.child("reels").child(currentItem.reelsId)


                        try {
                            reference.removeValue()
                                .addOnSuccessListener {

                                    Toast.makeText(context, "Reel deleted", Toast.LENGTH_SHORT).show()


                                }


                        } catch (e: java.lang.Exception) {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()

                        }


                    }

                    val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child(currentItem.reelsStorageId)

                    try {

                        storageReference.delete()


                    }
                    catch (e:Exception){

                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

                    }





                }



            }


        }


        if (currentItem.reelsBy==FirebaseAuth.getInstance().uid){

            holder.searchFollow.visibility=View.GONE

        }


        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentItem.reelsBy)
            .child("followers")
            .child(FirebaseAuth.getInstance().uid.toString()).addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    follow = FollowModel(FirebaseAuth.getInstance().uid.toString(),Date().time)

                    if(snapshot.exists() ){
                        holder.searchFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_button))
                        holder.searchFollow.text="Following"
                        isFollow=true

                    }

                    else{


                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(FirebaseAuth.getInstance().uid.toString())
            .child("following")
            .child(currentItem.reelsBy).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    following= FollowingModel(currentItem.reelsBy,Date().time)

                    if(snapshot.exists() ){

                    }
                }

                override fun onCancelled(error: DatabaseError) {




                }


            })


        holder.searchFollow.setOnClickListener {

            if (isFollow==true){


                holder.searchFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_button))
                holder.searchFollow.text="Follow"
                isFollow=false


                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(currentItem.reelsBy)
                    .child("followers")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .removeValue().addOnSuccessListener {



                    }

                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .child("following")
                    .child(currentItem.reelsBy)
                    .removeValue().addOnSuccessListener {



                    }




            }
            else{


                holder.searchFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_button))
                holder.searchFollow.text="Following"
                isFollow=true

                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(currentItem.reelsBy)
                    .child("followers")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .setValue(follow).addOnSuccessListener {

                        val notification: NotificationModel = NotificationModel()

                        notification.notificationBy= FirebaseAuth.getInstance().uid.toString()
                        notification.notificationAt= Date().time
                        notification.type="follow"


                        val fire=FirebaseDatabase.getInstance().reference
                            .child("notification")
                            .child(currentItem.reelsBy)
                            .push()

                        val notifyId=fire.key.toString()


                        fire.setValue(notification)

                        fire.child("notificationId")
                            .setValue(notifyId)

                        notification.notificationId=notifyId



                    }



                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .child("following")
                    .child(currentItem.reelsBy)
                    .setValue(following).addOnSuccessListener {



                    }

            }

        }




        }

    override fun getItemCount(): Int {
            return list.size
        }



    private fun showKeyboard(comment: EditText?) {

            val manager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            manager.showSoftInput(comment?.rootView, InputMethodManager.SHOW_IMPLICIT)

            comment?.requestFocus()


        }

    private fun hideKeyboard(comment: EditText?) {

            val manager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            manager.hideSoftInputFromWindow(comment?.applicationWindowToken, 0)


        }


    private fun downloadForLessThan11(position: Int) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(list[position].reelsVideo)
        val request = DownloadManager.Request(uri)
        request.setTitle(list[position].reelsTitle + ".mp4")
        request.setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS,
            list[position].reelsTitle + ".mp4"
        )
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val reference = downloadManager.enqueue(request)
        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //Fetching the download id received with the broadcast
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                    try {
                        downloadManager.openDownloadedFile(id)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    private fun downloadForLessThan11ForSharing(position: Int) {
        val dialogView = View.inflate(context, R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(context).setView(dialogView).create()



        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        builder.setCancelable(false)

        builder.show()

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(list[position].reelsVideo)
        val request = DownloadManager.Request(uri)
        request.setTitle(list[position].reelsTitle + ".mp4")
        request.setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS,
            list[position].reelsTitle + ".mp4"
        )
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val reference = downloadManager.enqueue(request)
        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //Fetching the download id received with the broadcast
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                    builder.dismiss()
                    val vidUri = downloadManager.getUriForDownloadedFile(id)
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "video/mp4"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, vidUri)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check This Video on $appLink")
                    context.startActivity(shareIntent)
                }
            }
        }
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }




    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun downloadAndroid11(position: Int, fileDoc: DocumentFile) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(list[position].reelsVideo)
        val request = DownloadManager.Request(uri)
        request.setTitle(list[position].reelsTitle + ".mp4")
        request.setDestinationInExternalFilesDir(
            context,
            fileDoc.uri.path,
            list[position].reelsTitle + ".mp4"
        )
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val reference = downloadManager.enqueue(request)
        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //Fetching the download id received with the broadcast
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                    try {
                        downloadManager.openDownloadedFile(id)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun downloadAndroid11ForSharing(position: Int, fileDoc: DocumentFile) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Downloading Before Sharing..")
        progressDialog.setMessage("Please Wait..")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(list[position].reelsVideo)
        val request = DownloadManager.Request(uri)
        request.setTitle("Downloading : " + list[position].reelsTitle + ".mp4")
        request.setDestinationInExternalFilesDir(
            context,
            fileDoc.uri.path,
            list[position].reelsTitle + ".mp4"
        )
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val reference = downloadManager.enqueue(request)
        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //Fetching the download id received with the broadcast
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    val vidUri = downloadManager.getUriForDownloadedFile(id)
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "video/mp4"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, vidUri)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check This Video on : $appLink")
                    context.startActivity(shareIntent)
                }
            }
        }
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )


    }



    private fun setClipboard(context: Context, text: String) {

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("COPIED ID", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "COPIED ID", Toast.LENGTH_SHORT).show()

    }



}