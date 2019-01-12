package com.mk.calculator

import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        lateinit var textView: TextView
        lateinit var context: Context
    }

    var functionText = ""
    var functionPosition = -1
    var pointState = false
    lateinit var webView: WebView
    lateinit var numberButton: Array<Button>
    lateinit var functionButton: Array<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        textView = calculation
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(JSLinker(), "android")
        webView.loadUrl("file:///android_asset/calculate.html")

        numberButton = arrayOf(one, two, three, four, five, six, seven, eight, nine)
        for (i in 0..8) {
            numberButton[i].setOnClickListener {
                calculation.text = calculation.text.toString() + (i + 1)
            }
        }

        functionButton =
                arrayOf(clear, plus_minus, percent, division, multiplication, minus, plus, equal, point, zerozero, zero)
        for (i in 0 until functionButton.size) {
            functionButton[i].setOnClickListener(this)
        }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.clear -> {
                    functionPosition = -1
                    pointState = false
                    calculation.text = ""
                }
                R.id.plus_minus -> {
                    when {
                        functionPosition + 1 == 0 && calculation.text.isEmpty() -> calculation.text = "-"
                        functionPosition + 1 == calculation.length() -> calculation.text = calculation.text.toString() +
                                "-"
                        else -> {
                            if (calculation.text[functionPosition + 1] == '-') {
                                var builder = StringBuilder()
                                builder.append(calculation.text)
                                builder.deleteCharAt(functionPosition + 1)
                                calculation.text = builder
                            } else {
                                var builder = StringBuilder()
                                builder.append(calculation.text)
                                builder.insert(functionPosition + 1, "-")
                                calculation.text = builder
                            }
                        }
                    }

                }
                R.id.percent -> {
                    if (calculation.text.isNotEmpty()) {
                        functionPosition = calculation.length()
                        functionText = "%"
                    }
                }
                R.id.division -> {
                    if (calculation.text.isNotEmpty()) {
                        functionPosition = calculation.length()
                        functionText = "÷"
                    }
                }
                R.id.multiplication -> {
                    if (calculation.text.isNotEmpty()) {
                        functionPosition = calculation.length()
                        functionText = "×"
                    }
                }
                R.id.minus -> {
                    if (calculation.text.isNotEmpty()) {
                        functionPosition = calculation.length()
                        functionText = "-"
                    }
                }
                R.id.plus -> {
                    if (calculation.text.isNotEmpty()) {
                        functionPosition = calculation.length()
                        functionText = "+"
                    }
                }
                R.id.equal -> {
                    if (!isModeNumber()) {
                        Toast.makeText(context, "수식이 완성되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        functionPosition = -1
                        pointState = false
                        webView.loadUrl(
                            "javascript:calculate('${calculation.text.toString().replace(
                                "×",
                                "*"
                            ).replace("÷", "/")}')"
                        )
                    }
                }
                R.id.point -> {
                    if (isModeNumber()) {
                        if (calculation.text[calculation.text.lastIndex] != '.' && !pointState) {
                            calculation.text = calculation.text.toString() + "."
                            pointState = true
                        }
                    } else {
                        calculation.text = calculation.text.toString() + "0."
                        pointState = true
                    }
                }
                R.id.zerozero -> {
                    calculation.text = calculation.text.toString() + "00"
                }
                R.id.zero -> {
                    calculation.text = calculation.text.toString() + "0"
                }
            }
            if (!isModeNumber()) {
                calculation.text = calculation.text.slice(0 until calculation.text.lastIndex).toString() + functionText
            } else {
                if (functionText.isNotEmpty()) {
                    calculation.text = calculation.text.toString() + functionText
                    pointState = false
                }
            }
            functionText = ""
        }
    }

    fun isModeNumber() =
        calculation.text.isNotEmpty() && (calculation.text[calculation.text.lastIndex].toString() in "0".."9" || calculation.text[calculation.text.lastIndex].toString() == "%" || calculation.text[calculation.text.lastIndex].toString() == ".")

    private class JSLinker {

        @JavascriptInterface
        fun sendData(data: String) {
            Handler().post {
                if (data == "error") {
                    Toast.makeText(context, "수식에 문제가 있습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    textView.text = data
                }
            }
        }

    }

}
