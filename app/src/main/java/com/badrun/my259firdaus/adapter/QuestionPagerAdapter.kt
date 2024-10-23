package com.badrun.my259firdaus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.SoalActivity
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.model.AnswerRequest
import com.badrun.my259firdaus.model.Soal
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class QuestionPagerAdapter(
    private val context: Context,
    private val questions: List<Soal>,
    private val studentAnswers: MutableMap<Int, String>,
    private val onQuestionAnswered: (Int, String, String) -> Unit
) : PagerAdapter() {

    private val answers: MutableMap<Int, Int> = mutableMapOf() // Menyimpan jawaban santri
    private var currentFontSize: Float = 18f
    private val views: MutableList<View> = mutableListOf()

    override fun getCount(): Int {
        return questions.size
    }

    override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
        return view === `object` as View
    }

    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
        val question = questions[position]
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_question, container, false)

        val questionTextView: TextView = view.findViewById(R.id.question_text)
        val imageView: PhotoView = view.findViewById(R.id.image_question)
        val passageTextView: TextView = view.findViewById(R.id.passage_text)
        val radioGroup: RadioGroup = view.findViewById(R.id.radio_group_options)

        questionTextView.textSize = currentFontSize
        passageTextView.textSize = currentFontSize

        // Set question text
        questionTextView.text = question.question_text

        // Load image if available
        if (question.image_url != null) {
            imageView.visibility = View.VISIBLE
            val imgLink = "https://${ApiConfig.iplink.ip}/storage/soal/" + question.image_url
            Picasso.get()
                .load(imgLink)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.errorimage)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        // Image loaded successfully
                    }

                    override fun onError(e: Exception?) {
                        // Image load failed, set click listener to retry loading the image
                        imageView.setOnClickListener {
                            Picasso.get()
                                .load(imgLink)
                                .placeholder(R.drawable.progress_animation)
                                .error(R.drawable.errorimage)
                                .into(imageView)
                        }
                    }
                })
        } else {
            imageView.visibility = View.GONE
        }

        // Set passage if available
        if (question.passage != null) {
            passageTextView.text = question.passage
            passageTextView.visibility = View.VISIBLE
        } else {
            passageTextView.visibility = View.GONE
        }

        val customFont = ResourcesCompat.getFont(context, R.font.arabic)
        val inactiveColor = ContextCompat.getColor(context, R.color.inactive_text_color)
        // Add options to the RadioGroup
        radioGroup.removeAllViews()
        for (option in question.options) {
            val radioButton = RadioButton(context).apply {
                text = option.text
                tag = option.label // Set label sebagai tag untuk akses mudah
                id = View.generateViewId() // Generate ID baru
                textSize = currentFontSize
                typeface = customFont
                setTextColor(inactiveColor)
            }
            radioGroup.addView(radioButton)
        }

        val selectedAnswer = studentAnswers[question.id] // Get the saved answer for this question
        if (selectedAnswer != null) {
            for (i in 0 until radioGroup.childCount) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                if (radioButton.text.toString() == selectedAnswer) {
                    radioButton.isChecked = true // Re-check the correct answer
                    break
                }
            }
        }

        answers[position]?.let { savedAnswerId ->
            radioGroup.check(savedAnswerId) // Periksa RadioButton dengan ID yang disimpan
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            val selectedText = radioButton.text.toString()
            val selectedLabel = radioButton.tag.toString()
            // Simpan jawaban yang dipilih
            onQuestionAnswered(position, selectedLabel,selectedText )
        }

        views.add(view)
        // Add the view to the ViewPager
        container.addView(view)
        return view
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        container.removeView(`object` as View)
        // Jangan menghapus dari views di sini
    }

    fun updateFontSize(fontSize: Float) {
        currentFontSize = fontSize

        // Iterasi untuk mengupdate setiap view yang sudah ada
        for (view in views) {
            val questionTextView: TextView = view.findViewById(R.id.question_text)
            val passageTextView: TextView = view.findViewById(R.id.passage_text)

            // Update ukuran font
            questionTextView.textSize = currentFontSize
            passageTextView.textSize = currentFontSize

            // Update ukuran font untuk radio buttons
            val radioGroup: RadioGroup = view.findViewById(R.id.radio_group_options)
            for (j in 0 until radioGroup.childCount) {
                val radioButton = radioGroup.getChildAt(j) as RadioButton
                radioButton.textSize = currentFontSize
            }
        }

        notifyDataSetChanged() // Refresh untuk menerapkan perubahan ukuran font
    }
}




