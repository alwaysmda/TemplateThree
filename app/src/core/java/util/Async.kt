package util

import android.os.AsyncTask

class Async(val callback: (Any) -> Unit) : AsyncTask<() -> (Any), Int, Any>() {
    override fun doInBackground(vararg p0: (() -> Any)): Any {
        return p0[0].invoke()
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        callback(result!!)
    }
}