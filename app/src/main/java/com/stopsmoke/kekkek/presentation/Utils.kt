package com.stopsmoke.kekkek.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.stopsmoke.kekkek.R
import com.stopsmoke.kekkek.core.domain.model.DateTimeUnit
import com.stopsmoke.kekkek.core.domain.model.ElapsedDateTime
import com.stopsmoke.kekkek.core.domain.model.PostCategory
import com.stopsmoke.kekkek.core.domain.model.PostWriteCategory
import com.stopsmoke.kekkek.core.domain.model.ProfileImage
import com.stopsmoke.kekkek.core.domain.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun FragmentActivity.visible() {
    (this as? MainActivity)?.let {
        val layout = findViewById<ConstraintLayout>(R.id.bottom_navigation)
        layout.visibility = View.VISIBLE
    }
}

fun FragmentActivity.invisible() {
    (this as? MainActivity)?.let {
        val layout = findViewById<ConstraintLayout>(R.id.bottom_navigation)
        layout.visibility = View.GONE
    }
}

fun FragmentActivity.isVisible(): Boolean {
    val layout = findViewById<ConstraintLayout>(R.id.bottom_navigation)
    return View.VISIBLE == layout.visibility
}

fun getRelativeTime(pastTime: ElapsedDateTime): String {
    val timeType = when (pastTime.elapsedDateTime) {
        DateTimeUnit.YEAR -> "년"
        DateTimeUnit.MONTH -> "달"
        DateTimeUnit.DAY -> "일"
        DateTimeUnit.WEEK -> "주"
        DateTimeUnit.HOUR -> "시간"
        DateTimeUnit.MINUTE -> "분"
        DateTimeUnit.SECOND -> return "방금 전"
    }
    return "${pastTime.number} ${timeType} 전"
}

fun convertSpannableToHtml(spannable: Spannable): String {
    return Html.toHtml(spannable, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
}

fun convertHtmlToSpannable(html: String): Spannable {
    return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT) as Spannable
}

fun<T> List<T>.toggleElement(value: T): List<T> {
    if (contains(value)) {
        return this.toMutableList().apply {
            remove(value)
        }
    }
    return this.toMutableList().apply {
        add(value)
    }
}

fun<T> Set<T>.toggleElement(value: T): Set<T> {
    if (contains(value)) {
        return this.toMutableSet().apply {
            remove(value)
        }
    }
    return this.toMutableSet().apply {
        add(value)
    }
}

internal fun ElapsedDateTime.toResourceId(context: Context): String =
    when (elapsedDateTime) {
        DateTimeUnit.YEAR -> "${number}${context.getString(R.string.notification_elapsed_year)}"
        DateTimeUnit.MONTH -> "${number}${context.getString(R.string.notification_elapsed_month)}"
        DateTimeUnit.WEEK -> "${number}${context.getString(R.string.notification_elapsed_week)}"
        DateTimeUnit.DAY -> "${number}${context.getString(R.string.notification_elapsed_day)}"
        DateTimeUnit.HOUR -> "${number}${context.getString(R.string.notification_elapsed_hour)}"
        DateTimeUnit.MINUTE -> "${number}${context.getString(R.string.notification_elapsed_minute)}"
        DateTimeUnit.SECOND -> "방금 전"
    }


internal fun<T> Flow<T>.collectLatest(
    lifecycleScope: LifecycleCoroutineScope,
    action: (value: T) -> Unit
): Job {
    return lifecycleScope.launch {
        this@collectLatest.collectLatest(action)
    }
}

internal fun<T> Flow<T>.collectLatestWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (value: T) -> Unit
): Job {
    return lifecycle.coroutineScope.launch {
        this@collectLatestWithLifecycle
            .flowWithLifecycle(lifecycle = lifecycle, minActiveState = minActiveState)
            .collectLatest(action)
    }
}

internal fun<T> Bundle.getParcelableAndroidVersionSupport(key: String, clazz: Class<T>) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        getParcelable(key)
    }

