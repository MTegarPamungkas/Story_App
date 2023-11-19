package com.tegar.submissionaplikasistoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tegar.submissionaplikasistoryapp.R
import com.tegar.submissionaplikasistoryapp.data.local.preference.UserDataStore
import com.tegar.submissionaplikasistoryapp.data.remote.ResultMessage
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseLogin
import com.tegar.submissionaplikasistoryapp.databinding.FragmentBottomLoginBinding
import com.tegar.submissionaplikasistoryapp.util.ToastUtils
import com.tegar.submissionaplikasistoryapp.util.hideKeyboard
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.LoginViewModel
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.AuthViewModelFactory
import com.tegar.submissionaplikasistoryapp.util.EspressoIdlingResource
import kotlinx.coroutines.launch

class LoginDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var binding: FragmentBottomLoginBinding? = null
    private val loginViewModel: LoginViewModel by viewModels {
        AuthViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var userDataStore: UserDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDataStore = UserDataStore.getInstance(requireContext())
        binding?.closeLogin?.setOnClickListener(this)
        binding?.btnLogin?.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.close_login -> dismiss()
            R.id.btn_login -> {
                val email: String = binding?.edEmailLogin?.text.toString()
                val password: String = binding?.edPasswordLogin?.text.toString()

                val isEmailValid = binding?.edEmailLogin?.error == null
                val isPasswordValid = binding?.edPasswordLogin?.error == null

                if (isEmailValid && isPasswordValid) {
                    hideKeyboard()
                    EspressoIdlingResource.increment()
                    loginViewModel.login(email, password).observe(requireActivity()) { result ->
                        handleResult(result)
                        EspressoIdlingResource.decrement()
                    }
                } else {
                    ToastUtils.showToast(requireActivity(), getString(R.string.data_invalid))
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleResult(result: ResultMessage<ResponseLogin>) {
        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }
                is ResultMessage.Success -> {
                    ToastUtils.showToast(requireActivity(), getString(R.string.login_successful))
                    val userId: String = result.data.loginResult?.userId.toString()
                    val name: String = result.data.loginResult?.name.toString()
                    val token: String = result.data.loginResult?.token.toString()
                    lifecycleScope.launch {
                        userDataStore.saveUserData(userId, name, token)
                    }
                    showLoading(false)
                    dismiss()
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            is ResultMessage.Error -> {
                val exception = result.exception
                val errorMessage = exception.message ?: getString(R.string.an_error_occurred_during_login)
                ToastUtils.showToast(requireContext(), errorMessage)
                showLoading(false)
            }
        }
    }
}
