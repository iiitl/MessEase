package com.theayushyadav11.MessEase.ui.splash.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.theayushyadav11.MessEase.R
import com.theayushyadav11.MessEase.databinding.FragmentSignUpBinding
import com.theayushyadav11.MessEase.utils.Mess


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var mess: Mess
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        animate()
        listeners()


    }

    private fun initialise() {
        auth = FirebaseAuth.getInstance()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        mess=Mess(requireContext())
    }

    fun animate() {
        val imageView: ImageView = binding.imageView
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom)
        imageView.startAnimation(animation)
    }

    fun listeners() {
        binding.verify.setOnClickListener {
            email = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString().trim()
            if(email.isNotEmpty()&&password.isNotEmpty())
            {
                registerUser(email, password)
            }
            else
            {
                mess.toast("Feilds cannot be Empty!")
            }


        }
        binding.tvSignUp.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun registerUser(email: String, password: String) {
        mess.addPb("Registering...")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    mess.pbDismiss()
                    sendVerificationEmail()

                } else {
                    mess.toast(task.exception?.message!!)
                    mess.pbDismiss()
                }
            }
    }

    private fun sendVerificationEmail() {
        mess.addPb("Sending verification email...")
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    mess.pbDismiss()
                    mess.toast("Verification email sent to ${user.email}")
                    auth.signOut()
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                } else {
                    mess.pbDismiss()
                    auth.signOut()
                    mess.toast(task.exception?.message!!)

                }
            }
    }

}