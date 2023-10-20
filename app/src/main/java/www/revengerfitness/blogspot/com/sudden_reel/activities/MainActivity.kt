package www.revengerfitness.blogspot.com.sudden_reel.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import www.revengerfitness.blogspot.com.sudden_reel.R

class MainActivity : AppCompatActivity() {
    private lateinit var bottomBar: BottomNavigationView

    private lateinit var navController: NavController

    private lateinit var bottomBarFloatButton: CardView


    //    lateinit var navHome:BottomNavigationItemView
//    lateinit var navSearch:BottomNavigationItemView
//    lateinit var navAdd:BottomNavigationItemView
//    lateinit var navNotification:BottomNavigationItemView
//    lateinit var navProfile:BottomNavigationItemView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomBar=findViewById(R.id.bottom_bar)

        bottomBarFloatButton=findViewById(R.id.bottom_bar_floatButton)


        supportActionBar?.hide()

        this.theme?.applyStyle(R.style.FullScreen,false)
        this.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        val fragments = supportFragmentManager.findFragmentById(R.id.fragments) as NavHostFragment



        navController=fragments.navController


        setupWithNavController(bottomBar,navController)

        setupActionBarWithNavController(navController)


//        navHome=findViewById(R.id.nav_home)
//        navSearch=findViewById(R.id.nav_search)
//        navAdd=findViewById(R.id.nav_add)
//        navNotification=findViewById(R.id.nav_notification)
//        navProfile=findViewById(R.id.nav_profile)


        bottomBarFloatButton.setOnClickListener()
        {

            val intent = Intent(this, AddActivity::class.java)

            startActivity(intent)

            overridePendingTransition(0, 0)

        }

    }


//        replaceFragment(HomeFragment())
//
//        navHome.isClickable=false
//        navSearch.isClickable=true
//        navAdd.isClickable=true
//        navNotification.isClickable=true
//        navProfile.isClickable=true
//
//        bottomBar.setOnNavigationItemSelectedListener {
//
//            when(it.itemId){
//
//                R.id.nav_home ->
//                {
//                    replaceFragment(HomeFragment())
//
//                    navHome.isClickable=false
//                    navSearch.isClickable=true
//                    navAdd.isClickable=true
//                    navNotification.isClickable=true
//                    navProfile.isClickable=true
//
//
//
//                }
//                R.id.nav_search ->
//                {
//                    replaceFragment(SearchFragment())
//
//                    navHome.isClickable=true
//                    navSearch.isClickable=false
//                    navAdd.isClickable=true
//                    navNotification.isClickable=true
//                    navProfile.isClickable=true
//
//                }
//
//                R.id.nav_add ->{
//
//                    replaceFragment(AddFragment())
//
//                    navHome.isClickable=true
//                    navSearch.isClickable=true
//                    navAdd.isClickable=false
//                    navNotification.isClickable=true
//                    navProfile.isClickable=true
//
//
//                }
//                R.id.nav_notification ->{
//
//                    replaceFragment(NotificationFragment())
//
//                    navHome.isClickable=true
//                    navSearch.isClickable=true
//                    navAdd.isClickable=true
//                    navNotification.isClickable=false
//                    navProfile.isClickable=true
//
//                }
//                R.id.nav_profile ->{
//
//                    replaceFragment(ProfileFragment())
//
//                    navHome.isClickable=true
//                    navSearch.isClickable=true
//                    navAdd.isClickable=true
//                    navNotification.isClickable=true
//                    navProfile.isClickable=false
//
//                }
//
//            }
//
//            return@setOnNavigationItemSelectedListener true
//        }


}