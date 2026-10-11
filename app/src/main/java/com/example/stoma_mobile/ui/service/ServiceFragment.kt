package com.example.stoma_mobile.ui.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stoma_mobile.R
import com.example.stoma_mobile.ui.theme.StomaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            StomaTheme {
                ServiceScreen(
                    onServiceClick = { service ->
                        val args = Bundle().apply {
                            putString("serviceId", service.id)
                        }
                        findNavController().navigate(
                            R.id.action_service_to_doctor,
                            args
                        )
                    }
                )
            }
        }
    }
}