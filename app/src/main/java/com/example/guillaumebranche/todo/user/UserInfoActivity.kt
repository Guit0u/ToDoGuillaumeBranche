package com.example.guillaumebranche.todo.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.guillaumebranche.todo.R
import com.example.guillaumebranche.todo.network.Api
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class UserInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        findViewById<Button>(R.id.take_picture_button).setOnClickListener {
            launchCameraWithPermission()
        }

        lifecycleScope.launch{
            val userInfo = Api.userWebService.getInfo().body()
            findViewById<ImageView>(R.id.image_view).load(userInfo?.avatar){
                error(R.drawable.ic_launcher_background)
            }
        }
    }

    private val mediaStore by lazy { MediaStoreRepository(this) }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
            val view = findViewById<Button>(R.id.take_picture_button)// n'importe quelle vue (ex: un bouton, binding.root, window.decorView, ...)
                if (accepted) handleImage(photoUri)
                else Snackbar.make(view, "Échec!", Snackbar.LENGTH_LONG).show()
        }


    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted)
                launchCameraWithPermission()// lancer l'action souhaitée
            else
                showExplanation()// afficher une explication
        }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        when {
            isAlreadyAccepted -> launchCamera()// lancer l'action souhaitée
            isExplanationNeeded -> showExplanation()// afficher une explication
            else -> cameraPermissionLauncher.launch(camPermission)// lancer la demande de permission
        }
    }

    private fun showExplanation() {
        // ici on construit une pop-up système (Dialog) pour expliquer la nécessité de la demande de permission
        AlertDialog.Builder(this)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ -> launchAppSettings()}
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    private fun handleImage(imageUri: Uri) {
        findViewById<ImageView>(R.id.image_view).setImageURI(imageUri)// afficher l'image dans l'ImageView
        lifecycleScope.launch {
            Api.userWebService.updateAvatar(convert(imageUri))
        }
    }

    private lateinit var photoUri: Uri
    private fun launchCamera() {
        lifecycleScope.launch {
            photoUri = mediaStore.createMediaUri(
                filename = "picture-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrThrow()
            cameraLauncher.launch(photoUri)
        }
    }

    private fun convert(uri: Uri): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
        )
    }
}