package io.github.darriousliu.katex.freetype

/**
 * This is a simple class wich contains the version information about FreeType.
 */
class LibraryVersion(
    val major: Int,
    val minor: Int, // Example: 2.6.0
    val patch: Int
) {
    override fun toString(): String {
        return "$major.$minor.$patch"
    }
}