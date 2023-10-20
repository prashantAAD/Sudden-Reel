package www.revengerfitness.blogspot.com.sudden_reel.activities

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import www.revengerfitness.blogspot.com.sudden_reel.adapters.SearchAdapter
import www.revengerfitness.blogspot.com.sudden_reel.model.FollowModel
import www.revengerfitness.blogspot.com.sudden_reel.model.FollowingModel
import www.revengerfitness.blogspot.com.sudden_reel.adapters.ReelsThumbnailAdapter

import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.SavedModel
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.ActivityViewProfileBinding
import www.revengerfitness.blogspot.com.sudden_reel.databinding.EditProfileDialogLayoutBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.NotificationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel

import java.util.*
import kotlin.collections.ArrayList

class ViewProfileActivity : AppCompatActivity() {

    lateinit var vpBinding: ActivityViewProfileBinding


    lateinit var follow: FollowModel
    lateinit var following: FollowingModel

    private var isFollow=false


    lateinit var epdBinding : EditProfileDialogLayoutBinding

    private lateinit var finalProfileUri: Uri

    private var finalData: String? =null


    private var previousUrl=""



    lateinit var followersList:ArrayList<FollowModel>

    lateinit var followingList:ArrayList<FollowingModel>

    lateinit var savedList: ArrayList<SavedModel>


    lateinit var list:ArrayList<ReelsModel>


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        vpBinding= DataBindingUtil.setContentView(this,R.layout.activity_view_profile)

        val intent = intent

        var userId=intent.getStringExtra("userId").toString()

        list=ArrayList()

        followersList= ArrayList()
        followingList= ArrayList()
        savedList= ArrayList()


        supportActionBar?.hide()

