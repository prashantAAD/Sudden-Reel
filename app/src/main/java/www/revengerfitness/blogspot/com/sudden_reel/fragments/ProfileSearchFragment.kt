package www.revengerfitness.blogspot.com.sudden_reel.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import www.revengerfitness.blogspot.com.sudden_reel.R
import www.revengerfitness.blogspot.com.sudden_reel.databinding.FragmentProfileSearchBinding


class ProfileSearchFragment : Fragment() {

    lateinit var binding: FragmentProfileSearchBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_profile_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentProfileSearchBinding.bind(view)

        showKeyboard(binding.profileSearch)






    }

    private fun hideKeyboard(profileSearch: AutoCompleteTextView?) {

        var manager= requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.hideSoftInputFromWindow(profileSearch?.applicationWindowToken,0)


    }

    private fun showKeyboard(profileSearch: AutoCompleteTextView?) {

        var manager= requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.showSoftInput(profileSearch?.rootView, InputMethodManager.SHOW_IMPLICIT)

        profileSearch?.requestFocus()


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