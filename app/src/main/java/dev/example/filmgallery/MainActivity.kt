package dev.example.filmgallery

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dev.example.filmgallery.ui.FilmDescriptionFragment
import dev.example.filmgallery.ui.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentContainer = FrameLayout(this).apply { id = View.generateViewId() }

        setContentView(fragmentContainer)

        if (savedInstanceState == null) {
            val navHostFragment = NavHostFragment.create(R.navigation.nav_graph)
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, navHostFragment, "NavHostFragment")
                .commit()
        }
    }
}

