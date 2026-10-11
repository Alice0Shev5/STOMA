package com.example.stoma_mobile.ui.doctor

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
class DoctorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            StomaTheme {
                DoctorScreen(
                    onDoctorClick = { doctor ->
                        val serviceId = arguments?.getString("serviceId").orEmpty()
                        val args = Bundle().apply {
                            putString("doctorId", doctor.id)
                            putString("serviceId", serviceId)
                        }
                        findNavController().navigate(
                            R.id.action_doctor_to_calendar,
                            args
                        )
                    }
                )
            }
        }
    }
}