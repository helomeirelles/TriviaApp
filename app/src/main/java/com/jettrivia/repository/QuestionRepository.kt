package com.jettrivia.repository

import android.util.Log
import com.jettrivia.data.DataOrException
import com.jettrivia.model.QuestionItem
import com.jettrivia.network.QuestionApiImp
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val api: QuestionApiImp) {

    private val dataOrException = DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        with(dataOrException) {
            try {
                loading = true
                data = api.getAllQuestions()
                if (data.toString().isNotEmpty()) loading = false
            } catch (exception: java.lang.Exception) {
                e = exception
                Log.d("Exception", "getAllQuestions: ${e?.localizedMessage}")
            }
            return this
        }
    }
}