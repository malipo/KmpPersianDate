package io.github.malipo.kmppersiandate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform