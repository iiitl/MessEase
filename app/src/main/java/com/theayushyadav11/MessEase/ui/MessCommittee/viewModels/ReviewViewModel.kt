package com.theayushyadav11.MessEase.ui.MessCommittee.viewModels

import androidx.lifecycle.ViewModel
import com.theayushyadav11.MessEase.Models.Review
import com.theayushyadav11.MessEase.utils.Constants.Companion.COMPARER
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.REVIEWS
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference

class ReviewViewModel : ViewModel() {
    fun getAllReviews(onResult:(List<Review>)->Unit)
    {
          firestoreReference.collection(REVIEWS).orderBy(COMPARER, DESCENDING_ORDER).addSnapshotListener{
              value,error->
                if(error!=null)
                {
                    onResult(listOf())
                    return@addSnapshotListener
                }

              val reviews= mutableListOf<Review>()
              for(document in value?.documents!!)
              {
                  val review=document.toObject(Review::class.java)!!
                  reviews.add(review)
              }
              onResult(reviews)

          }
    }
}