internal fun PostCategory.getResourceString(context: Context) = when(this) {
    PostCategory.NOTICE -> context.getString(R.string.community_category_notice)
    PostCategory.QUIT_SMOKING_AIDS_REVIEWS -> context.getString(R.string.community_category_quit_smoking_aids_reviews)
    PostCategory.SUCCESS_STORIES -> context.getString(R.string.community_category_success_stories)
    PostCategory.GENERAL_DISCUSSION -> context.getString(R.string.community_category_general_discussion)
    PostCategory.RESOLUTIONS -> context.getString(R.string.community_category_resolutions)
    PostCategory.UNKNOWN -> context.getString(R.string.community_category_unknown)
    PostCategory.ALL -> context.getString(R.string.community_category_all)
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

internal fun View.snackbarLongShow(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

internal fun ImageView.setDefaultProfileImage(profileImage: ProfileImage) {
    when(profileImage) {
        ProfileImage.Default -> setImageResource(R.drawable.ic_user_profile_test)
        is ProfileImage.Web -> load(profileImage.url)
    }
}


fun (User.Registered).getTotalDay(): Long{
    var totalDay:Long = 0
    this.history.historyTimeList.forEach {
        if(it.quitSmokingStartDateTime !=null){
            if(it.quitSmokingStopDateTime != null)  totalDay += ChronoUnit.DAYS.between(it.quitSmokingStartDateTime, it.quitSmokingStopDateTime)
            else if(it.quitSmokingStopDateTime == null) totalDay += ChronoUnit.DAYS.between(it.quitSmokingStartDateTime, LocalDateTime.now())
        }
    }
    return totalDay
}

internal fun View.hideSoftKeyboard() {
    val inputMethodManager = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
}

internal fun<T> NavController.putNavigationResult(key: String, value: T) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun PostCategory.toRequestString(): String? = when (this) {
    PostCategory.NOTICE -> "notice"
    PostCategory.QUIT_SMOKING_AIDS_REVIEWS -> "quit_smoking_aids_reviews"
    PostCategory.SUCCESS_STORIES -> "success_stories"
    PostCategory.GENERAL_DISCUSSION -> "general_discussion"
    PostCategory.RESOLUTIONS -> "resolutions"
    PostCategory.UNKNOWN -> null
    PostCategory.ALL -> null
}

fun PostCategory.toStringKR(): String? = when (this) {
    PostCategory.NOTICE -> "공지사항"
    PostCategory.QUIT_SMOKING_AIDS_REVIEWS -> "금연 보조제 후기"
    PostCategory.SUCCESS_STORIES -> "금연 성공 후기"
    PostCategory.GENERAL_DISCUSSION -> "자유 게시판"
    PostCategory.RESOLUTIONS -> "금연 다짐"
    PostCategory.UNKNOWN -> null
    PostCategory.ALL -> "커뮤니티 홈"
}

fun String.toPostCategory(): PostCategory = when (this) {
    "notice", "공지사항" -> PostCategory.NOTICE
    "quit_smoking_aids_reviews", "금연 보조제 후기" -> PostCategory.QUIT_SMOKING_AIDS_REVIEWS
    "success_stories", "금연 성공 후기" -> PostCategory.SUCCESS_STORIES
    "general_discussion", "자유 게시판" -> PostCategory.GENERAL_DISCUSSION
    "resolutions", "금연 다짐" -> PostCategory.RESOLUTIONS
    "all", "커뮤니티 홈" -> PostCategory.ALL
    else -> PostCategory.UNKNOWN
}

fun PostWriteCategory.toRequestString(): String = when(this) {
    PostWriteCategory.QUIT_SMOKING_AIDS_REVIEWS -> "quit_smoking_aids_reviews"
    PostWriteCategory.SUCCESS_STORIES -> "success_stories"
    PostWriteCategory.GENERAL_DISCUSSION -> "general_discussion"
    PostWriteCategory.RESOLUTIONS -> "resolutions"
}

fun String.toPostWriteCategory() = when(this){
    "자유 게시판" -> PostWriteCategory.GENERAL_DISCUSSION
    "금연 성공 후기" -> PostWriteCategory.SUCCESS_STORIES
    "금연 보조제 후기" -> PostWriteCategory.QUIT_SMOKING_AIDS_REVIEWS
    "금연 다짐" -> PostWriteCategory.RESOLUTIONS
    else -> throw IllegalStateException()
}

fun PostWriteCategory.toPostCategory(): PostCategory = when(this){
    PostWriteCategory.GENERAL_DISCUSSION -> PostCategory.GENERAL_DISCUSSION
    PostWriteCategory.SUCCESS_STORIES -> PostCategory.SUCCESS_STORIES
    PostWriteCategory.QUIT_SMOKING_AIDS_REVIEWS -> PostCategory.QUIT_SMOKING_AIDS_REVIEWS
    PostWriteCategory.RESOLUTIONS -> PostCategory.RESOLUTIONS
    else -> PostCategory.UNKNOWN
}