package com.basic.newsapp.utils

sealed class NetworkResponseStatus<T> (val data: T? = null,
                                       val message: String? = null){

    class Success<T>(data: T) : NetworkResponseStatus<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResponseStatus<T>(data, message)
    class Loading<T> : NetworkResponseStatus<T>()
}
