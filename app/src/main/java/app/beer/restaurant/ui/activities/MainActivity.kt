package app.beer.restaurant.ui.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import app.beer.restaurant.R
import app.beer.restaurant.api.App
import app.beer.restaurant.database.initFirebase
import app.beer.restaurant.model.User
import app.beer.restaurant.model.basket.BasketResponse
import app.beer.restaurant.ui.fragments.account.AccountFragment
import app.beer.restaurant.ui.fragments.cart.CartFragment
import app.beer.restaurant.ui.fragments.chat.ChatFragment
import app.beer.restaurant.ui.fragments.main.MainFragment
import app.beer.restaurant.ui.fragments.register.AuthFragment
import app.beer.restaurant.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var splashScreen: FrameLayout

    lateinit var sharedManager: SharedManager

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        APP_ACTIVITY = this
        APP = application as App

        bottomNavigationView = findViewById(R.id.bottomNav)
        splashScreen = findViewById(R.id.splash_screen)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(MainFragment())
                R.id.nav_basket -> replaceFragment(CartFragment())
                R.id.nav_chat -> replaceFragment(ChatFragment())
                R.id.nav_account -> replaceFragment(AccountFragment())
            }
            true
        }

        sharedManager = SharedManager()

        if (sharedManager.getBoolean(IS_AUTH_KEY)) {
            if (sharedManager.getString(LANGUAGE_KEY) == "") {
                sharedManager.putString(LANGUAGE_KEY, LANGUAGE_ENG)
            }

            val localStr =
                when (sharedManager.getString(LANGUAGE_KEY)) {
                    LANGUAGE_RUS -> "ru"
                    LANGUAGE_ENG -> "en"
                    LANGUAGE_DOT -> "de"
                    LANGUAGE_BOL -> "bg"
                    else -> "en"
                }

            val locale = Locale(localStr)
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.locale = locale
            resources.updateConfiguration(configuration, null)

            bottomNavigationView.visibility = View.VISIBLE
            APP.getApi().getUser(sharedManager.getInt(USER_ID_KEY))
                .enqueue(RetrofitCallback<User> { _, response ->
                    if (response.isSuccessful && response.code() != 404) {
                        if (response.body() != null) {
                            USER = response.body()!!
                            createBadgeBTN()
                            initFirebase()
                            replaceFragment(MainFragment())
                            splashScreen.visibility = View.GONE
                        }
                    }
                })
        } else {
            bottomNavigationView.visibility = View.GONE
            splashScreen.visibility = View.GONE
            replaceFragment(AuthFragment(), false)
        }

        if (sharedManager.getBoolean("is_night_mode")) {
            changeTheme(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            changeTheme(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    fun changeTheme(themeName: Int) {
        AppCompatDelegate.setDefaultNightMode(themeName)
    }

    fun createBadgeBTN() {
        APP.getApi().getBasketItems(USER.id)
            .enqueue(RetrofitCallback<BasketResponse> { _, response ->
                if (response.isSuccessful && response.code() != 404) {
                    val body = response.body()
                    if (body != null && body.results!!.isNotEmpty()) {
                        count = body.results!!.size
                        if (count > 0) {
                            val badge = bottomNavigationView.getOrCreateBadge(R.id.nav_basket)
                            badge.number = count
                            badge.isVisible = true
                        }
                    }
                }
            })
        // сделать отображение badge если у пользователя есть товары в корзине и чтобы отоюражалось количество товаров
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (sharedManager.getBoolean(IS_AUTH_KEY)) {
            menuInflater.inflate(R.menu.main_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sign_out) {
            sharedManager.putBoolean(IS_AUTH_KEY, false)
            restartActivity()
        }
        return true
    }

}