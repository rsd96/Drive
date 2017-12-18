package com.rsd96.drive

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.rsd96.drive.CurrentUser.user
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit private var mAuth: FirebaseAuth
    lateinit private var authListener: FirebaseAuth.AuthStateListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val actionBar = supportActionBar
//        actionBar!!.displayOptions = actionBar.displayOptions //or supportActionBar.DISPLAY_SHOW_CUSTOM
//        val imageView = ImageView(actionBar.themedContext)
//        imageView.scaleType = ImageView.ScaleType.CENTER
//        imageView.setImageResource(R.drawable.profile_default)
//        val layoutParams = ActionBar.LayoutParams(
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT or Gravity.CENTER_VERTICAL)
//        layoutParams.rightMargin = 40
//        imageView.layoutParams = layoutParams
//        actionBar.customView = imageView

        setupViewPager()

        mAuth = FirebaseAuth.getInstance()

        mAuth.addAuthStateListener {
            Log.d(TAG, "AUTH")
            Log.d(TAG, it.currentUser?.uid ?: "no user")
            user = it.currentUser
            if(user != null) {
                var ref = FirebaseDatabase.getInstance().reference

                ref.child("users").child(FirebaseAuth.getInstance()?.uid)
                        .child("device_token").setValue(FirebaseInstanceId.getInstance().token)

                ref.child("users").child(FirebaseAuth.getInstance()?.uid)
                        .child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot?) {
                        var cUser = snap?.value.toString()
                        FirebaseMessaging.getInstance().subscribeToTopic("user_$cUser")
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })

                Log.d(TAG, "user found")
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> tab?.setIcon(R.drawable.ic_tab_alert_selected)
                    1 -> tab?.setIcon(R.drawable.ic_alert_feed)
                    2 -> tab?.setIcon(R.drawable.ic_tab_chat_selected)
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> tab?.setIcon(R.drawable.ic_tab_alert_unselected)
                    1 -> tab?.setIcon(R.drawable.ic_tab_alert_feed_unselected)
                    2 -> tab?.setIcon(R.drawable.ic_tab_chat_unselected)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    private fun setupViewPager() {
        val adapter: TabsAdapter =  TabsAdapter(supportFragmentManager)
        adapter.addFragment(AlertFragment(), "")
        adapter.addFragment(AlertFeedFragment(), "")
        adapter.addFragment(ChatFeedFragment(), "")
        viewPager.adapter = adapter
        viewPager.currentItem = 1
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)?.setIcon(R.drawable.ic_tab_alert_unselected)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_alert_feed)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_tab_chat_unselected)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_user) {

            startActivity(Intent(this, UserActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
