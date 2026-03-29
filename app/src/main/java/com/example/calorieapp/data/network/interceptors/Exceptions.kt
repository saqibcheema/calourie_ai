package com.example.calorieapp.data.network.interceptors

import java.io.IOException

class NoConnectivityException : IOException("No internet connection available")
class RateLimitException : IOException("You are making requests too quickly. Please wait a moment.")
