package com.theayushyadav11.MessEase.ui.NavigationDrawers.ViewModels

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.theayushyadav11.MessEase.Models.Comment
import com.theayushyadav11.MessEase.Models.Menu
import com.theayushyadav11.MessEase.Models.Msg
import com.theayushyadav11.MessEase.Models.OptionSelected
import com.theayushyadav11.MessEase.Models.Particulars
import com.theayushyadav11.MessEase.Models.Poll
import com.theayushyadav11.MessEase.Models.User
import com.theayushyadav11.MessEase.RoomDatabase.MenuDataBase.MenuDao
import com.theayushyadav11.MessEase.utils.Constants.Companion.COMMENTS
import com.theayushyadav11.MessEase.utils.Constants.Companion.COMPARER
import com.theayushyadav11.MessEase.utils.Constants.Companion.DATE
import com.theayushyadav11.MessEase.utils.Constants.Companion.DESCENDING_ORDER
import com.theayushyadav11.MessEase.utils.Constants.Companion.MESSAGES
import com.theayushyadav11.MessEase.utils.Constants.Companion.POLLS
import com.theayushyadav11.MessEase.utils.Constants.Companion.POLL_RESULT
import com.theayushyadav11.MessEase.utils.Constants.Companion.SELECTED_OPTION
import com.theayushyadav11.MessEase.utils.Constants.Companion.UID
import com.theayushyadav11.MessEase.utils.Constants.Companion.USERS
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Constants.Companion.fireBase
import com.theayushyadav11.MessEase.utils.Constants.Companion.firestoreReference
import com.theayushyadav11.MessEase.utils.Mess
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
    val uid = auth.currentUser?.uid.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    val monthYear = MutableLiveData<String>().apply {

        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
        value = currentDate.format(formatter).uppercase()

    }


    val menu = MutableLiveData<Menu>()
    fun getDayPolls(user: User, date: String, onSuccess: (List<Poll>) -> Unit) {
        firestoreReference.collection(POLLS).whereEqualTo(DATE, date)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onSuccess(listOf())
                    return@addSnapshotListener
                }
                val polls = mutableListOf<Poll>()
                value?.forEach {
                    val poll = it.toObject(Poll::class.java)
                    if (poll.target.contains(user.batch) && poll.target.contains(user.passingYear) && poll.target.contains(
                            user.gender
                        )
                    )
                        polls.add(poll)
                }
                polls.sortByDescending { it.comp }
                onSuccess(polls)
            }
    }

    fun getDayParticulars(context: Context, day: Int, onSuccess: (List<Particulars>) -> Unit) {

        Mess(context).getMainMenu {
            val list = it.menu[day].particulars
            onSuccess(list)
        }
//        viewModelScope.launch {
//            val list = menuDao.getMenu().menu[day].particulars
//            onSuccess(list)
//
//        }
    }

    fun selectOption(pid: String, optionSelected: OptionSelected) {

        firestoreReference.collection(POLL_RESULT).document(pid).collection(USERS).document(uid)
            .set(optionSelected)
    }

    fun getVotesOnOption(pid: String, option: String, onResult: (Int) -> Unit) {
        firestoreReference.collection(POLL_RESULT).document(pid).collection(USERS).whereEqualTo(
            SELECTED_OPTION, option
        ).addSnapshotListener { value, error ->
            if (error != null) {
                onResult(0)
                return@addSnapshotListener
            }
            val votes = value?.size()
            onResult(votes!!)
        }
    }

    fun getVoteByUid(pid: String, onResult: (String) -> Unit) {
        firestoreReference.collection(POLL_RESULT).document(pid).collection(USERS)
            .whereEqualTo("user.$UID", uid).addSnapshotListener { value, error ->
            if (error != null) {
                onResult("")
                return@addSnapshotListener
            }
            try {
                val option =
                    value?.documents?.get(0)?.toObject(OptionSelected::class.java)?.selected
                onResult(option!!)
            } catch (e: Exception) {
                onResult("")
            }
        }
    }

    fun getTotalVotes(pid: String, onResult: (Int) -> Unit) {
        firestoreReference.collection(POLL_RESULT).document(pid).collection(USERS)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(0)
                    return@addSnapshotListener
                }
                val votes = value?.size()
                onResult(votes!!)
            }
    }

    fun getDayMsgs(user: User, date: String, onResult: (List<Msg>) -> Unit) {
        firestoreReference.collection(MESSAGES).orderBy(COMPARER, DESCENDING_ORDER)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(listOf())
                    return@addSnapshotListener
                }
                val msgs = mutableListOf<Msg>()
                value?.documents?.forEach {
                    val msg = it.toObject(Msg::class.java)

                    val check =
                        msg?.target?.contains(user.batch) == true && msg.target.contains(user.passingYear) && msg.target.contains(
                            user.gender
                        )
                    if (msg != null) {
                        if (msg.date == date && check)
                            msgs.add(msg)
                    }
                }
                onResult(msgs)
            }
    }

    fun getComments(id: String, onResult: (List<Comment>) -> Unit) {
        val ref = firestoreReference.collection(MESSAGES).document(id).collection(COMMENTS).orderBy(
            COMPARER,
            DESCENDING_ORDER
        )

        ref.addSnapshotListener { value, error ->
            if (error != null) {
                onResult(listOf())
                return@addSnapshotListener
            }
            val comments = mutableListOf<Comment>()
            for (document in value?.documents!!) {
                val comment = document.toObject(Comment::class.java)
                if (comment != null) {
                    comments.add(comment)
                }
            }
            onResult(comments)

        }

    }
}
