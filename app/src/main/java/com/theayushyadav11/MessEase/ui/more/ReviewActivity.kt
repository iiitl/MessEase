package com.theayushyadav11.MessEase.ui.more

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
    val items2 = listOf("Breakfast", "Lunch", "Snacks", "Dinner")
    val items =
        listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday","AppReview")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        listeners()
    }

    private fun initialise() {
        mess = Mess(this)
        setUpAdapter()
        setUpToolBar()
        setUpfood()
        ratingBar()
        rating.value=0.0f
    }

    private fun setUpfood() {
        day.observe(this, Observer { day ->
            if(day==7)
            {
                binding.type.visibility=View.GONE
                binding.food.setText("App Review")
                return@Observer
            }
            else
            {
                binding.type.visibility=View.VISIBLE
            }
            foodtype.observe(this, Observer { type ->
                if (day != null && foodtype.value != null) {
                    fireBase.getMainMenu {
                        val d = if (day == 6) 0 else day + 1
                        val food = it.menu[d].particulars[type].food
                        binding.food.setText(food)
                    }
                }
            })
        })
    }

    private fun listeners() {
        binding.btnPost.setOnClickListener {
            if (binding.food.text.isNotEmpty())
            {
                    if (rating.value!! > 0.0f) {
                        addReview(binding.food.text.toString(), binding.review.text.toString())
                    }
                    else
                    {
                        mess.toast("Please select rating")
                    }

            } else {
                mess.toast("Please select food ")
            }
        }
    }

    fun setUpToolBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Add Review"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun addReview(food: String, review: String) {
        mess.addPb("Adding Review...")
        fireBase.getUser(auth.currentUser!!.uid, onSuccess = { it ->
            val key = databaseReference.push().key.toString()
            val rv = Review(
                id = key,
                creater = it,
                food = food,
                day=items[day.value!!],
                foodtype=if(day.value==7) "" else items2[foodtype.value!!] ,
                rating = rating.value!!,
                review = review,
                dateTime = getCurrentTimeAndDate()
            )
            firestoreReference.collection("Reviews").document(key).set(rv).addOnCompleteListener { n->
                if (n.isSuccessful) {
                    mess.toast("Review Added")
                    mess.pbDismiss()
                    binding.review.text=null
                    binding.ratingBar.rating=0.0f
                } else {
                    mess.toast("Failed to add review")
                    mess.pbDismiss()
                }
            }

        },
            onFailure = {
                mess.pbDismiss()
            })

    }

    private fun getCurrentTimeAndDate(): String {
        val dateFormat = SimpleDateFormat("hh:mm a dd MMM yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun setUpAdapter() {

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, items)

        val spinner = binding.day
        spinner.setAdapter(adapter)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                day.value = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                day.value = 0
            }
        }

        val adapter2 = ArrayAdapter(this, R.layout.simple_list_item_1, items2)

        val spinner2 = binding.type
        spinner2.setAdapter(adapter2)

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner2.adapter = adapter2
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                foodtype.value = position

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                foodtype.value = 0
            }
        }

    }
    private fun ratingBar() {
        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if(fromUser)
            {

                this.rating.value=rating
            }
        }
    }
}