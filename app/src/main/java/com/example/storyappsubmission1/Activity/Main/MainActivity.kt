package com.example.storyappsubmission1.Activity.Main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyappsubmission1.Activity.Start.LoginActivity
import com.example.storyappsubmission1.Activity.Start.WelcomeActivity
import com.example.storyappsubmission1.Data.Adapter.LoadingStateAdapter
import com.example.storyappsubmission1.Data.Adapter.PagingAdapter
import com.example.storyappsubmission1.R
import com.example.storyappsubmission1.ViewModel.FactoryVM
import com.example.storyappsubmission1.ViewModel.MainVM
import com.example.storyappsubmission1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val _mainVM : MainVM by viewModels{
        FactoryVM.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: PagingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        binding.post.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                _mainVM.logout()
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                return true
            }
            R.id.action_Maps->{
                val i = Intent(this, MapsActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                return true
            }
            else -> return true
        }
    }



    private fun setupViewModel() {
        _mainVM.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                listAdapter = PagingAdapter()

                _mainVM.getStories(token).observe(this) {
                    listAdapter.submitData(lifecycle, it)
                }

                binding.rvStory.adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        listAdapter.retry()
                    }
                )
            }
        }
    }

}