        this.theme?.applyStyle(R.style.FullScreen,false)
        this.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)



        val dialogView = View.inflate(this, R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(this).setView(dialogView).create()

        val dialogTxt:TextView?=dialogView.findViewById(R.id.dialog_txt)

        dialogTxt?.text="Profile Updating..."


        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        builder.setCancelable(false)


        var profileVideoList: ArrayList<ReelsModel> = ArrayList()

            var adapter= ReelsThumbnailAdapter(profileVideoList,this)

            var layoutManager= GridLayoutManager(this,3)

            vpBinding.profileRecyclerView.layoutManager=layoutManager

            FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                        data?.userId=snapshot.key.toString()

                        Glide.with(this@ViewProfileActivity).load(data?.userProfilePhoto).into(vpBinding.profileProfile)

                        vpBinding.profileProfileName.text=data?.userName

                        vpBinding.profileId.text="RANDOM ID: ${data?.userId}"

                        vpBinding.toolBar.title=data?.userName

                        vpBinding.profileIdCopy.setOnClickListener {


                            setClipboard(this@ViewProfileActivity,data?.userId.toString())

                        }



                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()
                    }

                })



            FirebaseDatabase.getInstance().reference.child("reels").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    profileVideoList.clear()

                    for (dataSnapshot in snapshot.children){

                        var data: ReelsModel?=dataSnapshot.getValue(ReelsModel::class.java)

                        data?.reelsId=dataSnapshot.key.toString()

                        if (data?.reelsBy== userId) {

                            profileVideoList.add(data)

                        }



                    }

                    profileVideoList.reverse()

                    adapter.notifyDataSetChanged()


                    vpBinding.profileRecyclerView.adapter=adapter

                    vpBinding.photos.text=list.size.toString()


                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                }



            })


            if (userId== FirebaseAuth.getInstance().uid){

                vpBinding.vpFollow.visibility= View.GONE

                vpBinding.vpEditProfile.visibility= View.VISIBLE

            }

            else{

                vpBinding.vpFollow.visibility= View.VISIBLE

                vpBinding.vpEditProfile.visibility= View.GONE


            }


            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(userId)
                .child("followers")
                .child(FirebaseAuth.getInstance().uid.toString()).addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        follow = FollowModel(FirebaseAuth.getInstance().uid.toString(), Date().time)

                        if(snapshot.exists() ){
                            vpBinding.vpFollow.setBackgroundDrawable(ContextCompat.getDrawable(this@ViewProfileActivity,R.drawable.following_button))
                            vpBinding.vpFollow.text="Following"
                            isFollow=true

                        }

                        else{

                            vpBinding.vpFollow.setBackgroundDrawable(ContextCompat.getDrawable(this@ViewProfileActivity,R.drawable.follow_button))
                            vpBinding.vpFollow.text="Follow"
                            isFollow=false


                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


            FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(FirebaseAuth.getInstance().uid.toString())
                .child("following")
                .child(userId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        following= FollowingModel(userId, Date().time)

                        if(snapshot.exists() ){

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {




                    }


                })


            FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                .child("followers").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        followersList.clear()

                        for (dataSnapshot in snapshot.children){

                            var data: FollowModel?=dataSnapshot.getValue(FollowModel::class.java)

                            data?.followedBy=dataSnapshot.key.toString()

                            followersList.add(data!!)


                        }

                        vpBinding.followers.text=followersList.size.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()
                    }


                })

            FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                .child("following").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        followingList.clear()

                        for (dataSnapshot in snapshot.children){

                            var data: FollowingModel?=dataSnapshot.getValue(FollowingModel::class.java)

                            data?.followingTo=dataSnapshot.key.toString()

                            followingList.add(data!!)


                        }

                        vpBinding.following.text=followingList.size.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()
                    }


                })



            FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                .child("Saved").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        savedList.clear()

                        for (dataSnapshot in snapshot.children){

                            var data: SavedModel?=dataSnapshot.getValue(SavedModel::class.java)

                            data?.savedReel=dataSnapshot.key.toString()

                            savedList.add(data!!)


                        }

                        vpBinding.saved.text=savedList.size.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()
                    }


                })



            vpBinding.vpFollow.setOnClickListener {

                if (isFollow==true){


                    vpBinding.vpFollow.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.follow_button))
                    vpBinding.vpFollow.text="Follow"
                    vpBinding.vpFollow.setTextColor(Color.WHITE)
                    isFollow=false


                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(userId)
                        .child("followers")
                        .child(FirebaseAuth.getInstance().uid.toString())
                        .removeValue().addOnSuccessListener {



                        }

                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(FirebaseAuth.getInstance().uid.toString())
                        .child("following")
                        .child(userId)
                        .removeValue().addOnSuccessListener {



                        }




                }
                else{


                    vpBinding.vpFollow.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.following_button))
                    vpBinding.vpFollow.setTextColor(this.getColor(R.color.black))
                    vpBinding.vpFollow.text="Following"

                    vpBinding.vpFollow.setTextColor(Color.WHITE)
                    isFollow=true

                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(userId)
                        .child("followers")
                        .child(FirebaseAuth.getInstance().uid.toString())
                        .setValue(follow).addOnSuccessListener {

                            var notification: NotificationModel = NotificationModel()

                            notification.notificationBy= FirebaseAuth.getInstance().uid.toString()
                            notification.notificationAt= Date().time
                            notification.type="follow"


                            var fire= FirebaseDatabase.getInstance().reference
                                .child("notification")
                                .child(userId)
                                .push()

                            var notifyId=fire.key.toString()


                            fire.setValue(notification)

                            fire.child("notificationId")
                                .setValue(notifyId)

                            notification.notificationId=notifyId



                        }



                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(FirebaseAuth.getInstance().uid.toString())
                        .child("following")
                        .child(userId)
                        .setValue(following).addOnSuccessListener {



                        }

                }



            }


            vpBinding.vpEditProfile.setOnClickListener {


                val dialogEdit = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)

                dialogEdit.requestWindowFeature(Window.FEATURE_NO_TITLE)

                dialogEdit.setContentView(R.layout.edit_profile_dialog_layout)

                dialogEdit.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                dialogEdit.show()

                dialogEdit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                epdBinding = EditProfileDialogLayoutBinding.inflate(LayoutInflater.from(this), null, false)

                dialogEdit.setContentView(epdBinding.root)




                FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                            data?.userId=snapshot.key.toString()

                            Glide.with(this@ViewProfileActivity).load(data?.userProfilePhoto).into(epdBinding.epdProfile)

                            previousUrl= data?.userProfilePhoto!!

                            epdBinding.epdName.setText(data.userName)


                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()
                        }


                    })

                epdBinding.epdSave.setOnClickListener {

                    dialogEdit.dismiss()

//                        builder.show()

                    if (epdBinding.epdName.text.toString().trim().isEmpty()){

//                            builder.dismiss()

                        Toast.makeText(this, "User name must not be empty", Toast.LENGTH_SHORT).show()

                    }

                    else{

                        if (finalData==null){

                            FirebaseDatabase.getInstance().reference.child("Users").child(
                                FirebaseAuth.getInstance().uid.toString())
                                .child("userProfilePhoto").setValue(previousUrl).addOnSuccessListener {

//                                        builder.dismiss()


                                }

                            FirebaseDatabase.getInstance().reference.child("Users").child(
                                FirebaseAuth.getInstance().uid.toString())
                                .child("userName").setValue(epdBinding.epdName.text.toString()).addOnSuccessListener {

                                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()

                                }


                        }
                        else {

                            val storageReference: StorageReference =
                                FirebaseStorage.getInstance().reference.child("profile_photos")
                                    .child(
                                        FirebaseAuth.getInstance().uid!!
                                    )

                            storageReference.putFile(finalProfileUri).addOnSuccessListener {


//                                    builder.dismiss()

                                Toast.makeText(
                                    this,
                                    "Profile updated",
                                    Toast.LENGTH_SHORT
                                ).show()

                                storageReference.downloadUrl.addOnSuccessListener { uri ->

                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(FirebaseAuth.getInstance().uid.toString())
                                        .child("userProfilePhoto").setValue(uri.toString())


                                }


                            }
                        }

                    }

                }





                epdBinding.epdProfileAdd.setOnClickListener {


                    var intent= Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "image/*"
                    startActivityForResult(intent, 100)


                }


                epdBinding.epdSave.setOnClickListener {



                    dialogEdit.dismiss()

                    builder.show()

                    if (epdBinding.epdName.text.toString().trim().isEmpty()){

                        builder.dismiss()

                        Toast.makeText(this, "User name must not be empty", Toast.LENGTH_SHORT).show()

                    }

                    else{

                        if (finalData==null){

                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                                .child("userProfilePhoto").setValue(previousUrl).addOnSuccessListener {

                                    builder.dismiss()


                                }

                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                                .child("userName").setValue(epdBinding.epdName.text.toString()).addOnSuccessListener {

                                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()

                                }


                        }



                        else {

                            val storageReference: StorageReference =
                                FirebaseStorage.getInstance().reference.child("profile_photos")
                                    .child(
                                        FirebaseAuth.getInstance().uid!!
                                    )

                            storageReference.putFile(finalProfileUri).addOnSuccessListener {


                                builder.dismiss()

                                Toast.makeText(
                                    this,
                                    "Profile updated",
                                    Toast.LENGTH_SHORT
                                ).show()

                                storageReference.downloadUrl.addOnSuccessListener { uri ->

                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(FirebaseAuth.getInstance().uid.toString())
                                        .child("userProfilePhoto").setValue(uri.toString())


                                }


                            }
                        }

                    }

                }





            }


            vpBinding.followerLogo.setOnClickListener {

                val dialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

                dialog.setContentView(R.layout.profile_elements_bottomsheet_layout)

                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                dialog.show()

                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val pebList: ArrayList<InformationModel> = ArrayList()




                val pebTitle: TextView? = dialog.findViewById(R.id.peb_title)
                val pebCount: TextView? = dialog.findViewById(R.id.peb_count)
                val pebSearch: EditText? = dialog.findViewById(R.id.peb_search)
                val pebRecyclerView: RecyclerView? = dialog.findViewById(R.id.peb_recycler)


                val pebAdapter= SearchAdapter(pebList,this)
                val pebLayoutManager= LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL,false)

                pebRecyclerView?.layoutManager=pebLayoutManager



                pebTitle?.text="Followers"

                FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                    .child("followers").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (dataSnapshot in snapshot.children){

                                val pebData: FollowModel?= dataSnapshot.getValue(FollowModel::class.java)

                                pebData?.followedBy=dataSnapshot.key.toString()

                                FirebaseDatabase.getInstance().reference.child("Users").addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        pebList.clear()

                                        for (dataSnapshot1 in snapshot.children){

                                            val pebFollowData: InformationModel?= dataSnapshot1.getValue(
                                                InformationModel::class.java)

                                            pebFollowData?.userId=dataSnapshot1.key.toString()

                                            if (pebFollowData?.userId==pebData?.followedBy){

                                                pebList.add(pebFollowData!!)

                                            }

                                            pebAdapter.notifyDataSetChanged()

                                            pebRecyclerView?.adapter=pebAdapter

                                            pebCount?.text=pebList.size.toString()


                                        }


                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                        Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                                    }


                                })


                                pebSearch?.addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(
                                        p0: CharSequence?,
                                        p1: Int,
                                        p2: Int,
                                        p3: Int
                                    ) {

                                    }

                                    override fun onTextChanged(
                                        p0: CharSequence?,
                                        p1: Int,
                                        p2: Int,
                                        p3: Int
                                    ) {

                                    }

                                    override fun afterTextChanged(p0: Editable?) {
                                        FirebaseDatabase.getInstance().reference.child("Users").orderByChild("userName").startAt(pebSearch.text.toString()).endAt(pebSearch.text.toString()+"\uf8ff")
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {

                                                    pebList.clear()

                                                    for (dataSnapshot2 in snapshot.children){

                                                        var data2=dataSnapshot2.getValue(
                                                            InformationModel::class.java)
                                                        data2?.userId=dataSnapshot2.key.toString()

                                                        if (data2?.userId==pebData?.followedBy) {

                                                            pebList.add(data2!!)

                                                        }

                                                    }

                                                    pebAdapter.notifyDataSetChanged()

                                                    pebRecyclerView?.adapter=pebAdapter

                                                }

                                                override fun onCancelled(error: DatabaseError) {

                                                }

                                            })
                                    }


                                })


                            }


                        }

                        override fun onCancelled(error: DatabaseError) {

                            Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                        }


                    })





            }

            vpBinding.followingLogo.setOnClickListener {

                val dialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

                dialog.setContentView(R.layout.profile_elements_bottomsheet_layout)

                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                dialog.show()

                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val pebList: ArrayList<InformationModel> = ArrayList()




                val pebTitle: TextView? = dialog.findViewById(R.id.peb_title)
                val pebCount: TextView? = dialog.findViewById(R.id.peb_count)
                val pebSearch: EditText? = dialog.findViewById(R.id.peb_search)
                val pebRecyclerView: RecyclerView? = dialog.findViewById(R.id.peb_recycler)


                val pebAdapter= SearchAdapter(pebList,this)
                val pebLayoutManager= LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL,false)

                pebRecyclerView?.layoutManager=pebLayoutManager



                pebTitle?.text="Following"

                FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                    .child("following").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (dataSnapshot in snapshot.children){

                                val pebData: FollowingModel?= dataSnapshot.getValue(FollowingModel::class.java)

                                pebData?.followingTo=dataSnapshot.key.toString()

                                FirebaseDatabase.getInstance().reference.child("Users").addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        pebList.clear()

                                        for (dataSnapshot1 in snapshot.children){

                                            val pebFollowData: InformationModel?= dataSnapshot1.getValue(
                                                InformationModel::class.java)

                                            pebFollowData?.userId=dataSnapshot1.key.toString()

                                            if (pebFollowData?.userId==pebData?.followingTo){

                                                pebList.add(pebFollowData!!)

                                            }

                                            pebAdapter.notifyDataSetChanged()

                                            pebRecyclerView?.adapter=pebAdapter

                                            pebCount?.text=pebList.size.toString()


                                        }


                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                        Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                                    }


                                })


                                pebSearch?.addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(
                                        p0: CharSequence?,
                                        p1: Int,
                                        p2: Int,
                                        p3: Int
                                    ) {

                                    }

                                    override fun onTextChanged(
                                        p0: CharSequence?,
                                        p1: Int,
                                        p2: Int,
                                        p3: Int
                                    ) {

                                    }

                                    override fun afterTextChanged(p0: Editable?) {
                                        FirebaseDatabase.getInstance().reference.child("Users").orderByChild("userName").startAt(pebSearch.text.toString()).endAt(pebSearch.text.toString()+"\uf8ff")
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {

                                                    pebList.clear()

                                                    for (dataSnapshot2 in snapshot.children){

                                                        var data2=dataSnapshot2.getValue(
                                                            InformationModel::class.java)
                                                        data2?.userId=dataSnapshot2.key.toString()

                                                        if (data2?.userId==pebData?.followingTo) {

                                                            pebList.add(data2!!)

                                                        }

                                                    }

                                                    pebAdapter.notifyDataSetChanged()

                                                    pebRecyclerView?.adapter=pebAdapter

                                                }

                                                override fun onCancelled(error: DatabaseError) {

                                                    Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                                                }

                                            })
                                    }


                                })


                            }


                        }

                        override fun onCancelled(error: DatabaseError) {

                            Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                        }


                    })




            }

            vpBinding.savedLogo.setOnClickListener {

                val dialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

                dialog.setContentView(R.layout.profile_elements_bottomsheet_layout)

                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                dialog.show()

                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val pebList: ArrayList<ReelsModel> = ArrayList()




                val pebTitle: TextView? = dialog.findViewById(R.id.peb_title)
                val pebCount: TextView? = dialog.findViewById(R.id.peb_count)
                val pebSearch: EditText? = dialog.findViewById(R.id.peb_search)
                val pebRecyclerView: RecyclerView? = dialog.findViewById(R.id.peb_recycler)


                val toolBar: MaterialToolbar?=dialog.findViewById(R.id.tool_bar)

                toolBar?.visibility= View.GONE




                val pebAdapter= ReelsThumbnailAdapter(pebList,this)
                val pebLayoutManager= GridLayoutManager(this,3)

                pebRecyclerView?.layoutManager=pebLayoutManager



                pebTitle?.text="Saved"

                FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                    .child("Saved").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (dataSnapshot in snapshot.children){

                                val pebData: SavedModel?= dataSnapshot.getValue(SavedModel::class.java)

                                pebData?.savedReel=dataSnapshot.key.toString()

                                FirebaseDatabase.getInstance().reference.child("reels").addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        pebList.clear()

                                        for (dataSnapshot1 in snapshot.children){

                                            val pebSavedData: ReelsModel?= dataSnapshot1.getValue(
                                                ReelsModel::class.java)

                                            pebSavedData?.reelsId=dataSnapshot1.key.toString()


                                            if (pebSavedData?.reelsId==pebData?.savedReel){

                                                pebList.add(pebSavedData!!)

                                            }

                                            pebAdapter.notifyDataSetChanged()

                                            pebRecyclerView?.adapter=pebAdapter

                                            pebCount?.text=pebList.size.toString()


                                        }


                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                        Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                                    }


                                })




                            }


                        }

                        override fun onCancelled(error: DatabaseError) {

                            Toast.makeText(this@ViewProfileActivity, error.message, Toast.LENGTH_SHORT).show()

                        }


                    })



        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)





        if(requestCode==100) {


            if (data?.data != null) {

                finalData=data.data.toString()

                val uri: Uri = data.data!!

                epdBinding.epdProfile.setImageURI(uri)

                finalProfileUri=uri




            }

            else{

                finalData=null

            }

        }






    }




    private fun setClipboard(context: Context, text: String) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("COPIED ID", text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "COPIED ID", Toast.LENGTH_SHORT).show()

        }
    }

}