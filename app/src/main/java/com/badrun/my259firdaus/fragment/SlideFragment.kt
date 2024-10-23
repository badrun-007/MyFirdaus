package com.badrun.my259firdaus.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SlideFragment : Fragment() {

    companion object {
        private const val ARG_LAYOUT_ID = "layoutId"

        fun newInstance(layoutId: Int): SlideFragment {
            val fragment = SlideFragment()
            val args = Bundle()
            args.putInt(ARG_LAYOUT_ID, layoutId)
            fragment.arguments = args
            return fragment
        }
    }

    private var layoutId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            layoutId = it.getInt(ARG_LAYOUT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }
}