package event

class OnRequestPermissionResultEvent(var requestCode: Int, var permissions: Array<String>?, var grantResults: IntArray?)

