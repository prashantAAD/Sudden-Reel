package www.revengerfitness.blogspot.com.sudden_reel.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import www.revengerfitness.blogspot.com.sudden_reel.R

class LogInActivity : AppCompatActivity() {

//    lateinit var signInBtn:SignInButton
//
//    lateinit var mGoogleSignInClient: GoogleSignInClient
//
//    private val default_web_client_id= "1234"
//
//    lateinit var mAuth:FirebaseAuth
//
//    private val RC_SIGN_IN = 123

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()

        this.theme?.applyStyle(R.style.FullScreen,false)
        this.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)




        firebaseAuth= FirebaseAuth.getInstance()

        val firebaseUser=firebaseAuth.currentUser

        if(firebaseUser!=null){
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()

        }



    }



}