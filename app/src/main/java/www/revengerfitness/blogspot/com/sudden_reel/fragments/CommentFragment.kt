package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.adapters.CommentAdapter
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentCommentBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.CommentModel
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentFragment : Fragment() {

    lateinit var binding: FragmentCommentBinding

    lateinit var commentList: ArrayList<CommentModel>

    private var reelsId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentCommentBinding.bind(view)


        commentList= ArrayList()

        reelsId = arguments?.getString("reelsId").toString()

        showKeyboard(binding.commentContent)

        val adapter= CommentAdapter(commentList,requireContext())

        val layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        binding.commentRecyclerView.layoutManager=layoutManager

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                    data?.userId=snapshot.key.toString()

                    Glide.with(requireContext()).load(data?.userProfilePhoto).into(binding.commentOwnProfile)



                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                }


            })

        binding.commentContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {



            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var title:String=binding.commentContent.text.toString()

                if(title.isNotEmpty()){

                    binding.commentPost.visibility=View.VISIBLE

                }


                else{

                    binding.commentPost.visibility=View.GONE

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })





        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                    data?.userId=snapshot.key.toString()

                    Glide.with(requireContext()).load(data?.userProfilePhoto).into(binding.commentOwnProfile)


                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                }


            })



        FirebaseDatabase.getInstance().reference.child("reels").child(reelsId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var post: ReelsModel?= snapshot.getValue(ReelsModel::class.java)

                post?.reelsId=snapshot.key.toString()

                FirebaseDatabase.getInstance().reference.child("Users").child(post?.reelsBy.toString())
                    .addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            var data: InformationModel?=snapshot.getValue(InformationModel::class.java)

                            data?.userId=snapshot.key.toString()

                            Glide.with(requireContext()).load(data?.userProfilePhoto).into(binding.commentProfile)

                            binding.commentProfileName.text= data?.userName


                        }

                        override fun onCancelled(error: DatabaseError) {

                            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                        }


                    })

                binding.reelsTitle.text=post?.reelsTitle

                binding.reelsDate.text=SimpleDateFormat.getInstance().format(post?.reelsAt).toString()



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }


        })


        FirebaseDatabase.getInstance().reference.child("reels").child(reelsId).child("comments").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                commentList.clear()

                for (dataSnapshot in snapshot.children){

                    var data: CommentModel?= dataSnapshot.getValue(CommentModel::class.java)

                    data?.commentId=dataSnapshot.key.toString()

                    commentList.add(data!!)

                }

                adapter.notifyDataSetChanged()

                binding.commentRecyclerView.adapter=adapter


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

        })


        binding.commentPost.setOnClickListener {

            var comment = CommentModel()

            comment.commentBody = binding.commentContent.text.toString()
            comment.commentAt = Date().time
            comment.commentBy = FirebaseAuth.getInstance().uid.toString()

            var fire=FirebaseDatabase.getInstance().reference
                .child("reels")
                .child(reelsId)
                .child("comments")
                .push()

            var commId=fire.key.toString()




            comment.commentId=commId

            comment.commentPostId=reelsId


            fire.setValue(comment).addOnSuccessListener {


                FirebaseDatabase.getInstance().reference.child("reels").child(reelsId).child("comments").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        commentList.clear()

                        for (dataSnapshot in snapshot.children){

                            var data: CommentModel?= dataSnapshot.getValue(CommentModel::class.java)

                            data?.commentId=dataSnapshot.key.toString()

                            commentList.add(data!!)

                        }

                        adapter.notifyDataSetChanged()

                        binding.commentRecyclerView.adapter=adapter


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                    }

                })


                binding.commentContent.text.clear()

                hideKeyboard(binding.commentContent)


            }


        }





    }

    private fun showKeyboard(comment: EditText?) {

        var manager= context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.showSoftInput(comment?.rootView, InputMethodManager.SHOW_IMPLICIT)

        comment?.requestFocus()


    }

    private fun hideKeyboard(comment: EditText?) {

        var manager= requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.hideSoftInputFromWindow(comment?.applicationWindowToken,0)


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