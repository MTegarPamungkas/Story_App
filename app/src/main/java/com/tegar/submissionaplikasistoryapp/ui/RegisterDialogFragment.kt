package com.tegar.submissionaplikasistoryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tegar.submissionaplikasistoryapp.R
import com.tegar.submissionaplikasistoryapp.data.remote.ResultMessage
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseRegister
import com.tegar.submissionaplikasistoryapp.databinding.FragmentBottomRegisterBinding
import com.tegar.submissionaplikasistoryapp.util.ToastUtils.showToast
import com.tegar.submissionaplikasistoryapp.util.hideKeyboard
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.RegisterViewModel
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.AuthViewModelFactory

class RegisterDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var binding: FragmentBottomRegisterBinding? = null
    private val registerViewModel: RegisterViewModel by viewModels {
        AuthViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.closeRegister?.setOnClickListener(this)
        binding?.btnRegister?.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun handleResult(result: ResultMessage<ResponseRegister>) {
        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }
            is ResultMessage.Success -> {
                showToast(requireActivity(), getString(R.string.registration_successful))
                showLoading(false)
                dismiss()
            }
            is ResultMessage.Error -> {
                val exception = result.exception
                val errorMessage = exception.message ?: getString(R.string.an_error_occurred_while_registering)
                showLoading(false)
                showToast(requireContext(), errorMessage)
            }
        }
    }

    private fun register() {
        val name: String = binding?.edNameRegister?.text.toString()
        val email: String = binding?.edEmailRegister?.text.toString()
        val password: String = binding?.edPasswordRegister?.text.toString()

        val isNameValid = binding?.edNameRegister?.error == null
        val isEmailValid = binding?.edEmailRegister?.error == null
        val isPasswordValid = binding?.edPasswordRegister?.error == null

        if (isNameValid && isEmailValid && isPasswordValid) {
            hideKeyboard()
            registerViewModel.register(name, email, password).observe(requireActivity()) { result ->
                handleResult(result)
            }
        } else {
            Toast.makeText(requireActivity(), getString(R.string.data_invalid), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.close_register -> dismiss()
            R.id.btn_register -> register()
        }
    }
}
