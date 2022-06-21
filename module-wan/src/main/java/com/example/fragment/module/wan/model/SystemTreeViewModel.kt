package com.example.fragment.module.wan.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fragment.library.base.http.HttpRequest
import com.example.fragment.library.base.http.get
import com.example.fragment.library.base.model.BaseViewModel
import com.example.fragment.library.common.bean.SystemTreeBean
import com.example.fragment.library.common.bean.SystemTreeListBean
import kotlinx.coroutines.launch

class SystemTreeViewModel : BaseViewModel() {

    var listData: List<SystemTreeBean> = ArrayList()
    var listScroll: Int = 0

    private val systemTreeResult: MutableLiveData<List<SystemTreeBean>> by lazy {
        MutableLiveData<List<SystemTreeBean>>().also {
            getSystemTree()
        }
    }

    fun systemTreeResult(): LiveData<List<SystemTreeBean>> {
        return systemTreeResult
    }

    /**
     * 获取项目分类
     */
    private fun getSystemTree() {
        viewModelScope.launch {
            val request = HttpRequest("tree/json")
            val response = get<SystemTreeListBean>(request) { updateProgress(it) }
            //如果LiveData.value == null，则在转场动画结束后加载数据，用于解决过度动画卡顿问题
            if (systemTreeResult.value == null) {
                transitionAnimationEnd(request, response)
            }
            response.data?.let {
                systemTreeResult.postValue(it)
            }
        }
    }

}