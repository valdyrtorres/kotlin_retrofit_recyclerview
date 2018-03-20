package com.example.teste.restprj

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val client by lazy {
        ArticlesApiClient.create()
    }

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Uncomment to show list of articles in Logcat
        showArticles()

        // Uncomment to show article with id=1 in Logcat
        //showArticle(1)

        // Test post request and add new article
        //val article = Article(1, 101, "Test Article", "Have fun posting")
        //postArticle(article)
    }

    // GET List of Articles
    private fun showArticles() {

        disposable = client.getArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> setupRecycler(result)  },
                        { error -> Log.e("ERROR", error.message) }
                )

    }

    // GET Article by id
    private fun showArticle(id: Int) {

        disposable = client.getArticle(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> Log.v("ARTICLE ID ${id}: ", "" + result) },
                        { error -> Log.e("ERROR", error.message) }
                )

    }

    // POST new Article
    private fun postArticle(article: Article) {

        disposable = client.addArticle(article)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> Log.v("POSTED ARTICLE", "" + article ) },
                        { error -> Log.e("ERROR", error.message ) }
                )
    }

    fun setupRecycler(articleList: List<Article>) {

        articles_recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        articles_recycler.layoutManager = layoutManager
        articles_recycler.adapter = ArticleAdapter(articleList){
            Log.v("Article", it.id.toString())
        }

    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
