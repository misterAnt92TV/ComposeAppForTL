package com.tlincompose.data.local

interface StorageDriver {
    fun read(fileName: String): String?
    fun write(fileName: String, content: String)
}

class InMemoryStorageDriver : StorageDriver {
    private val files = mutableMapOf<String, String>()

    override fun read(fileName: String): String? = files[fileName]

    override fun write(fileName: String, content: String) {
        files[fileName] = content
    }
}
