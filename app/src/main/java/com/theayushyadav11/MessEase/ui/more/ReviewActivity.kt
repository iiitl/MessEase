package com.theayushyadav11.MessEase.ui.more

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.theayushyadav11.MessEase.Models.Review
import com.theayushyadav11.MessEase.databinding.ActivityReviewBinding
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.databaseReference
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var mess: Mess
    val day = MutableLiveData<Int>()
    val rating = MutableLiveData<Float>()
    val foodtype = MutableLiveData<Int>()
    private val vm: ReviewViewModel by viewModels()
    val items2 = listOf("Breakfast", "Lunch", "Snacks", "Dinner")
    val items = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "AppReview")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        listeners()
    }

    private fun initialise() {
        mess = Mess(this)
        setUpToolBar()
        setUpAdapters()
        setUpFoodDisplay()
        setUpRatingBar()
        rating.value = 0.0f
    }

    private fun setUpFoodDisplay() {
        day.observe(this, Observer { selectedDay ->
            if (selectedDay == 7) {
                // App Review selected
                binding.typeContainer.visibility = View.GONE
                binding.food.text = "App Review"
                return@Observer
            } else {
                binding.typeContainer.visibility = View.VISIBLE
            }

            foodtype.observe(this, Observer { selectedType ->
                if (selectedDay != null && foodtype.value != null) {
                    vm.getMainMenu(this) { menuData ->
                        val dayIndex = selectedDay + 1
                        val food = menuData.menu[dayIndex].particulars[selectedType].food
                        binding.food.text = food
                    }
                }
            })
        })
    }

    private fun listeners() {
        binding.btnPost.setOnClickListener {
            val foodText = binding.food.text.toString()
            val reviewText = binding.review.text.toString()

            if (foodText.isNotEmpty()) {
                if (rating.value!! > 0.0f) {
                    addReview(foodText, reviewText)
                } else {
                    mess.toast("Please select a rating")
                }
            } else {
                mess.toast("Please select a food item")
            }
        }
    }

    private fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Add Review"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun addReview(food: String, review: String) {
        mess.addPb("Adding Review...")
        val user = mess.getUser()
        val key = databaseReference.push().key.toString()

        val rv = Review(
            id = key,
            creater = user,
            food = food,
            day = items[day.value!!],
            foodtype = if (day.value == 7) "" else items2[foodtype.value!!],
            rating = rating.value!!,
            review = review.trim(),
            dateTime = getCurrentTimeAndDate()
        )

        firestoreReference.collection("Reviews").document(key).set(rv)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mess.toast("Review Added")
                    mess.pbDismiss()
                    resetForm()
                } else {
                    mess.toast("Failed to add review")
                    mess.pbDismiss()
                }
            }
    }

    private fun resetForm() {
        binding.review.text = null
        binding.ratingBar.rating = 0.0f
        // Optionally reset dropdowns to default values
        (binding.day as? MaterialAutoCompleteTextView)?.setText(items[0], false)
        (binding.type as? MaterialAutoCompleteTextView)?.setText(items2[0], false)
    }

    private fun getCurrentTimeAndDate(): String {
        val dateFormat = SimpleDateFormat("hh:mm a dd MMM yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun setUpAdapters() {
        // Day adapter
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        (binding.day as? MaterialAutoCompleteTextView)?.setAdapter(dayAdapter)

        // Meal type adapter
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items2)
        (binding.type as? MaterialAutoCompleteTextView)?.setAdapter(typeAdapter)

        // Set initial values
        (binding.day as? MaterialAutoCompleteTextView)?.setText(items[0], false)
        (binding.type as? MaterialAutoCompleteTextView)?.setText(items2[0], false)

        // Set item click listeners
        (binding.day as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            day.value = position
        }

        (binding.type as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            foodtype.value = position
        }

        // Initialize default values
        day.value = 0
        foodtype.value = 0
    }

    private fun setUpRatingBar() {
        binding.ratingBar.setOnRatingBarChangeListener { _, newRating, fromUser ->
            if (fromUser) {
                this.rating.value = newRating
            }
        }
    }
}