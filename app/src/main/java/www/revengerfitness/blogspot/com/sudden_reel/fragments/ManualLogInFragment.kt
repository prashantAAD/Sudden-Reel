package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.activities.MainActivity
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentManualLogInBinding


class ManualLogInFragment : Fragment() {

    lateinit var binding: FragmentManualLogInBinding

    lateinit var firebaseAuth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manual_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentManualLogInBinding.bind(view)

        firebaseAuth= FirebaseAuth.getInstance()

        binding.goToGoogle.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_manualLogInFragment_to_googleLogInFragment)

        }

        binding.goToSignUp.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_manualLogInFragment_to_signUpFragment)

        }

        binding.goToForgotPassword.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_manualLogInFragment_to_forgotPasswordFragment)

        }

        binding.signin.setOnClickListener {
            val mail: String = binding.loginemail.text.toString().trim()
            val password: String = binding.loginpassword.text.toString().trim()
            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "All Field Are Required", Toast.LENGTH_SHORT)
                    .show()
            } else {


                binding.progressBar.visibility= View.VISIBLE

                firebaseAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {


                            checkMailVerification()
                        } else {
                            Toast.makeText(requireContext(),
                                "Account Doesn't Exist",
                                Toast.LENGTH_SHORT)
                                .show()
                            binding.progressBar.visibility= View.INVISIBLE
                        }
                    }
            }

        }

    }

    private fun checkMailVerification() {


        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser!!.isEmailVerified) {
            Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility=View.INVISIBLE

            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

            activity?.finish()




        } else {
            Toast.makeText(requireContext(), "Verify your mail first", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility=View.INVISIBLE
            firebaseAuth.signOut()
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