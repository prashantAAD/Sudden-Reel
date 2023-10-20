package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.adapters.NotificationAdapter
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentNtCommentsBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.NotificationModel


class NtCommentsFragment : Fragment() {

    lateinit var binding: FragmentNtCommentsBinding

    lateinit var list: ArrayList<NotificationModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nt_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentNtCommentsBinding.bind(view)

        list= ArrayList()

        var adapter: NotificationAdapter = NotificationAdapter(list,requireContext())
        var layoutManager: LinearLayoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,true)
        binding.ntCommentsRecyclerview.layoutManager=layoutManager

        FirebaseDatabase.getInstance().reference
            .child("notification")
            .child(FirebaseAuth.getInstance().uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (dataSnapshot in snapshot.children) {
                        val notification: NotificationModel?= dataSnapshot.getValue(
                            NotificationModel::class.java)
                        notification?.notificationId=dataSnapshot.key.toString()

                        if (notification?.type=="comment")

                            list.add(notification)
                    }


                    adapter.notifyDataSetChanged()

                    binding.ntCommentsRecyclerview.adapter=adapter


                }

                override fun onCancelled(error: DatabaseError) {

                }



            })




        binding.CommentsNtDelete.setOnClickListener {


            if (list.size==0){
                Toast.makeText(requireContext(), "Notification is empty", Toast.LENGTH_SHORT).show()
            }
            else{

                val dialogView = View.inflate(context, R.layout.pop_up_dialog_layout, null)

                val builder = android.app.AlertDialog.Builder(context).setView(dialogView).create()

                builder.show()

                builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

                var dialogCancel = dialogView.findViewById(R.id.dialog_cancel) as ImageView
                var dialogYes=dialogView.findViewById(R.id.dialog_yes) as Button
                var dialogNo=dialogView.findViewById(R.id.dialog_no) as Button
                var dialogLogo=dialogView.findViewById(R.id.dialog_logo) as ImageView
                var dialogTxt=dialogView.findViewById(R.id.dialog_txt) as TextView

                dialogLogo.visibility=View.GONE

                dialogTxt.text="Delete all likes notifications?"


                dialogYes.setOnClickListener {

                    FirebaseDatabase.getInstance().reference.child("notification")
                        .child(FirebaseAuth.getInstance().uid.toString()).addValueEventListener(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                for (dataSnapshot in snapshot.children){

                                    var data: NotificationModel?=dataSnapshot.getValue(
                                        NotificationModel::class.java)

                                    data?.notificationId=dataSnapshot?.key.toString()

                                    if (data?.type=="comment"){

                                        FirebaseDatabase.getInstance().reference.child("notification")
                                            .child(FirebaseAuth.getInstance().uid.toString())
                                            .child(data?.notificationId.toString()).removeValue()

                                    }

                                }

                            }

                            override fun onCancelled(error: DatabaseError) {

                                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

                            }


                        })




                    builder.dismiss()

                }

                dialogNo.setOnClickListener {

                    builder.dismiss()

                }

                dialogCancel.setOnClickListener {

                    builder.dismiss()

                }




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