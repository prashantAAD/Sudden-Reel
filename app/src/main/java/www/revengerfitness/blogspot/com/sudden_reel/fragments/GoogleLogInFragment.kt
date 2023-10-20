package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.os.Bundle
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.activities.MainActivity
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentGoogleLogInBinding

import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel



class GoogleLogInFragment : Fragment() {

    lateinit var binding: FragmentGoogleLogInBinding

//    lateinit var signInRequest: BeginSignInRequest



    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var firebaseAuth: FirebaseAuth

    private companion object{

        private const val RC_SIGN_IN=100

        private const val TAG="GOOGLE_SIGN_IN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentGoogleLogInBinding.bind(view)

        firebaseAuth= FirebaseAuth.getInstance()

        checkUser()


        val googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("474001678404-f7tibf9a7po5hs93s4ramtfke5mgqomi.apps.googleusercontent.com")
            .requestEmail()
            .build()



        googleSignInClient=GoogleSignIn.getClient(requireActivity(),googleSignInOptions)



        binding.goToManualLogIn.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_googleLogInFragment_to_manualLogInFragment)

        }

        val paint: TextPaint = binding.logoTxt.paint
        val width = paint.measureText(getString(R.string.app_name))


        val textShader: Shader = LinearGradient(
            0f, 0f, width, binding.logoTxt.textSize, intArrayOf(
                Color.parseColor("#feda75"),
                Color.parseColor("#962fbf"),
                Color.parseColor("#d62976")
            ), null, TileMode.CLAMP
        )
        binding.logoTxt.paint.shader = textShader


        binding.signInBtn.setOnClickListener {

            val intent=googleSignInClient.signInIntent

            startActivityForResult(intent, RC_SIGN_IN)


        }


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        when (requestCode) {

            RC_SIGN_IN -> {

                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {

                    val account=accountTask.getResult(ApiException::class.java)

                    firebaseAuthWithGoogleAccount(account)

                }
                catch (e:Exception){

                    Toast.makeText(requireContext(),e.message, Toast.LENGTH_SHORT).show()

                }

                }
            }
        }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->

                val firebaseUser = firebaseAuth.currentUser


                if (authResult.additionalUserInfo!!.isNewUser){

                    var user: InformationModel = InformationModel(
                        firebaseUser?.displayName.toString(),
                        firebaseUser?.email.toString(),
                        "",
                        firebaseUser?.photoUrl.toString(),
                        firebaseUser?.uid.toString(), false
                    )

                    if (firebaseUser != null) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
                            .setValue(user)

                        Toast.makeText(
                            requireContext(),
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                        ).show()


                    }

                    else{

                        Toast.makeText(requireContext(), "User is not found", Toast.LENGTH_SHORT).show()

                    }



                }

                else{

                    Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()

                }


                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))

                requireActivity().overridePendingTransition(0,0)

                requireActivity().finish()





            }
            .addOnFailureListener {

                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }



    }

    private fun checkUser(){

        val firebaseUser=firebaseAuth.currentUser

        if (firebaseUser!=null){

            requireActivity().startActivity(Intent(requireContext(),MainActivity::class.java))

            requireActivity().overridePendingTransition(0,0)

            requireActivity().finish()

        }

        else{

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