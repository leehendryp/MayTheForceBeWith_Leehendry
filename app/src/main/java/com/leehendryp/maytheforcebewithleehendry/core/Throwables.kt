package com.leehendryp.maytheforcebewithleehendry.core

class ShouldNotBeAnException(message: String) : Throwable(message)
class ClientException(message: String) : Throwable(message)
class ServiceException(message: String) : Throwable(message)