package com.theayushyadav11.MessEase .ui.NavigationDrawers.ViewModels

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theayushyadav11.MessEase.Models.Comment
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.Models.OptionSelected
import com.theayushyadav11.MessEase.Models.Particulars
import com.theayushyadav11.MessEase.Models.Poll
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDao
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel(val menuDao: MenuDao) : ViewModel() {
    val day = MutableLiveData<Int>().apply {
        value = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }
    val dayOfWeek = MutableLiveData<Int>().apply {
        val calendar = Calendar.getInstance()
        value = calendar.get(Calendar.DAY_OF_WEEK)
    }
    val uid=auth.currentUser?.uid.toString()
    @RequiresApi(Build.VERSION_CODES.O)
    val monthYear = MutableLiveData<String>().apply {

        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
        value = currentDate.format(formatter).uppercase()

    }


    val menu = MutableLiveData<Menu>()
    fun getDayPolls(user: User, date: String, onSuccess: (List<Poll>) -> Unit) {
        firestoreReference.collection("Polls").whereEqualTo("date", date)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onSuccess(listOf())
                    return@addSnapshotListener
                }
                val polls = mutableListOf<Poll>()
                value?.forEach {
                    val poll = it.toObject(Poll::class.java)
                    if(poll.target.contains(user.batch)&&poll.target.contains(user.passingYear)&&poll.target.contains(user.gender))
                    polls.add(poll)
                }
                polls.sortByDescending { it.comp }
                onSuccess(polls)
            }
    }

    fun getDayParticulars(day: Int, onSuccess: (List<Particulars>) -> Unit) {

        fireBase.getMainMenu {
            val list = it.menu[day].particulars
            onSuccess(list)
        }
//        viewModelScope.launch {
//            val list = menuDao.getMenu().menu[day].particulars
//            onSuccess(list)
//
//        }
    }
    fun selectOption(pid:String,optionSelected: OptionSelected)
    {

        firestoreReference.collection("PollResult").document(pid).collection("Users").document(uid).set(optionSelected)
    }
    fun removeSelection(pid:String)
    {
        firestoreReference.collection("PollResult").document(pid).collection("Users").document(uid).delete()
    }
    fun getVotesOnOption(pid:String, option:String,onResult: (Int) -> Unit)
    {
        firestoreReference.collection("PollResult").document(pid).collection("Users").whereEqualTo("selected",option).addSnapshotListener{
            value, error ->
            if(error!=null)
            {
                onResult(0)
                return@addSnapshotListener
            }
            val votes = value?.size()
            onResult(votes!!)
        }
    }
    fun getVoteByUid(pid:String,onResult: (String) -> Unit)
    {
        firestoreReference.collection("PollResult").document(pid).collection("Users").whereEqualTo("user.uid",uid).addSnapshotListener { value, error ->
            if(error!=null)
            {
                onResult("")
                return@addSnapshotListener
            }
            try {
                val option = value?.documents?.get(0)?.toObject(OptionSelected::class.java)?.selected
                onResult(option!!)
            } catch (e: Exception) {
                onResult("")
            }
        }
    }
  fun getTotalVotes(pid:String,onResult: (Int) -> Unit)
  {
      firestoreReference.collection("PollResult").document(pid).collection("Users").addSnapshotListener { value, error ->
          if(error!=null)
          {
              onResult(0)
              return@addSnapshotListener
          }
          val votes = value?.size()
          onResult(votes!!)
      }
  }
    fun getDayMsgs(user:User,date:String,onResult:(List<Msg>)->Unit)
    {
        firestoreReference.collection("Msgs").orderBy("comp", DESCENDING_ORDER).addSnapshotListener{
                value, error ->
            if(error!=null)
            {
                onResult(listOf())
                return@addSnapshotListener
            }
            val msgs = mutableListOf<Msg>()
            value?.documents?.forEach{
                val msg = it.toObject(Msg::class.java)

                val check= msg?.target?.contains(user.batch) == true && msg.target.contains(user.passingYear)&&msg.target.contains(user.gender)
                if(msg!=null)
                {
                    if(msg.date==date&&check)
                        msgs.add(msg)
                }
            }
            onResult(msgs)
        }
    }
    fun getComments(id:String,onResult:(List<Comment>)->Unit)
    {
        val ref= firestoreReference.collection("Msgs").document(id).collection("Comments").orderBy("comp",
            DESCENDING_ORDER)

        ref.addSnapshotListener{
            value,error->
            if(error!=null)
            {
                onResult(listOf())
                return@addSnapshotListener
            }
            val comments= mutableListOf<Comment>()
            for(document in value?.documents!!)
            {
                val comment=document.toObject(Comment::class.java)
                if(comment!=null)
                {
                    comments.add(comment)
                }
            }
            onResult(comments)

        }

    }
}
