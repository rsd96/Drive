package com.rsd96.drive

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user.*
import java.io.IOException
import java.util.*

/**
 * Created by Ramshad on 12/18/17.
 */

class UserActivity : AppCompatActivity() {


    companion object {
        private val TAG = "UserActivity"
    }

    lateinit var reLayoutManager: LinearLayoutManager
    lateinit var reAdapter : RecyclerView.Adapter<CarsRecycleAdapter.ViewHolder>
    var nameList = ArrayList<String>()
    var plateList = ArrayList<String>()
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    var storage: FirebaseStorage? = FirebaseStorage.getInstance()
    var storageReference: StorageReference? = storage?.reference
    val ref = storageReference?.child("profiles/${user?.uid}_profile.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        iv_user_profile.bringToFront()

        reLayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

        ref?.downloadUrl?.addOnSuccessListener { uri ->
            Log.d(TAG, "URI :  ${uri.toString()}")
            if (iv_user_profile != null)
                Picasso.with(applicationContext).load(uri.toString()).into(iv_user_profile)
        }

        database.child("users").child(user?.uid).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot?) {
                tvUserName.text = snap?.value.toString()
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        tvUserEmail.text = user?.email
        rv_user_cars.layoutManager = reLayoutManager
        rv_user_cars.setHasFixedSize(true)
        reAdapter = CarsRecycleAdapter(this, plateList, nameList)


        // Load Cars
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            Log.d(TAG, "auth state changed !")
            if (firebaseAuth.currentUser != null) {
                Log.d(TAG, firebaseAuth.currentUser?.uid)
                user = firebaseAuth.currentUser

                database.child("users").child(user?.uid).child("cars").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot?) {
                        plateList.clear()
                        nameList.clear()
                        if (snapshot != null) {
                            for (x in snapshot.children) {
                                plateList.add(x.key.toString())
                                nameList.add(x.child("car_name").value.toString())
                            }
                            if (rv_user_cars != null) {
                                rv_user_cars.adapter = reAdapter
                                reAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onCancelled(snapshot: DatabaseError?) {

                    }
                })
            }
        }


        // Add cars
        btn_user_add_cars.setOnClickListener({v ->
            var builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.myDialog))
            var inflater = layoutInflater
            var dialogLayout = inflater.inflate(R.layout.dialog_add_cars, null)
            builder.setView(dialogLayout)
            builder.setPositiveButton("ADD", { dialogInterface, i ->

                var etCarName = dialogLayout.findViewById<EditText>(R.id.et_add_car_name) as EditText
                var etCarPlate = dialogLayout.findViewById<EditText>(R.id.et_add_car_plate) as EditText

                var plate = etCarPlate.text.trim().toString()
                var name = etCarName.text.toString()

                database.child("users").child(user?.uid).child("cars").child("$plate").child("car_name").setValue(name)
            })
            builder.setNegativeButton("CANCEL", null)
            builder.create().show()
        })


        // Logout
        btn_user_logout.setOnClickListener({ view ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        })

        btn_user_upload.setOnClickListener({view ->
            chooseImage()
        })

    }


    //Initialize Views
    private fun uploadImage() {

        if (filePath != null) {
            Log.d(TAG, "uploading... ")

            // Get the data from an ImageView as bytes
//            iv_user_profile.isDrawingCacheEnabled = true
//            iv_user_profile.buildDrawingCache()
//            val bitmap = iv_user_profile.drawingCache
//            val baos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            val data = baos.toByteArray()

            val uploadTask = ref?.putFile(filePath!!)
            uploadTask?.addOnFailureListener({
                Toast.makeText(applicationContext, "Failed to upload image!", Toast.LENGTH_SHORT).show()
            })?.addOnSuccessListener({ taskSnapshot ->
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                val downloadUrl = taskSnapshot.downloadUrl
                Toast.makeText(applicationContext, "Image uploaded!", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, filePath)
                iv_user_profile.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}