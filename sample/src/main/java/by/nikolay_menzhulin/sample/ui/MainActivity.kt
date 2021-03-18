package by.nikolay_menzhulin.sample.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import by.nikolay_menzhulin.sample.R
import by.nikolay_menzhulin.sample.service.GitHubRepository
import by.nikolay_menzhulin.sample.service.response.GitHubRepoResponse
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val gitHubRepository = GitHubRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.get_repo_btn).setOnClickListener { loadDate() }
    }

    private fun loadDate() {
        lifecycleScope.launch {
            val repo: GitHubRepoResponse? = gitHubRepository.getRepo().getDataOrNull()
            val repoName: String = repo?.name ?: "EMPTY"
            Toast.makeText(this@MainActivity, repoName, LENGTH_SHORT).show()
        }
    }
}