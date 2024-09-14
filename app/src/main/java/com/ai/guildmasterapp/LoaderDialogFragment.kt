package com.ai.guildmasterapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class LoaderDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_CUSTOM_MESSAGE = "Any Custom Message"

        fun newInstance(customMessage: String?): LoaderDialogFragment {
            val fragment = LoaderDialogFragment()
            val args = Bundle()
            args.putString(ARG_CUSTOM_MESSAGE, customMessage)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_loader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingTextView = view.findViewById<TextView>(R.id.loading)
        loadingTextView.text = "Loading..."

        // Retrieve the custom message from arguments
        val customMessage = arguments?.getString(ARG_CUSTOM_MESSAGE)

        val customMessageTextView = view.findViewById<TextView>(R.id.custom_message)

        if (!customMessage.isNullOrEmpty()) {
            customMessageTextView.text = customMessage
            customMessageTextView.visibility = View.VISIBLE
        } else {
            customMessageTextView.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
