package com.basic.newsapp.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.basic.newsapp.R
import com.basic.newsapp.adapter.NewsListAdapter
import com.basic.newsapp.databinding.FragmentDetailNewsBinding
import com.basic.newsapp.databinding.FragmentNewsBinding
import com.basic.newsapp.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewsDetailFragment : Fragment(R.layout.fragment_detail_news) {

    private var _binding: FragmentDetailNewsBinding? = null
    private val binding get() = _binding!!

    val args : NewsDetailFragmentArgs by navArgs()

    @Inject
    lateinit var newsListAdapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDetailNewsBinding.bind(view)


        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
    }


}