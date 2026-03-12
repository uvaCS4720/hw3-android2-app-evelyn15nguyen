package edu.nd.pmcburne.hwapp.one.model

// Gender used to filter API endpoint
enum class Gender(val apiValue: String, val displayName: String) {
    MEN("men", "Men"),
    WOMEN("women", "Women")
}