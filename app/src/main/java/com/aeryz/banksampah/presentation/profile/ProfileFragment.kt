package com.aeryz.banksampah.presentation.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.aeryz.banksampah.R
import com.aeryz.banksampah.databinding.FragmentProfileBinding
import com.aeryz.banksampah.presentation.login.LoginActivity
import com.aeryz.banksampah.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModel()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                changePhotoProfile(uri)
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForm()
        setClickListeners()
        showUserData()
        observeData()
    }

    private fun observeData() {
        viewModel.changePhotoResult.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(requireContext(), "Change Photo Profile Success !", Toast.LENGTH_SHORT).show()
                    showUserData()
                },
                doOnError = {
                    Toast.makeText(requireContext(), "Change Photo Profile Failed !", Toast.LENGTH_SHORT).show()
                    showUserData()
                }
            )
        }
        viewModel.changeProfileResult.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    Toast.makeText(requireContext(), "Change Profile Data Success !", Toast.LENGTH_SHORT).show()
                },
                doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    Toast.makeText(requireContext(), "Change Profile Data Failed !", Toast.LENGTH_SHORT).show()
                },
                doOnLoading = {
                    binding.pbLoading.isVisible = true
                    binding.btnChangeProfile.isVisible = false
                }
            )
        }
    }

    private fun setupForm() {
        viewModel.isEditModeEnabled.observe(viewLifecycleOwner) { isEditModeEnabled ->
            if (isEditModeEnabled) {
                binding.ivEdit.load(R.drawable.ic_edit_active)
                binding.ivEditPhoto.isEnabled = true
                binding.layoutForm.tilName.isVisible = true
                binding.layoutForm.etName.isEnabled = true
                binding.layoutForm.tilEmail.isVisible = true
                binding.layoutForm.etEmail.isEnabled = false
                binding.tilUserAddress.isVisible = true
                binding.etUserAddress.isEnabled = true
                binding.btnChangeProfile.isEnabled = true
            } else {
                binding.ivEdit.load(R.drawable.ic_edit)
                binding.ivEditPhoto.isEnabled = false
                binding.layoutForm.tilName.isVisible = true
                binding.layoutForm.etName.isEnabled = false
                binding.layoutForm.tilEmail.isVisible = true
                binding.layoutForm.etEmail.isEnabled = false
                binding.tilUserAddress.isVisible = true
                binding.etUserAddress.isEnabled = false
                binding.btnChangeProfile.isEnabled = false
            }
        }
    }

    private fun setClickListeners() {
        binding.btnChangeProfile.setOnClickListener {
            if (checkNameValidation()) {
                changeProfileData()
            }
            val userAddress = binding.etUserAddress.text.toString()
            viewModel.setUserAddress(userAddress)
        }
        binding.ivEditPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.tvChangePwd.setOnClickListener {
            requestChangePassword()
        }
        binding.tvLogout.setOnClickListener {
            doLogout()
        }
        binding.ivEdit.setOnClickListener {
            viewModel.toggleEditMode()
        }
    }

    private fun showUserData() {
        viewModel.getCurrentUser()?.let {
            binding.layoutForm.etName.setText(it.fullName)
            binding.layoutForm.etEmail.setText(it.email)
            binding.ivProfileImage.load(it.photoUrl) {
                crossfade(true)
                placeholder(R.drawable.iv_profile_picture)
                error(R.drawable.iv_profile_picture)
                transformations(CircleCropTransformation())
            }
        }
        viewModel.userAddress.observe(viewLifecycleOwner){ address ->
            binding.etUserAddress.setText(address)
        }
    }

    private fun changeProfileData() {
        val fullName = binding.layoutForm.etName.text.toString().trim()
        viewModel.updateFullName(fullName)
    }

    private fun changePhotoProfile(uri: Uri) {
        viewModel.updateProfilePicture(uri)
    }

    private fun checkNameValidation(): Boolean {
        val fullName = binding.layoutForm.etName.text.toString().trim()
        return if (fullName.isEmpty()) {
            binding.layoutForm.tilName.isErrorEnabled = true
            binding.layoutForm.tilName.error = getString(R.string.text_error_name_empty)
            false
        } else {
            binding.layoutForm.tilName.isErrorEnabled = false
            true
        }
    }

    private fun requestChangePassword() {
        viewModel.createChangePwdRequest()
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("Change password request sended to your email : ${viewModel.getCurrentUser()?.email} Please check to your inbox or spam")
            .setPositiveButton(
                "Okay"
            ) { _, _ ->
            }.create()
        dialog.show()
    }

    private fun doLogout() {
        val dialog = AlertDialog.Builder(requireContext()).setMessage("Do you want to logout ?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                viewModel.doLogout()
                navigateToLogin()
            }
            .setNegativeButton(
                "No"
            ) { _, _ ->
                // no-op , do nothing
            }.create()
        dialog.show()
    }

    private fun navigateToLogin() {
        context?.startActivity(Intent(requireContext(), LoginActivity::class.java))
    }
}
