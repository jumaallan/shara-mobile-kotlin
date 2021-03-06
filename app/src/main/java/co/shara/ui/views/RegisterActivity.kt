package co.shara.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.shara.R
import co.shara.data.retrofit.UserRegister
import co.shara.network.NetworkResult
import co.shara.settings.Settings
import co.shara.ui.viewmodel.UserViewModel
import co.shara.util.Util
import co.shara.util.makeProgressDialog
import co.shara.util.makeSnackbar
import co.shara.util.makeStatusBarTransparent
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModel()
    private val settings: Settings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_register)

        makeStatusBarTransparent()

        textViewSignIn.text = Util.setUnderlinedSpan(
            this,
            getString(R.string.don_t_have_an_account_sign_in),
            getString(R.string.don_t_have_an_account_sign_in_underlined)
        )

    }

    fun validateUser(view: View) {

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val password = etPin.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }

        if (phoneNumber.isEmpty()) {
            etPhoneNumber.error = "Phone number is required"
            etPhoneNumber.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPin.error = "Password is required"
            etPin.requestFocus()
            return
        }

        registerUser(name, email, phoneNumber, password)
    }

    private fun registerUser(name: String, email: String, phoneNumber: String, password: String) {

        val progressDialog = makeProgressDialog("Signing In...")
        progressDialog.show()

        lifecycleScope.launch {
            when (val loginResult =
                userViewModel.registerUser(UserRegister(name, phoneNumber, email, password))) {
                is NetworkResult.Success -> {
                    progressDialog.dismiss()
                    // update the shared pref here
                    settings.setIsLoggedIn(true)

                    val intent = Intent(applicationContext, OrderActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is NetworkResult.ServerError -> {
                    progressDialog.dismiss()
                    makeSnackbar(loginResult.errorBody?.message ?: "Failed to register user.")
                }
                NetworkResult.NetworkError -> {
                    progressDialog.dismiss()
                    makeSnackbar("A network error occurred. Please try again later.")
                }
                NetworkResult.Loading -> {
                    progressDialog.show()
                }
            }
        }

    }

    fun login(view: View) {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

}
