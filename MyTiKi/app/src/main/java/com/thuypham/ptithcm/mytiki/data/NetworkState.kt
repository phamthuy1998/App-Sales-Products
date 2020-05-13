package com.thuypham.ptithcm.mytiki.data

enum class Status {
    RUNNING,
    SUCCESS,
    LOADING_PROCESS,
    FAILED
}

data class NetworkState constructor(
    val status: Status,
    val msg: String? = null,
    val process: Double? = null
) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun loadingProcess(process: Double) = NetworkState(
            Status.LOADING_PROCESS,
            process = process
        )

        fun error(msg: String?) = NetworkState(
            Status.FAILED,
            msg = msg
        )
    }
}