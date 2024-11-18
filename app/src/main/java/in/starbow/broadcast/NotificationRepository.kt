package `in`.starbow.broadcast

import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val apiService: NotificationApiService
) {
    suspend fun postNotification(notification: PushNotification) = apiService.postNotification(notification)
}
