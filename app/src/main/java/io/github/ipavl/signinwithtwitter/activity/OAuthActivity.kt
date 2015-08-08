package io.github.ipavl.signinwithtwitter.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import io.github.ipavl.signinwithtwitter.util.Constants

import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken
import twitter4j.conf.ConfigurationBuilder

/**
 * This class handles authorization through Twitter OAuth via a WebView. On a successful login,
 * the user's ID, token, token secret, and current screen name are stored in the shared preferences
 * file.
 */
public class OAuthActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private var twitter: Twitter? = null

    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Configure the Twitter4J instance
        twitter = TwitterFactory(ConfigurationBuilder()
                .setOAuthConsumerKey(Constants.TWITTER_KEY)
                .setOAuthConsumerSecret(Constants.TWITTER_SECRET)
                .build()
        ).getInstance()

        webView = WebView(this)
        webView!!.getSettings().setJavaScriptEnabled(true)
        setContentView(webView)

        performLogin()
    }

    /**
     * Gets the OAuth RequestToken and then prompts the user for app authorization.
     */
    private fun performLogin() {
        object : AsyncTask<Void, Void, RequestToken>() {
            override fun doInBackground(vararg params: Void): RequestToken {
                var token: RequestToken? = null
                try {
                    token = twitter!!.getOAuthRequestToken(Constants.URI_SCHEME)
                } catch (te: TwitterException) {
                    Log.e(Constants.TAG, "Exception getting OAuth request token: " + te.toString())
                }

                return token!!
            }

            override fun onPostExecute(token: RequestToken) {
                webView!!.setWebViewClient(object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        if (url!!.startsWith(Constants.URI_SCHEME)) {
                            handleLoginResult(token,
                                    Uri.parse(url).getQueryParameter("oauth_verifier"))
                        }
                    }
                })
                webView!!.loadUrl(token.getAuthorizationURL())
            }
        }.execute()
    }

    /**
     * After receiving authorization, get the access token and store the information in SharedPrefs.
     */
    private fun handleLoginResult(requestToken: RequestToken, oauthVerifier: String) {
        object : AsyncTask<Void, Void, AccessToken>() {
            override fun doInBackground(vararg params: Void): AccessToken {
                var accessToken: AccessToken? = null
                try {
                    accessToken = twitter!!.getOAuthAccessToken(requestToken, oauthVerifier)
                } catch (te: TwitterException) {
                    Log.e(Constants.TAG, "Exception getting OAuth access token: " + te.toString())
                }

                return accessToken!!
            }

            /**
             * Saves the user ID, token, and token secret to shared preferences and finishes the
             * activity with a result code of Constants.INTENT_OAUTH and the user's screen name.
             */
            override fun onPostExecute(token: AccessToken) {
                var intent = Intent()
                intent.putExtra(Constants.EXTRA_USER_SCREEN_NAME, token.getScreenName())

                prefs!!.edit()
                        .putLong(Constants.PREFS_USER_ID, token.getUserId())
                        .putString(Constants.PREFS_USER_TOKEN, token.getToken())
                        .putString(Constants.PREFS_USER_SECRET, token.getTokenSecret())
                        .putString(Constants.PREFS_USER_SCREEN_NAME, token.getScreenName())
                        .commit()

                setResult(Constants.INTENT_OAUTH_RESULT, intent)
                finish()
            }
        }.execute()
    }
}
