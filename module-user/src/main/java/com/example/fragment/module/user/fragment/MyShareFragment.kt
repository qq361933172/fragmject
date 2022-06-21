package com.example.fragment.module.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragment.library.base.model.BaseViewModel
import com.example.fragment.library.base.view.pull.OnLoadMoreListener
import com.example.fragment.library.base.view.pull.OnRefreshListener
import com.example.fragment.library.base.view.pull.PullRefreshLayout
import com.example.fragment.library.common.adapter.ArticleAdapter
import com.example.fragment.library.common.fragment.RouterFragment
import com.example.fragment.module.user.databinding.MyShareFragmentBinding
import com.example.fragment.module.user.model.MyShareViewModel

class MyShareFragment : RouterFragment() {

    private val viewModel: MyShareViewModel by viewModels()
    private var _binding: MyShareFragmentBinding? = null
    private val binding get() = _binding!!
    private var _articleAdapter: ArticleAdapter? = null
    private val articleAdapter get() = _articleAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyShareFragmentBinding.inflate(inflater, container, false)
        _articleAdapter = ArticleAdapter()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.listData = articleAdapter.getData()
        viewModel.listScroll = binding.list.computeVerticalScrollOffset()
        _articleAdapter = null
        _binding = null
    }

    override fun initView() {
        binding.black.setOnClickListener { activity.onBackPressed() }
        //我分享的文章列表
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        binding.list.adapter = articleAdapter
        //下拉刷新
        binding.pullRefresh.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: PullRefreshLayout) {
                viewModel.getMyShareArticleHome()
            }
        })
        //加载更多
        binding.pullRefresh.setOnLoadMoreListener(binding.list, object : OnLoadMoreListener {
            override fun onLoadMore(refreshLayout: PullRefreshLayout) {
                viewModel.getMyShareArticleNext()
            }
        })
    }

    override fun initViewModel(): BaseViewModel {
        if (viewModel.listData.isNullOrEmpty()) {
            viewModel.myShareArticleResult().observe(viewLifecycleOwner) { result ->
                httpParseSuccess(result) {
                    if (viewModel.isHomePage()) {
                        articleAdapter.setNewData(it.data?.shareArticles?.datas)
                    } else {
                        articleAdapter.addData(it.data?.shareArticles?.datas)
                    }
                }
                //结束下拉刷新状态
                binding.pullRefresh.finishRefresh()
                //设置加载更多状态
                binding.pullRefresh.setLoadMore(viewModel.hasNextPage())
            }
        } else {
            articleAdapter.setNewData(viewModel.listData)
            binding.list.scrollTo(0, viewModel.listScroll)
        }
        return viewModel
    }

}