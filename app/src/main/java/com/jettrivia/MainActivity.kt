package com.jettrivia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jettrivia.screens.TriviaHome
import com.jettrivia.ui.theme.JetTriviaTheme
import com.jettrivia.util.AppColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTriviaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppColors.mDarkPurple
                ) {
                    TriviaHome()
                }
            }
        }
    }
}
