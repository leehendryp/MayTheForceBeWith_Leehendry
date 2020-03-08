package com.leehendryp.maytheforcebewithleehendry.feed.data

class CouldNotFetchPeopleError(message: String, error: Throwable) : Throwable(message, error)
class CouldNotFetchCharacterError(message: String, error: Throwable) : Throwable(message, error)
class CouldNotSearchCharacterError(message: String, error: Throwable) : Throwable(message, error)
class CouldNotSavePeopleError(message: String, error: Throwable) : Throwable(message, error)