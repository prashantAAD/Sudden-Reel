package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
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
import www.revengerfitness.blogspot.com.sudden_reel.activities.LogInActivity
import www.revengerfitness.blogspot.com.sudden_reel.adapters.ReelsThumbnailAdapter

import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.EditProfileDialogLayoutBinding
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentProfileBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel
import www.revengerfitness.blogspot.com.sudden_reel.model.SavedModel



class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    lateinit var list:ArrayList<ReelsModel>

    lateinit var epdBinding : EditProfileDialogLayoutBinding

    lateinit var finalProfileUri:Uri

    private var finalData: String? =null

    private var previousUrl=""

    lateinit var followersList:ArrayList<FollowModel>

    lateinit var followingList:ArrayList<FollowingModel>

    lateinit var savedList: ArrayList<SavedModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentProfileBinding.bind(view)

        list= ArrayList()

        followersList= ArrayList()
        followingList= ArrayList()
        savedList=ArrayList()


        val dialogView = View.inflate(context, R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(context).setView(dialogView).create()

        val dialogTxt:TextView?=dialogView.findViewById(R.id.dialog_txt)

        dialogTxt?.text="Profile Updating..."


        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        builder.setCancelable(false)




        var adapter= ReelsThumbnailAdapter(list,requireContext())

        var layoutManager=GridLayoutManager(requireContext(),3)

        binding.profileRecyclerView.layoutManager=layoutManager

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                    data?.userId=snapshot.key.toString()

                    Glide.with(requireContext()).load(data?.userProfilePhoto).into(binding.profileProfile)

                    binding.profileProfileName.text=data?.userName

                    binding.profileId.text="RANDOM ID: ${data?.userId}"

                    binding.toolBar.title=data?.userName

                    binding.profileIdCopy.setOnClickListener {

                        setClipboard(requireContext(),data?.userId.toString())

                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })


        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .child("followers").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    followersList.clear()

                    for (dataSnapshot in snapshot.children){

                        var data: FollowModel?=dataSnapshot.getValue(FollowModel::class.java)

                        data?.followedBy=dataSnapshot.key.toString()

                        followersList.add(data!!)


                    }

                    binding.followers.text=followersList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }


            })

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .child("following").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    followingList.clear()

                    for (dataSnapshot in snapshot.children){

                        var data: FollowingModel?=dataSnapshot.getValue(FollowingModel::class.java)

                        data?.followingTo=dataSnapshot.key.toString()

                        followingList.add(data!!)


                    }

                    binding.following.text=followingList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }


            })
        
        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .child("Saved").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    savedList.clear()

                    for (dataSnapshot in snapshot.children){

                        var data: SavedModel?=dataSnapshot.getValue(SavedModel::class.java)

                        data?.savedReel=dataSnapshot.key.toString()

                        savedList.add(data!!)


                    }

                    binding.saved.text=savedList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }


            })



        FirebaseDatabase.getInstance().reference.child("reels").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (dataSnapshot in snapshot.children){

                    var data: ReelsModel?=dataSnapshot.getValue(ReelsModel::class.java)

                    data?.reelsId=dataSnapshot.key.toString()

                    if (data?.reelsBy==FirebaseAuth.getInstance().uid.toString()) {

                        list.add(data)

                    }



                }

                list.reverse()

                adapter.notifyDataSetChanged()


                binding.profileRecyclerView.adapter=adapter



                binding.photos.text=list.size.toString()


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }


        })


        binding.profileOptions.setOnClickListener {

            val dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.profile_bottomsheet_layout)

            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

            dialog.show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val pblSettingsCard: CardView? =dialog.findViewById(R.id.pbl_settings_card)
            val pblPpCard: CardView? =dialog.findViewById(R.id.pbl_pp_card)
            val pblShareCard: CardView? =dialog.findViewById(R.id.pbl_share_card)
            val pblLogoutCard: CardView? =dialog.findViewById(R.id.pbl_logout_card)

            pblSettingsCard?.setOnClickListener {

                dialog.dismiss()

                val dialogEdit = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)

                dialogEdit.requestWindowFeature(Window.FEATURE_NO_TITLE)

                dialogEdit.setContentView(R.layout.edit_profile_dialog_layout)

                dialogEdit.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

                dialogEdit.show()

                dialogEdit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                epdBinding = EditProfileDialogLayoutBinding.inflate(layoutInflater, null, false)

                dialogEdit.setContentView(epdBinding.root)

                FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                    .addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                            data?.userId=snapshot.key.toString()

                            Glide.with(requireContext()).load(data?.userProfilePhoto).into(epdBinding.epdProfile)

                            previousUrl= data?.userProfilePhoto!!

                            epdBinding.epdName.setText(data.userName)


                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                        }


                    })

                epdBinding.epdSave.setOnClickListener {

                    dialogEdit.dismiss()

                    builder.show()

                    if (epdBinding.epdName.text.toString().trim().isEmpty()){

                        builder.dismiss()

                        Toast.makeText(requireContext(), "User name must not be empty", Toast.LENGTH_SHORT).show()

                    }

                    else{

                        if (finalData==null){

                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                            .child("userProfilePhoto").setValue(previousUrl).addOnSuccessListener {

                                builder.dismiss()


                                }

                            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                                .child("userName").setValue(epdBinding.epdName.text.toString()).addOnSuccessListener {

                                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()

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
                                    requireContext(),
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
                        startActivityForResult(intent,100)



                }



            }

            pblPpCard?.setOnClickListener {
                val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://iamtechnosp.blogspot.com/2022/05/random-reels-privacy-policy.html.html"))

                startActivity(openUrlIntent)




            }

            pblShareCard?.setOnClickListener {


                   var rrLink= "https://play.google.com/store/apps/details?id=com.sunayanpradhan.randomreels"

                var rrLogo= "android.resource://com.sunayanpradhan.randomreels/drawable/logo_transparent.png"

                val uri = Uri.parse(rrLogo)

                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.type = "image/*"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Download Now : ${rrLink}")
                shareIntent.type = "text/plain"
                context?.startActivity(shareIntent)


            }

            pblLogoutCard?.setOnClickListener {

                dialog.dismiss()

                val dialogView = View.inflate(context, R.layout.pop_up_dialog_layout, null)

                val builder = AlertDialog.Builder(context).setView(dialogView).create()

                builder.show()


                builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

                var dialogCancel = dialogView.findViewById(R.id.dialog_cancel) as ImageView
                var dialogYes=dialogView.findViewById(R.id.dialog_yes) as Button
                var dialogNo=dialogView.findViewById(R.id.dialog_no) as Button
                var dialogLogo=dialogView.findViewById(R.id.dialog_logo) as ImageView
                var dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

                dialogTxt.text="Logout?"

                FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                    .addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            var data: InformationModel?= snapshot.getValue(InformationModel::class.java)

                            data?.userId=snapshot.key.toString()

                            Glide.with(requireContext()).load(data?.userProfilePhoto).into(dialogLogo)


                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                        }
                    })





                dialogCancel.setOnClickListener {

                    builder.dismiss()

                }

                dialogNo.setOnClickListener {

                    builder.dismiss()

                }

                dialogYes.setOnClickListener {

                    FirebaseAuth.getInstance().signOut()

                    requireContext().startActivity(Intent(requireContext(), LogInActivity::class.java))

                    activity?.finish()

                }



            }


        }


        binding.followerLogo.setOnClickListener {

            val dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.profile_elements_bottomsheet_layout)

            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

            dialog.show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val pebList:ArrayList<InformationModel> = ArrayList()




            val pebTitle: TextView? = dialog.findViewById(R.id.peb_title)
            val pebCount:TextView? = dialog.findViewById(R.id.peb_count)
            val pebSearch:EditText? = dialog.findViewById(R.id.peb_search)
            val pebRecyclerView:RecyclerView? = dialog.findViewById(R.id.peb_recycler)


            val pebAdapter= SearchAdapter(pebList,requireContext())
            val pebLayoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

            pebRecyclerView?.layoutManager=pebLayoutManager
            


            pebTitle?.text="Followers"

            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                .child("followers").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (dataSnapshot in snapshot.children){

                            val pebData: FollowModel?= dataSnapshot.getValue(FollowModel::class.java)

                            pebData?.followedBy=dataSnapshot.key.toString()

                            FirebaseDatabase.getInstance().reference.child("Users").addListenerForSingleValueEvent(object : ValueEventListener{
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

                                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                                }


                            })


                            pebSearch?.addTextChangedListener(object :TextWatcher{
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
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
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

                        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                    }


                })



        }


        binding.followingLogo.setOnClickListener {

            val dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.profile_elements_bottomsheet_layout)

            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

            dialog.show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val pebList:ArrayList<InformationModel> = ArrayList()




            val pebTitle: TextView? = dialog.findViewById(R.id.peb_title)
            val pebCount:TextView? = dialog.findViewById(R.id.peb_count)
            val pebSearch:EditText? = dialog.findViewById(R.id.peb_search)
            val pebRecyclerView:RecyclerView? = dialog.findViewById(R.id.peb_recycler)


            val pebAdapter= SearchAdapter(pebList,requireContext())
            val pebLayoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

            pebRecyclerView?.layoutManager=pebLayoutManager



            pebTitle?.text="Following"

            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                .child("following").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (dataSnapshot in snapshot.children){

                            val pebData: FollowingModel?= dataSnapshot.getValue(FollowingModel::class.java)

                            pebData?.followingTo=dataSnapshot.key.toString()

                            FirebaseDatabase.getInstance().reference.child("Users").addListenerForSingleValueEvent(object : ValueEventListener{
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

                                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                                }


                            })


                            pebSearch?.addTextChangedListener(object :TextWatcher{
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
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
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

                                                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                                            }

                                        })
                                }


                            })


                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                    }


                })





        }


        binding.savedLogo.setOnClickListener {


            val dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setContentView(R.layout.profile_elements_bottomsheet_layout)

            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

            dialog.show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val pebList:ArrayList<ReelsModel> = ArrayList()




            val pebTitle: TextView? = dialog.findViewById(R.id.peb_title)
            val pebCount:TextView? = dialog.findViewById(R.id.peb_count)
            val pebSearch:EditText? = dialog.findViewById(R.id.peb_search)
            val pebRecyclerView:RecyclerView? = dialog.findViewById(R.id.peb_recycler)


            val toolBar: MaterialToolbar?=dialog.findViewById(R.id.tool_bar)

            toolBar?.visibility=View.GONE




            val pebAdapter= ReelsThumbnailAdapter(pebList,requireContext())
            val pebLayoutManager= GridLayoutManager(requireContext(),3)

            pebRecyclerView?.layoutManager=pebLayoutManager



            pebTitle?.text="Saved"

            FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                .child("Saved").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (dataSnapshot in snapshot.children){

                            val pebData: SavedModel?= dataSnapshot.getValue(SavedModel::class.java)

                            pebData?.savedReel=dataSnapshot.key.toString()

                            FirebaseDatabase.getInstance().reference.child("reels").addListenerForSingleValueEvent(object : ValueEventListener{
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

                                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                                }


                            })




                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                    }


                })



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

            Toast.makeText(requireContext(), "COPIED ID", Toast.LENGTH_SHORT).show()

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