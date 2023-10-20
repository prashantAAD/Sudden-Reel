package www.revengerfitness.blogspot.com.sudden_reel.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.activities.ReelsViewActivity
import www.revengerfitness.blogspot.com.sudden_reel.activities.ViewProfileActivity
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.NotificationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel
import www.revengerfitness.blogspot.com.sudden_reel.R

class NotificationAdapter(private var NotificationItems:ArrayList<NotificationModel>, var context: Context):RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.notification_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem=NotificationItems[position]

        val timeAgo= TimeAgo.using(currentItem.notificationAt)
        holder.notificationTime.text=timeAgo

        val type:String=currentItem.type

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentItem.notificationBy)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: InformationModel?= snapshot.getValue(InformationModel::class.java)

                            Glide.with(context)
                                .load(user?.userProfilePhoto)
                                .into(holder.notificationProfile)





                    if (type == "like"){


                        holder.notificationText.text= Html.fromHtml("<b>"+user?.userName+"</b>"+" liked your post")

                        holder.notificationImgCard.visibility=View.VISIBLE

                        FirebaseDatabase.getInstance().reference
                            .child("reels")
                            .child(currentItem.reelsId)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val post: ReelsModel?=snapshot.getValue(ReelsModel::class.java)

                                    Glide.with(context)
                                        .load(post?.reelsVideo)
                                        .into(holder.notificationImg)


                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                    }
                    else if (type=="comment"){
                        holder.notificationText.text=Html.fromHtml("<b>"+user?.userName+"</b>"+" commented on your post ")

                        holder.notificationImgCard.visibility=View.VISIBLE

                        FirebaseDatabase.getInstance().reference
                            .child("reels")
                            .child(currentItem.reelsId)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val post: ReelsModel?=snapshot.getValue(ReelsModel::class.java)

                                    Glide.with(context)
                                        .load(post?.reelsVideo)
                                        .into(holder.notificationImg)


                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                    }

                    else{
                        holder.notificationText.text=Html.fromHtml("<b>"+user?.userName+"</b>"+" started following you")

                        holder.notificationImgCard.visibility=View.GONE

                    }


                }

                override fun onCancelled(error: DatabaseError) {


                }


            })



        holder.itemView.setOnLongClickListener {

            val dialogView = View.inflate(context, R.layout.pop_up_dialog_layout, null)

            val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()

            builder.show()

            builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val dialogCancel = dialogView.findViewById(R.id.dialog_cancel) as ImageView
            val dialogYes=dialogView.findViewById(R.id.dialog_yes) as Button
            val dialogNo=dialogView.findViewById(R.id.dialog_no) as Button
            val dialogLogo=dialogView.findViewById(R.id.dialog_logo) as ImageView
            val dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

            dialogTxt.text="Delete this notification?"

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



                FirebaseDatabase.getInstance().reference.child("notification")
                    .child(FirebaseAuth.getInstance().uid.toString()).child(currentItem.notificationId).removeValue()


                builder.dismiss()


            }









            return@setOnLongClickListener true
        }







        holder.openNotification.setOnClickListener {



            when (type) {

                "like" -> {

                    FirebaseDatabase.getInstance().reference
                        .child("notification")
                        .child(currentItem.reelsBy)
                        .child(currentItem.notificationId)
                        .child("checkOpen")
                        .setValue(true)

                    val intent = Intent(context, ReelsViewActivity::class.java)
                    intent.putExtra("reelsId", currentItem.reelsId)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)

                    (context as Activity).overridePendingTransition(
                        R.anim.slide_in_right,0)

                }

                "comment" -> {
                    FirebaseDatabase.getInstance().reference
                        .child("notification")
                        .child(currentItem.reelsBy)
                        .child(currentItem.notificationId)
                        .child("checkOpen")
                        .setValue(true)

                    val intent = Intent(
                        context, ReelsViewActivity::class.java)
                    intent.putExtra("reelsId", currentItem.reelsId)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)

                    (context as Activity).overridePendingTransition(
                        R.anim.slide_in_right,0)

                }

                "follow" -> {

                    FirebaseDatabase.getInstance().reference
                        .child("notification")
                        .child(currentItem.reelsBy)
                        .child(currentItem.notificationId)
                        .child("checkOpen")
                        .setValue(true)

                    val intent=Intent(context, ViewProfileActivity::class.java)

                    intent.putExtra("userId",currentItem.notificationBy)

                    context.startActivity(intent)

                    (context as Activity).overridePendingTransition(
                        R.anim.slide_in_right,0)


                }

            }

        }

        val checkOpen:Boolean=currentItem.checkOpen

        if (checkOpen){

            holder.openNotification.setBackgroundColor(Color.BLACK)
        }
        else{
            holder.openNotification.setBackgroundColor(Color.parseColor("#101010"))
        }



    }

    override fun getItemCount(): Int {
        return NotificationItems.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val openNotification:ConstraintLayout=itemView.findViewById(R.id.open_notification)
        val notificationProfile:ImageView=itemView.findViewById(R.id.nt_profile)
        val notificationText:TextView=itemView.findViewById(R.id.nt_text)
        val notificationTime:TextView=itemView.findViewById(R.id.nt_time)
        val notificationImg:ImageView=itemView.findViewById(R.id.nt_image)
        val notificationImgCard:CardView=itemView.findViewById(R.id.nt_image_card)




    }

}