package `in`.starbow.broadcast

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TargetActivity : AppCompatActivity() {

    private val viewModel: TargetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

        val textTitle = findViewById<TextView>(R.id.tvTitle)
        val textDescp = findViewById<TextView>(R.id.tvDescription)

        // Extract intent extras
        val title = intent.getStringExtra("title") ?: ""
        val message = intent.getStringExtra("msg") ?: ""

        // Populate ViewModel
        viewModel.setMessage(title, message)

        // Observe ViewModel state
        viewModel.uiState.observe(this, Observer { state ->
            when (state) {
                is TargetUiState.Loading -> {
                    // Show loading spinner (if applicable)
                    Log.d("TargetActivity", "Loading content...")
                }
                is TargetUiState.Content -> {
                    textTitle.text = state.title
                    textDescp.text = state.message
                }
                is TargetUiState.Error -> {
                    Toast.makeText(this, "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
                    Log.e("TargetActivity", state.errorMessage)
                }
            }
        })

        // Log data for analytics
        Log.d("TargetActivity", "Received data - Title: $title, Message: $message")
    }
}
