package io.github.malipo.kmppersiandate

class JSPlatform : Platform {
    override val name: String = "JS platform"
}

actual fun getPlatform(): Platform = JSPlatform()