package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.adapters.ReelsAdapter
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentReelsViewBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.ReelsModel


class ReelsViewFragment : Fragment() {

    lateinit var binding: FragmentReelsViewBinding

    lateinit var list: ArrayList<ReelsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reels_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentReelsViewBinding.bind(view)

        list=ArrayList()

        val adapter= ReelsAdapter(list,requireContext())


        val reelsId = requireArguments().getString("reelsId").toString()


        FirebaseDatabase.getInstance().reference.child("reels").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (dataSnapshot in snapshot.children){

                    var data: ReelsModel?=dataSnapshot.getValue(ReelsModel::class.java)

                    data?.reelsId=dataSnapshot.key.toString()

                    if (data?.reelsId==reelsId) {

                        list.add(data)

                    }




                }

                list.reverse()

                adapter.notifyDataSetChanged()


                binding.reelsViewViewPager.adapter=adapter


            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()

            }


        })




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