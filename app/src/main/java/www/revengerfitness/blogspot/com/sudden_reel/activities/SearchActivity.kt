package www.revengerfitness.blogspot.com.sudden_reel.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import www.revengerfitness.blogspot.com.sudden_reel.adapters.SearchAdapter
import www.revengerfitness.blogspot.com.sudden_reel.model.InformationModel
import www.revengerfitness.blogspot.com.sudden_reel.R

class SearchActivity : AppCompatActivity() {


    lateinit var list:ArrayList<InformationModel>

    lateinit var profileSearch:AutoCompleteTextView

    lateinit var searchButton:ImageView

    lateinit var rvProfiles:RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        profileSearch=findViewById(R.id.profile_search)

        searchButton=findViewById(R.id.search_button)

        rvProfiles=findViewById(R.id.rvProfiles)



        supportActionBar?.hide()


        this.theme?.applyStyle(R.style.FullScreen,false)
        this.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        showKeyboard(profileSearch)

        list= ArrayList()


        profileSearch.setOnEditorActionListener { p0, p1, p2 ->
            var s: String = profileSearch.text.toString().trim()

            if (p1== EditorInfo.IME_ACTION_DONE){

                hideKeyboard(profileSearch)

            }

            false
        }


        var titleList:ArrayList<String> = ArrayList()


        FirebaseDatabase.getInstance().reference.child("Users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){

                    val data: InformationModel?=dataSnapshot.getValue(InformationModel::class.java)
                    data?.userId=dataSnapshot.key.toString()

                        titleList.add(data?.userName!!)



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        val titleAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, titleList)


        profileSearch.setAdapter(titleAdapter)



        var adapter = SearchAdapter(list, this)
        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvProfiles.layoutManager = layoutManager


        profileSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {



            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {



            }

            override fun afterTextChanged(p0: Editable?) {

                FirebaseDatabase.getInstance().reference.child("Users")
                    .orderByChild("userName").startAt(profileSearch.text.toString()).endAt(profileSearch.text.toString()+"\uf8ff")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            list.clear()

                            for (dataSnapshot in snapshot.children){

                                var data=dataSnapshot.getValue(InformationModel::class.java)
                                data?.userId=dataSnapshot.key.toString()

                                    list.add(data!!)

                            }

                            adapter.notifyDataSetChanged()

                            rvProfiles.adapter=adapter

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })



            }


        })

        searchButton.setOnClickListener {

            FirebaseDatabase.getInstance().reference.child("Users").orderByChild("userName").startAt(profileSearch.text.toString()).endAt(profileSearch.text.toString()+"\uf8ff")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        list.clear()

                        for (dataSnapshot in snapshot.children){

                            var data=dataSnapshot.getValue(InformationModel::class.java)
                            data?.userId=dataSnapshot.key.toString()

                                list.add(data!!)


                        }

                        adapter.notifyDataSetChanged()

                        rvProfiles.adapter=adapter

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


        }


        profileSearch.setOnDismissListener {

            var inputMethodManager:InputMethodManager= getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.applicationWindowToken,0)

        }



    }

    private fun hideKeyboard(profileSearch: AutoCompleteTextView?) {

        var manager= getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        manager.hideSoftInputFromWindow(profileSearch?.applicationWindowToken,0)


    }

    private fun showKeyboard(profileSearch: AutoCompleteTextView?) {

        var manager= getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        manager.showSoftInput(profileSearch?.rootView, InputMethodManager.SHOW_IMPLICIT)

        profileSearch?.requestFocus()


    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(
            0,0)

    }


}