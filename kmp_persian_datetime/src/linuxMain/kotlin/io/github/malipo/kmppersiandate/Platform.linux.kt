package io.github.malipo.kmppersiandate

class LinuxPlatform : Platform {
    override val name: String = "Linux platform"
}

actual fun getPlatform(): Platform = LinuxPlatform()