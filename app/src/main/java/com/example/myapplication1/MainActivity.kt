package com.example.myapplication1


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    lateinit var txtInput: TextView


    var lastNumeric: Boolean = false


    var stateError: Boolean = false


    var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtInput = findViewById(R.id.txtInput)
    }

    fun onChange(view: View) {
        if (stateError) {

            txtInput.text = (view as Button).text
            stateError = false
        } else {
            txtInput.append((view as Button).text)
        }

        lastNumeric = true
    }


    fun onDecimalPoint(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            txtInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }


    fun onOperator(view: View) {
        if (lastNumeric && !stateError && (view as Button).text != "√"  || ((view as Button).text == "√" && !lastNumeric)) {
            txtInput.append((view as Button).text);
            lastNumeric = false;
            lastDot = false;
        }
    }


    fun delLastSymbol (view: View) {
        if(txtInput.text.isNotEmpty() && txtInput.length() != 1){

        if(lastDot){
            lastDot = false
        }

        txtInput.text = txtInput.text.toString().substring(0,txtInput.length() - 1)

        if(txtInput.text[txtInput.length() - 1] == '.'){
            lastDot = true
        }else{
            lastNumeric = true
        }

        }else if (txtInput.length() == 1){
            txtInput.text = ""
        }



    }

    fun onClear(view: View) {
        this.txtInput.text = ""
        lastNumeric = false
        stateError = false
        lastDot = false
    }




   override fun onSaveInstanceState(outState: Bundle) {


       super.onSaveInstanceState(outState)
       outState.putString("expression", txtInput.text.toString())
       outState.putBoolean("lastNumeric", lastNumeric)
       outState.putBoolean("stateError", stateError)
       outState.putBoolean("lastDot", lastDot)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        txtInput.text = savedInstanceState.getString("expression")
        lastNumeric = savedInstanceState.getBoolean("lastNumeric")
        stateError = savedInstanceState.getBoolean("stateError")
        lastDot= savedInstanceState.getBoolean("lastDot")
    }



    fun onEqual(view: View) {

        if (lastNumeric && !stateError) {

            val txt = txtInput.text.toString()
            val calculate:Calculate = Calculate()
            txtInput.text = calculate.getResult(txt).toString();

//            val expression = ExpressionBuilder(txt).build()
//            try {
//                val result = expression.evaluate()
//                txtInput.text = result.toString()
//                lastDot = true
//            } catch (ex: ArithmeticException) {
//
//                txtInput.text = "Error"
//                stateError = true
//                lastNumeric = false
//            }
        }
    }
}