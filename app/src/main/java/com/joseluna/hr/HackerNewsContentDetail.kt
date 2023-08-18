package com.joseluna.hr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.progressindicator.LinearProgressIndicator


class HackerNewsContentDetail : Fragment() {

    private val args: HackerNewsContentDetailArgs by navArgs()
    private lateinit var loadingView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = args.title
        (activity as AppCompatActivity).supportActionBar.apply {
            this?.subtitle = title
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hacker_news_content_detail, container, false)
        val urlValue = args.url


        val webView = view.findViewById<WebView>(R.id.web_view)
        loadingView = view.findViewById(R.id.loading_indicator)
        val progressIndicator = loadingView.findViewById<LinearProgressIndicator>(R.id.loadingProgress)

        webView.settings.javaScriptEnabled = true

        loadingView.visibility = View.VISIBLE
        webView.visibility = View.INVISIBLE

        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                if(newProgress < 100){
                    progressIndicator.progress = newProgress
                }

                if(newProgress == 100){
                    loadingView.visibility = View.INVISIBLE
                    webView.visibility = View.VISIBLE
                }
            }
        }


        webView.loadUrl(urlValue)
        return view
    }


}