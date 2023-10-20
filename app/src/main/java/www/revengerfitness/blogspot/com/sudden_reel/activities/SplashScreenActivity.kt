package www.revengerfitness.blogspot.com.sudden_reel.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView

import www.revengerfitness.blogspot.com.sudden_reel.R
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    lateinit var splashAnimation:LottieAnimationView

    lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        this.theme?.applyStyle(R.style.FullScreen,false)
        this.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        splashAnimation=findViewById(R.id.splash_animation)

        timer= Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0,0)
                finish()
            }
        }, 8000)




    }
}