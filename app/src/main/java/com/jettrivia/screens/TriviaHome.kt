package com.jettrivia.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.jettrivia.screens.viewModels.QuestionsViewModel

@Composable
fun TriviaHome(viewModel: QuestionsViewModel = hiltViewModel()){
    Questions(viewModel = viewModel)
}
