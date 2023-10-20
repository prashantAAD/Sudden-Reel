package www.revengerfitness.blogspot.com.sudden_reel.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.model.CommentModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel
import www.revengerfitness.blogspot.com.sudden_reel.activities.ReelsViewActivity


import www.revengerfitness.blogspot.com.sudden_reel.R


class ReelsThumbnailAdapter(var list: List<ReelsModel>, var context: Context) : RecyclerView.Adapter<ReelsThumbnailAdapter.ViewHolder>(){

    lateinit var likeList:ArrayList<String>

    lateinit var commentListAdt:ArrayList<CommentModel>

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        val rtImage:ImageView=itemView.findViewById(R.id.rt_image)

        val rtLike:TextView=itemView.findViewById(R.id.rt_like)

        val rtComment:TextView=itemView.findViewById(R.id.rt_comment)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=LayoutInflater.from(context).inflate(R.layout.reels_thumbnail_layout,parent,false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem=list[position]

        likeList=ArrayList()
        commentListAdt= ArrayList()


        Glide.with(context).load(currentItem.reelsVideo).into(holder.rtImage)


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

                    holder.rtLike.text = likeList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {


                }
            })


        FirebaseDatabase.getInstance().reference.child("reels")
            .child(currentItem.reelsId).child("comments")
            .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                commentListAdt.clear()

                for (dataSnapshot in snapshot.children){

                    val data: CommentModel?= dataSnapshot.getValue(CommentModel::class.java)

                    data?.commentId=dataSnapshot.key.toString()

                    commentListAdt.add(data!!)


                }

                holder.rtComment.text=commentListAdt.size.toString()


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

            }


        })


        holder.itemView.setOnClickListener {



            val intent= Intent(context, ReelsViewActivity::class.java)

            intent.putExtra("reelsId",currentItem.reelsId)

            context.startActivity(intent)

            (context as Activity).overridePendingTransition(
                R.anim.slide_in_right,0)



        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}