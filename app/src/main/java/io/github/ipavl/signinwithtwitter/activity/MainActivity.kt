package io.github.ipavl.signinwithtwitter.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import io.github.ipavl.signinwithtwitter.R
import io.github.ipavl.signinwithtwitter.util.Constants

import kotlinx.android.synthetic.activity_main.*

/**
 * This is the main class for the application.
 */
public class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        sign_out.setOnClickListener(this)
        twitter_sign_in.setOnClickListener(this)

        if (prefs!!.getString(Constants.PREFS_USER_TOKEN, null) == null) {
            // The user has not logged in/authorized the app before
            sign_in_layout.setVisibility(View.VISIBLE)
        } else {
            populateUserName()
        }
    }

    /**
     * This function represents what should happen for an authenticated user, either from a new or
     * previous authorization.
     */
    fun populateUserName() {
        val screenName = prefs!!.getString(Constants.PREFS_USER_SCREEN_NAME,
                getString(R.string.nobody))

        logged_in_as.setText(getString(R.string.logged_in_as) + " " + screenName)
    }

    override fun onActivityResult(requestCode: kotlin.Int, resultCode: kotlin.Int, data: Intent?) {
        super<AppCompatActivity>.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.INTENT_OAUTH_RESULT -> {
                var screenName: String? = data?.getExtras()?.
                        getString(Constants.EXTRA_USER_SCREEN_NAME) ?: return
                var toastText = getString(R.string.logged_in_as) + " @" + screenName

                sign_in_layout.setVisibility(View.GONE)

                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
                populateUserName()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // The action bar will automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        when (item!!.getItemId()) {
            R.id.action_settings -> {
                return true
            }
        }

        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.twitter_sign_in -> {
                    startActivityForResult(Intent(this, javaClass<OAuthActivity>()),
                            Constants.INTENT_OAUTH_RESULT)
            }
            R.id.sign_out -> {
                prefs!!.edit().clear().commit()
                populateUserName()

                sign_out.setVisibility(View.GONE)
                sign_in_layout.setVisibility(View.VISIBLE)
            }
        }
    }
}
