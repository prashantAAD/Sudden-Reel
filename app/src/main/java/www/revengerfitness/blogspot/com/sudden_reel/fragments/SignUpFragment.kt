package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentSignUpBinding
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel



class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentSignUpBinding.bind(view)

        firebaseAuth= FirebaseAuth.getInstance()
        firebaseDatabase= FirebaseDatabase.getInstance()

        binding.goToLogIn.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_manualLogInFragment)

        }


        binding.signup.setOnClickListener {

            val mail = binding.signupemail.text.toString().trim()
            val password = binding.signuppassword.text.toString().trim()
            val repassword = binding.signupRePassword.text.toString().trim()



            if (mail.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                Toast.makeText(requireContext(), "All Fields are Required", Toast.LENGTH_SHORT)
                    .show()
            } else if (password.length < 7) {


                Toast.makeText(requireContext(), "Password Should at least 8", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != repassword) {

                Toast.makeText(requireContext(), "Password Does Not Match", Toast.LENGTH_SHORT)
                    .show()

            } else {

                binding.progressBar.visibility= View.VISIBLE

                firebaseAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {


                            val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

                            val user: InformationModel = InformationModel(binding.signupname.text.toString(),
                                binding.signupemail.text.toString(),
                                binding.signuppassword.text.toString(),
                                "https://res.cloudinary.com/sunayan02/image/upload/v1644240537/dpholder_edwt0g.jpg",
                                firebaseUser?.uid.toString(),false)

                            if (firebaseUser != null) {
                                firebaseDatabase.reference.child("Users").child(firebaseUser.uid)
                                    .setValue(user)
                            }

                            Toast.makeText(requireContext(),
                                "Registration Successful",
                                Toast.LENGTH_SHORT).show()
                            sendEmailVerification()




                        } else {
                            Toast.makeText(requireContext(),
                                "Failed To Register",
                                Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility=View.GONE
                        }

                    }

            }
        }





    }

    private fun sendEmailVerification() {

        val firebaseUser: FirebaseUser? =firebaseAuth.currentUser

        binding.progressBar.visibility=View.GONE

        firebaseUser?.sendEmailVerification()?.addOnSuccessListener {


            Toast.makeText(
                requireContext(), "Verification Email is Send \n Verify and Log In Again", Toast.LENGTH_SHORT).show()

            Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_manualLogInFragment)



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