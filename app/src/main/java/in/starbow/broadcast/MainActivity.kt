package `in`.starbow.broadcast

import `in`.starbow.broadcast.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    private lateinit var viewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        // Get PIN from intent or fallback to default
        val pin = intent.getStringExtra("pin") ?: "default"
        val topic = "/topics/$pin"
        subscribeToTopic(topic)

        // Observe ViewModel state
        setupObservers()

        // Button click listener
        binding.btnBroadCast.setOnClickListener {
            val title = binding.edTitleTxt.text.toString()
            val message = binding.edtxtMsg.text.toString()
            if (title.isNotBlank() && message.isNotBlank()) {
                val notification = NotificationData(title, message)
                viewModel.sendNotification(notification, topic)
            } else {
                Toast.makeText(this, "Title and message cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subscribeToTopic(topic: String) {
        firebaseMessaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("MAIN", "Subscribed to $topic")
                } else {
                    Log.e("MAIN", "Failed to subscribe to topic")
                }
            }
    }

    private fun setupObservers() {
        viewModel.notificationState.observe(this) { state ->
            when (state) {
                is NotificationState.Success -> {
                    Toast.makeText(this, "Notification sent!", Toast.LENGTH_SHORT).show()
                }
                is NotificationState.Error -> {
                    Toast.makeText(this, "Failed to send notification: ${state.error}", Toast.LENGTH_LONG).show()
                }
                is NotificationState.Loading -> {
                    // Show loading indicator (optional)
                    Log.d("MAIN", "Sending notification...")
                }
            }
        }
    }
}
