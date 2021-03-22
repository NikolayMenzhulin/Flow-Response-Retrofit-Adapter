package by.nikolay_menzhulin.flow_response_retrofit_adapter.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import by.nikolay_menzhulin.flow_response_retrofit_adapter.R
import by.nikolay_menzhulin.flow_response_retrofit_adapter.service.GitHubRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val gitHubRepository = GitHubRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.get_repo_btn).setOnClickListener { loadDate() }
    }

    private fun loadDate() {
        lifecycleScope.launch {
            gitHubRepository.getRepo().collect { response ->
                Log.d("TEST_TAG", response.toString())
                val repoName: String = response.getDataOrNull()?.name ?: "EMPTY"
                Toast.makeText(this@MainActivity, repoName, LENGTH_SHORT).show()
            }
        }
    }
}