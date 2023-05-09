package com.jettrivia.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jettrivia.model.QuestionItem
import com.jettrivia.screens.viewModels.QuestionsViewModel
import com.jettrivia.util.AppColors

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()
    val questionIndex = remember {
        mutableStateOf(0)
    }
    if (viewModel.data.value.loading == true) {
        ShowProgressBar()

    } else {

        val question = try {
            questions?.get(questionIndex.value)
        } catch (e: Exception) {
            null
        }
        if (questions != null) {
            if (question != null) {
                QuestionDisplay(question = question, questionIndex = questionIndex, viewModel) {
                    questionIndex.value = questionIndex.value + 1
                }
            }
        }
    }


}

@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked: (Int) -> Unit = {}
) {
    val choicesState = remember(question) {
        question.choices.toMutableList()
    }

    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }

    val currentScore = remember {
        mutableStateOf(0)
    }

    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }

    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }

    val radioButtonState = remember {
        mutableStateOf(true)
    }

    val updateRadioButtonState: (Boolean) -> Unit = remember {
        {
            radioButtonState.value = it
        }
    }

    val updateCurrentScore: (Int) -> Unit = remember {
        {
            currentScore.value = it
        }
    }

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = AppColors.mDarkPurple
    )
    {

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (questionIndex.value >= 1) {
                ShowScore(score = currentScore.value)
            }
            viewModel.data.value.data?.size?.let {
                QuestionTracker(
                    counter = questionIndex.value + 1,
                    outOff = it
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            DrawDottedLine(pathEffect = pathEffect)
            Spacer(modifier = Modifier.size(20.dp))
            QuestionText(question.question)
            choicesState.forEachIndexed { index, answerText ->
                RadioButtonComponent(
                    radioButtonState,
                    answerState,
                    index,
                    updateAnswer,
                    correctAnswerState,
                    updateCurrentScore,
                    currentScore,
                    updateRadioButtonState,
                    answerText
                )
            }

            Spacer(modifier = Modifier.size(30.dp))

            NextButtonComponent(onNextClicked, questionIndex, updateRadioButtonState)


        }
    }
}

@Composable
fun NextButtonComponent(
    onNextClicked: (Int) -> Unit,
    questionIndex: MutableState<Int>,
    updateRadioButtonState: (Boolean) -> Unit
) {
    Button(
        onClick = {
            onNextClicked(questionIndex.value)
            updateRadioButtonState(true)
        },
        shape = RoundedCornerShape(25.dp),
        colors = buttonColors(
            backgroundColor = AppColors.mLighGray,
            contentColor = AppColors.mDarkPurple
        ),
        contentPadding = PaddingValues(15.dp)
    ) {
        Text(text = "Next")
    }
}

@Composable
fun RadioButtonComponent(
    radioButtonState: MutableState<Boolean>,
    answerState: MutableState<Int?>,
    index: Int,
    updateAnswer: (Int) -> Unit,
    correctAnswerState: MutableState<Boolean?>,
    updateCurrentScore: (Int) -> Unit,
    currentScore: MutableState<Int>,
    updateRadioButtonState: (Boolean) -> Unit,
    answerText: String
) {
    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .clip(
                RoundedCornerShape(50)
            )
            .background(AppColors.mLighGray),
        verticalAlignment = CenterVertically,
    ) {
        RadioButton(
            enabled = radioButtonState.value,
            selected = (answerState.value == index), onClick = {
                updateAnswer(index)
                if (correctAnswerState.value == true && index == answerState.value)
                    updateCurrentScore(currentScore.value + 1)
                updateRadioButtonState(false)
            },
            modifier = Modifier.padding(start = 16.dp),

            colors = RadioButtonDefaults.colors(
                selectedColor = if (correctAnswerState.value == true && index == answerState.value) {
                    Color.Green

                } else {
                    Color.Red
                },
                unselectedColor = AppColors.mDarkPurple
            )
        )

        val annotatedString = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Light,
                    color = if (correctAnswerState.value == true && index == answerState.value) {
                        Color.Green
                    } else if (correctAnswerState.value == false && index == answerState.value) {
                        Color.Red
                    } else {
                        AppColors.mDarkPurple
                    },
                    fontSize = 17.sp
                )
            ) {
                append(answerText)
            }
        }
        Text(text = annotatedString, modifier = Modifier.padding(6.dp))


    }
}


@Composable
fun QuestionTracker(counter: Int = 10, outOff: Int = 100) {

    Text(text = buildAnnotatedString {
        withStyle(ParagraphStyle(textIndent = TextIndent.None)) {}
        withStyle(
            SpanStyle(
                color = AppColors.mLighGray,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp
            )
        ) {
            append("Question $counter/")
            withStyle(
                style = SpanStyle(
                    color = AppColors.mLighGray,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                )
            ) {
                append("$outOff")
            }
        }
    })
}

@Composable
fun ShowProgressBar() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.mDarkPurple
    ) {
        Column(
            modifier = Modifier.size(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = AppColors.mLighGray)
        }
    }
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = AppColors.mLighGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}

@Composable
fun QuestionText(question: String) {
    Column {
        Text(
            text = question,
            modifier = Modifier
                .padding(6.dp)
                .align(alignment = Alignment.Start)
                .fillMaxHeight(0.3f),

            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp,
            color = AppColors.mOffWhite
        )
    }
}

@Composable
fun ShowScore(score: Int) {
    val progressFactor by remember(score) {
        mutableStateOf(score * 0.005f)
    }
    val gradient = Brush.linearGradient(listOf(AppColors.mLighGray, AppColors.mOffWhite))
    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppColors.mLighGray,
                        AppColors.mOffWhite
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            )
            .clip(RoundedCornerShape(50))
            .background(Color.Transparent),
        verticalAlignment = CenterVertically
    ) {
        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth(progressFactor)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent
            )
        ) {}
        Column(horizontalAlignment = CenterHorizontally) {
            Text(
                text = "$score",
                color = AppColors.mBlack,
                textAlign = TextAlign.Center

            )
        }

    }
}


