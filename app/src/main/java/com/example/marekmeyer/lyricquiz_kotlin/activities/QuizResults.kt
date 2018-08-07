package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.marekmeyer.lyricquiz_kotlin.R
import com.example.marekmeyer.lyricquiz_kotlin.models.DataManager

class QuizResults : AppCompatActivity() {

    private lateinit var resultsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_quiz_results)
        resultsTextView = findViewById(R.id.quizResultsTextView)

        title = resources.getString(R.string.title_activity_quiz_results)
        showQuizResults()
    }

    private fun showQuizResults(){

        val numberOfCorrectAnswers = DataManager.quiz.questions.fold(0) { acc, question ->
            if(question.correct){
                return@fold acc + 1
            }
            return@fold acc
        }
        resultsTextView.text = "You answered $numberOfCorrectAnswers out of ${DataManager.quiz.questions.size} questions correctly"

    }


    fun backToMenu(view: View){
        val intent = Intent(this, MainNavigation::class.java)
        startActivity(intent)
    }
}
