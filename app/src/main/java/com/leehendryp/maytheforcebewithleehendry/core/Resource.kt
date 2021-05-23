package com.leehendryp.maytheforcebewithleehendry.core

sealed class Resource<SuccessType, ErrorType> {
    data class Success<SuccessType, Nothing>(val data: SuccessType) :
        Resource<SuccessType, Nothing>()

    data class Error<Nothing, ErrorType>(val error: ErrorType) :
        Resource<Nothing, ErrorType>()
}