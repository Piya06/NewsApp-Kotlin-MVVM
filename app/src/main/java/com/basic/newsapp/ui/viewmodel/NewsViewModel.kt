package com.basic.newsapp.ui.viewmodel

import com.basic.newsapp.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.basic.newsapp.R
import com.basic.newsapp.models.NewsResponse
import com.basic.newsapp.utils.NetworkResponseStatus
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    application: Application
) : AndroidViewModel(application) {

    val mNews: MutableLiveData<NetworkResponseStatus<NewsResponse>> = MutableLiveData()
    var mNewsPage = 1
    var mNewsResponse: NewsResponse? = null


    init {
        getNews(getApplication<Application>().getString(R.string.us))
    }

    fun getNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): NetworkResponseStatus<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                resultResponse.articles.forEach { article ->
                    article.publishedAt = formatDate(article.publishedAt)
                }

                mNewsPage++
                if (mNewsResponse == null) {
                    mNewsResponse = resultResponse
                }
                return NetworkResponseStatus.Success(mNewsResponse ?: resultResponse)
            }
        }
        return NetworkResponseStatus.Error(response.message())
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        mNews.postValue(NetworkResponseStatus.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, mNewsPage)
                mNews.postValue(handleBreakingNewsResponse(response))
            } else {
                mNews.postValue(
                    NetworkResponseStatus.Error(
                        getApplication<Application>().getString(
                            R.string.no_internet_connection
                        )
                    )
                )
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> mNews.postValue(
                    NetworkResponseStatus.Error(
                        getApplication<Application>().getString(
                            R.string.network_failure
                        )
                    )
                )

                else -> mNews.postValue(
                    NetworkResponseStatus.Error(
                        getApplication<Application>().getString(
                            R.string.conversion_error
                        )
                    )
                )
            }
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }


    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return getApplication<Application>().getString(R.string.unknown_date)

        return try {
            val inputFormat = SimpleDateFormat(
                getApplication<Application>().getString(R.string.yyyy_mm_dd_t_hh_mm_ss_z),
                Locale.getDefault()
            )
            inputFormat.timeZone =
                TimeZone.getTimeZone(getApplication<Application>().getString(R.string.utc))

            val outputFormat = SimpleDateFormat(
                getApplication<Application>().getString(R.string.dd_mmm_yyyy),
                Locale.getDefault()
            )

            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) }
                ?: getApplication<Application>().getString(R.string.unknown_date)
        } catch (e: Exception) {
            getApplication<Application>().getString(R.string.invalid_date)
        }
    }
}
