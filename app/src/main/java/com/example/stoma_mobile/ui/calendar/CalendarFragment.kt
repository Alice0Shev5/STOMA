package com.example.stoma_mobile.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stoma_mobile.ui.theme.StomaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            StomaTheme {
                CalendarScreen(
                    onBack = { findNavController().navigateUp() },
                    onConfirmed = {
                        // Возвращаемся к стартовому экрану (список услуг),
                        // очищая бэк-стек — запись оформлена
                        findNavController().popBackStack(
                            com.example.stoma_mobile.R.id.nav_service,
                            inclusive = false
                        )
                    }
                )
            }
        }
    }
}