package com.jettrivia.network

import com.jettrivia.model.Question
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface QuestionApiImp {
    @GET("world.json")
    suspend fun getAllQuestions(): Question
}