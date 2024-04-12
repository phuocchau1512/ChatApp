package com.example.chatapp.fragment


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.Utils
import com.example.chatapp.databinding.FragmentSettingBinding
import com.example.chatapp.mvvm.ChatAppViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var bitmap: Bitmap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_setting,container,false)
        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        uri = createImageUri()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel!!.imageUrl.observe(viewLifecycleOwner){
            loadImage(it)
        }

        binding.settingBackBtn.setOnClickListener{
            val action = SettingFragmentDirections.actionSettingFragmentToHomeFragment()
            val navController = view.findNavController()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            navController.navigate(action, navOptions)
        }

        binding.settingUpdateImage.setOnClickListener{

            val options = arrayOf<CharSequence>("Take Photo","Choose from Gallery","Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) {dialog,item ->
                when {
                    options[item] == "Take Photo" ->{
                        onClickRequestCameraPermission(binding.settingUpdateImage)
                    }
                    options[item] == "Choose from Gallery" ->{
                        onClickRequestGalleryPermission(binding.settingUpdateImage)
                    }
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }

        binding.settingUpdateButton.setOnClickListener{
            viewModel.updateProfile()
//            view.findNavController().popBackStack(R.id.homeFragment,false)
//            view.findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
        }

    }

    private val requestGalleryPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                pickImageFromGallery()
            } else {
                Log.i("Permission: ", "Denied")
                Toast.makeText(requireContext(),"Photo and Video permission is denied",Toast.LENGTH_SHORT).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onClickRequestGalleryPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImageFromGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) -> {
                showSnackbar(
                    view,
                    getString(R.string.permission_photo_image_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.allow)
                ) {
                    requestGalleryPermissionLauncher.launch(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                }
            }

            else -> {
                requestGalleryPermissionLauncher.launch(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            val imageBitmap = getBitmapFromUri(requireContext(),uri)
            uploadImageToFirebaseStorage(imageBitmap)
        } else {
            Toast.makeText(context,"there no picture is choose",Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageFromGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private lateinit var uri: Uri

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){result ->
        if ( result ){
            val imageBitmap = getBitmapFromUri(requireContext(), uri)
            uploadImageToFirebaseStorage(imageBitmap)
        }
    }

    private fun createImageUri():Uri{
        val image = File(requireActivity().filesDir,"camera_photos.png")
        return FileProvider.getUriForFile(requireContext(),
            "com.coding.chatapp.FileProvider",
            image)
    }

    private fun takePhotoWithCamera() {
        contract.launch(uri)
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                takePhotoWithCamera()
            } else {
                Log.i("Permission: ", "Denied")
                Toast.makeText(requireContext(),"Camera permission is denied",Toast.LENGTH_SHORT).show()
            }
        }

    private fun onClickRequestCameraPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePhotoWithCamera()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                showSnackbar(
                    view,
                    getString(R.string.permission_camera_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.allow)
                ) {
                    requestCameraPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }

            else -> {
                requestCameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


    private fun loadImage(imageUrl: String?) {
        Glide.with(requireActivity()).load(imageUrl).placeholder(R.drawable.person).dontAnimate().into(binding.settingUpdateImage)
    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {
        // Kiểm tra bitmap truyền vào
        if (imageBitmap == null) {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        binding.settingUpdateImage.setImageBitmap(imageBitmap)

        // Đường dẫn cố định cho hình ảnh của người dùng dựa trên ID người dùng
        val storagePath = storageRef.child("images/${Utils.getUiLoggedIn()}/profile_picture.jpg")

        // Chuyển đổi bitmap thành mảng byte
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Xóa hình ảnh cũ trước khi tải lên hình ảnh mới
        storagePath.delete().addOnSuccessListener {
            // Xóa hình ảnh cũ thành công, tiến hành tải lên hình ảnh mới
            storagePath.putBytes(data)
                .addOnSuccessListener { taskSnapshot ->
                    // Tải lên hình ảnh mới thành công, lấy URL của hình ảnh mới
                    taskSnapshot.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener { imgUri ->
                            uri = imgUri
                            viewModel.imageUrl.value = uri.toString()
                            Toast.makeText(context, "Image uploaded and updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    // Xử lý lỗi tải lên
                    loadImage(viewModel.imageUrl.value)
                    Toast.makeText(context, "Failed to upload new image!", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            // Xử lý lỗi khi xóa hình ảnh cũ
            Toast.makeText(context, "Failed to delete old image!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getBitmapFromUri(context: Context,uri: Uri):Bitmap? {
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return bitmap
    }

    private fun Fragment.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(requireView())
            }.show()
        } else {
            snackbar.show()
        }
    }



}