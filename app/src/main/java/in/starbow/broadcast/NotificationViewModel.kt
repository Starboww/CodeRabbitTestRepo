package `in`.starbow.broadcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notificationState = MutableLiveData<NotificationState>()
    val notificationState: LiveData<NotificationState> get() = _notificationState

    fun sendNotification(notification: NotificationData, topic: String) {
        _notificationState.value = NotificationState.Loading
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    notificationRepository.postNotification(PushNotification(notification, topic))
                }
                if (response.isSuccessful) {
                    _notificationState.value = NotificationState.Success
                } else {
                    _notificationState.value = NotificationState.Error(response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _notificationState.value = NotificationState.Error(e.message ?: "Unknown exception")
            }
        }
    }
}
