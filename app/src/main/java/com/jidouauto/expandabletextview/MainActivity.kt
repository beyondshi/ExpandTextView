package com.jidouauto.expandabletextview

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var expandTextView: ExpandTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        button = findViewById(R.id.button)
        expandTextView = findViewById(R.id.expandTextView)
        expandTextView.setContentText(
            "测试测试测试测试测￥%￥……%￥……%￥……%￥……%试测试测试测试测试测试测测试测试测试" +
                    "测试测试测试测试测试测试测试测试%^&%^&%^%$$*&**^测试测试测试测试测试测" +
                    "[天啊][哇][天啊][哇][天啊][哇]试测试测试测试" +
                    "青少年健康请问您我就去xajnxxjkasxn测试测试测试" +
                    "测试测试测试测试测试测试测测试测试cscscscsqqq测试测试测试测试测试测试测试测试测试测试" +
                    "xsajnjksn测试测试测试测试测试"
        )

        button.setOnClickListener {
            expandTextView.expanded = !expandTextView.expanded
            if (expandTextView.expanded) {
                button.text = "收起"
            } else {
                button.text = "展开"
            }
        }
    }
}