package www.revengerfitness.blogspot.com.sudden_reel.adapters

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.activities.ViewProfileActivity
import www.revengerfitness.blogspot.com.sudden_reel.model.CommentModel
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel
import www.revengerfitness.blogspot.com.sudden_reel.R

class CommentAdapter(private var CommentItem:List<CommentModel>, var context: Context):RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    lateinit var postUser:String




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.comment_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem=CommentItem[position]
        val timeAgo=TimeAgo.using(currentItem.commentAt)
        holder.time.text=timeAgo

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentItem.commentBy).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: InformationModel?=snapshot.getValue(InformationModel::class.java)

                         Glide.with(context)
                        .load(user?.userProfilePhoto)
                        .into(holder.commentProfile)


                    holder.comment.text= Html.fromHtml("<b>"+user?.userName+"</b>" +"    " + currentItem.commentBody)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        FirebaseDatabase.getInstance().reference
            .child("reels")
            .child(currentItem.commentPostId)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val data: ReelsModel?=snapshot.getValue(ReelsModel::class.java)

                    data?.reelsId=snapshot.key.toString()

                    postUser=data?.reelsBy.toString()




                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }


            })





        val dialogView = View.inflate(context, R.layout.pop_up_dialog_layout, null)

        val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()




        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val dialogCancel = dialogView.findViewById(R.id.dialog_cancel) as ImageView
        val dialogYes=dialogView.findViewById(R.id.dialog_yes) as Button
        val dialogNo=dialogView.findViewById(R.id.dialog_no) as Button
        val dialogLogo=dialogView.findViewById(R.id.dialog_logo) as ImageView
        val dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

        dialogTxt.text="Delete this comment?"

        dialogLogo.visibility= View.GONE


        holder.itemView.setOnLongClickListener{

            Toast.makeText(context, postUser, Toast.LENGTH_SHORT).show()

            if (currentItem.commentBy==FirebaseAuth.getInstance().uid){



                builder.show()

            }


            return@setOnLongClickListener true


        }

        dialogYes.setOnClickListener {

            builder.dismiss()

            FirebaseDatabase.getInstance().reference
                .child("reels")
                .child(currentItem.commentPostId)
                .child("comments")
                .child(currentItem.commentId).removeValue().addOnSuccessListener {

                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()


                }


        }

        dialogNo.setOnClickListener {

            builder.dismiss()

        }

        dialogCancel.setOnClickListener {

            builder.dismiss()

        }


        holder.commentProfile.setOnClickListener {
            val intent=Intent(context, ViewProfileActivity::class.java)
            intent.putExtra("userId",currentItem.commentBy)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)

        }

        




    }

    override fun getItemCount(): Int {
        return CommentItem.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val comment:TextView=itemView.findViewById(R.id.comment)
        val time:TextView=itemView.findViewById(R.id.time)
        val commentProfile:ImageView=itemView.findViewById(R.id.comment_profile)



    }

}