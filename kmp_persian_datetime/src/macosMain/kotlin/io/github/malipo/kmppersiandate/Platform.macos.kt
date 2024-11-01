package io.github.malipo.kmppersiandate

class MacOsPlatform : Platform {
    override val name: String = "MacOs platform"
}

actual fun getPlatform(): Platform = MacOsPlatform()