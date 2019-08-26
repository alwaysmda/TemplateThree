package http

interface OnResponseListener {
    fun onResponse(response: Response)
    fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int)
}