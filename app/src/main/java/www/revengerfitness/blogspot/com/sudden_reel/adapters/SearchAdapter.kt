package www.revengerfitness.blogspot.com.sudden_reel.adapters


import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.model.FollowModel
import www.revengerfitness.blogspot.com.sudden_reel.model.FollowingModel
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.NotificationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel
import www.revengerfitness.blogspot.com.sudden_reel.model.SavedModel

import www.revengerfitness.blogspot.com.sudden_reel.activities.ViewProfileActivity

import www.revengerfitness.blogspot.com.sudden_reel.R
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(val SearchItems:List<InformationModel>, val context: Context): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {


    lateinit var progressDialog: ProgressDialog

    lateinit var follow: FollowModel
    lateinit var following: FollowingModel

    private var isFollow=false

    lateinit var followersList:ArrayList<FollowModel>

    lateinit var followingList:ArrayList<FollowingModel>

    lateinit var savedList: ArrayList<SavedModel>

    lateinit var reelsList: ArrayList<ReelsModel>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.search_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem=SearchItems[position]

        follow= FollowModel()
        following= FollowingModel()

        followersList= ArrayList()
        followingList= ArrayList()
        savedList=ArrayList()
        reelsList= ArrayList()



        Glide.with(context).load(currentItem.userProfilePhoto).into(holder.searchProfile)

        holder.searchName.text=currentItem.userName

        if (currentItem.userId==FirebaseAuth.getInstance().uid){

            holder.searchFollow.visibility=View.GONE

        }


        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentItem.userId)
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
            .child(currentItem.userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    following= FollowingModel(currentItem.userId,Date().time)

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
                            .child(currentItem.userId)
                            .child("followers")
                            .child(FirebaseAuth.getInstance().uid.toString())
                            .removeValue().addOnSuccessListener {



                    }

                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .child("following")
                    .child(currentItem.userId)
                    .removeValue().addOnSuccessListener {



                    }




            }
            else{


                holder.searchFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_button))
                holder.searchFollow.text="Following"
                isFollow=true

                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(currentItem.userId)
                    .child("followers")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    .setValue(follow).addOnSuccessListener {

                        var notification: NotificationModel = NotificationModel()

                        notification.notificationBy= FirebaseAuth.getInstance().uid.toString()
                        notification.notificationAt= Date().time
                        notification.type="follow"


                        var fire=FirebaseDatabase.getInstance().reference
                            .child("notification")
                            .child(currentItem.userId)
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
                    .child(currentItem.userId)
                    .setValue(following).addOnSuccessListener {



                    }

            }

        }

        holder.itemView.setOnClickListener {

            val intent=Intent(context, ViewProfileActivity::class.java)

            intent.putExtra("userId",currentItem.userId)

            context.startActivity(intent)

            (context as Activity).overridePendingTransition(
                R.anim.slide_in_right,0)

        }





    }


    override fun getItemCount(): Int {
        return SearchItems.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val searchProfile: ImageView =itemView.findViewById(R.id.search_profile)
        val searchName: TextView =itemView.findViewById(R.id.search_name)
        val searchFollow:Button=itemView.findViewById(R.id.searchFollow)







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