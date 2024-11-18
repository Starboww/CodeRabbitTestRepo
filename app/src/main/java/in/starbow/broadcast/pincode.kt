package `in`.starbow.broadcast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PincodeActivity : AppCompatActivity() {

    private lateinit var viewModel: PincodeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pincode2)

        // ViewModel initialization
        viewModel = ViewModelProvider(this).get(PincodeViewModel::class.java)

        val pinInput = findViewById<TextInputEditText>(R.id.pincode)
        val nextBtn = findViewById<FloatingActionButton>(R.id.doneBtn)

        // Observe ViewModel state
        setupObservers()

        nextBtn.setOnClickListener {
            val pinStr = pinInput.text.toString()
            if (pinStr.isNotBlank() && viewModel.validatePincode(pinStr)) {
                navigateToMainActivity(pinStr)
            } else {
                Toast.makeText(this, "Invalid pincode!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.pincodeValidationState.observe(this) { isValid ->
            if (!isValid) {

