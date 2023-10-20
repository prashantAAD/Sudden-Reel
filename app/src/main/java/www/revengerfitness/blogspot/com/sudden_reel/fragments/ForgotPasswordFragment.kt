package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentForgotPasswordBinding.bind(view)

        binding.gobacktologin.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_manualLogInFragment)

        }


        binding.passwordrecover.setOnClickListener {

            binding.progressBar.visibility=View.VISIBLE

            val mail: String = binding.forgotpasswordtext.text.toString().trim()
            if (mail.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your mail first", Toast.LENGTH_SHORT)
                    .show()

                binding.progressBar.visibility=View.GONE

            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(),
                            "Mail Sent , You can recover your password using mail",
                            Toast.LENGTH_SHORT).show()

                        Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_manualLogInFragment)

                        binding.progressBar.visibility=View.GONE


                    }

                    else {
                        Toast.makeText(requireContext(),
                            "Email is Wrong or Account Not Exits",
                            Toast.LENGTH_SHORT).show()

                        binding.progressBar.visibility=View.GONE
                    }
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