package com.wangzhen.openrc.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.wangzhen.openrc.R
import kotlinx.android.synthetic.main.activity_web_ota.*


class WebOtaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ip = intent.getStringExtra("ip")
        setContentView(R.layout.activity_web_ota)
        webView.loadUrl("http://${ip}:8080/webota") //加载url


        //使用webview显示html代码
//        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
//                "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);


        //使用webview显示html代码
//        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
//                "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);
//        webView.addJavascriptInterface(this, "android") //添加js监听 这样html就能调用客户端

        webView.webChromeClient = webChromeClient
        webView.webViewClient = webViewClient

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true //允许使用js


        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE //不使用缓存，只从网络获取数据.

        //支持屏幕缩放
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true

        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private val webViewClient: WebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) { //页面加载完成
            progressBar.setVisibility(View.GONE)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) { //页面开始加载
            progressBar.setVisibility(View.VISIBLE)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.i("wz", "拦截url:$url")
            return super.shouldOverrideUrlLoading(view, url)
        }
    }
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private val webChromeClient: WebChromeClient = object : WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        override fun onJsAlert(
            webView: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            val localBuilder: AlertDialog.Builder = AlertDialog.Builder(webView.context)
            localBuilder.setMessage(message).setPositiveButton("确定", null)
            localBuilder.setCancelable(false)
            localBuilder.create().show()

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm()
            return true
        }

        //获取网页标题
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            Log.i("wz", "网页标题:$title")
        }

        //加载进度回调
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progressBar.setProgress(newProgress)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.i("wz", "是否有上一个页面:" + webView.canGoBack())
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) { //点击返回按钮的时候判断有没有上一页
            webView.goBack() // goBack()表示返回webView的上一页面
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * JS调用android的方法
     * @param str
     * @return
     */

    override fun onDestroy() {
        super.onDestroy()
        //释放资源
        webView.destroy()
    }
}