package com.theayushyadav11.MessEase.ui.splash.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.theayushyadav11.MessEase.MainActivity
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentLoginBinding
import com.theayushyadav11.MessEase.ui.splash.ViewModels.LoginViewModel
import com.theayushyadav11.MessEase.utils.Constants.Companion.auth
import com.theayushyadav11.MessEase.utils.Mess

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var mess: Mess

    companion object {
        const val RC_SIGN_IN = 9001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
        animate()
        listeners()
        onBackPressed()

    }

    fun initialise() {
        mess = Mess(requireContext())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

    }

    fun listeners() {
        binding.btngsi.setOnClickListener {
            signIn()
        }
        binding.btnLogin.setOnClickListener {
            mess.addPb("Logging in...")
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            loginUser(email, password)
        }
        binding.tvForgotPassword.setOnClickListener {
            forgotPassword()
        }
        binding.tvSignUp.setOnClickListener {
            signUp()
        }
    }


    private fun signIn() {

        mess.addPb("Loading...")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mess.addPb("Signing in...")

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val email = account.email!!
                mess.isValidEmail(email) {
                    if (it) {
                        firebaseAuthWithGoogle(account)
                    } else {
                        mess.pbDismiss()
                        googleSignInClient.signOut()
                        mess.toast("Login with college email id only")
                    }
                }

            } catch (e: ApiException) {
                mess.pbDismiss()
                mess.toast("Google sign in failed: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigate()
                } else {
                    mess.toast("Authentication Failed.")
                }
            }
    }


    private fun forgotPassword() {
        val email = binding.etEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            mess.isValidEmail(email) {
                if (it) {
                    mess.addPb("Sending Password reset email...")
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            mess.pbDismiss()
                            if (task.isSuccessful) {
                                mess.toast("Password reset email sent")

                            } else {
                                mess.toast(task.exception?.message.toString())
                            }
                        }
                } else {
                    mess.toast("Login with college email id only")
                    mess.pbDismiss()

                }
            }
        } else {
            Snackbar.make(binding.root, "Email cannot be empty!", Snackbar.LENGTH_LONG).show()
            mess.pbDismiss()
        }
    }

    private fun animate() {
        val imageView: ImageView = binding.imageView
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom)
        imageView.startAnimation(animation)
    }

    private fun signUp() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

    private fun loginUser(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mess.isValidEmail(email) {
                if (it) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener() { task ->
                            mess.pbDismiss()
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user?.isEmailVerified!!) {
                                    navigate()
                                } else {
                                    mess.toast("Please verify your email address")
                                }
                            } else {
                                mess.toast(task.exception?.message.toString())
                            }
                        }
                } else {
                    mess.toast("Login with college email id only")
                    mess.pbDismiss()
                }
            }
        } else {
            Snackbar.make(binding.root, "Email or password cannot be empty!", Snackbar.LENGTH_LONG)
                .show()
            mess.pbDismiss()
        }

    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun navigate() {
        viewModel.isPresent { isPresent ->
            if (isPresent) {
                mess.pbDismiss()
                mess.toast("Authentication Successful.")
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                mess.pbDismiss()
                mess.toast("Please complete your profile")
                findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)

            }
        }
    }
}