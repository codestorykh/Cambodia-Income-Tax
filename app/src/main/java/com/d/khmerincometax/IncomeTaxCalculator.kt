package com.d.khmerincometax/*
package com.d.khmerincometax

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_income_tax_calculation.*
import java.text.DecimalFormat
import java.text.NumberFormat

class IncomeTaxCalculator : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtView = findViewById<View>(R.id.txt_marquee) as TextView
        txtView.isSelected = true

        val cal = findViewById<View>(R.id.buttonCal) as Button
        amountCannotStartWithZero()
        buttonReset()
        cal.setOnClickListener { calculate() }
    }


    @SuppressLint("SetTextI18n")
    private fun calculate() {

        val taxDependent = 3.75f
        val taxExchange = 4000f
        val benefitTaxPercentage = 0.2f

        val editBenefit = findViewById<View>(R.id.editBenefit) as EditText
        val editIncome = findViewById<View>(R.id.editIncome) as EditText
        val editChild = findViewById<View>(R.id.editChild) as EditText
        val editAfterTax = findViewById<View>(R.id.editTextCal) as EditText
        val editTaxResult = findViewById<View>(R.id.editTextTax) as EditText

        val personBenefit = editBenefit.text.toString()
        val personIncome = editIncome.text.toString()
        val personChild = editChild.text.toString()

        //Converting String to float

        val currencyType = findViewById<View>(R.id.currency_type) as RadioGroup
        val currencyRadioButtonID = currencyType.checkedRadioButtonId
        val currencyRadioButton =
            currencyType.findViewById<View>(currencyRadioButtonID) as RadioButton
        val currency = currencyRadioButton.text as String


        val marryRadioGroup = findViewById<View>(R.id.is_married) as RadioGroup
        val marryRadioButtonID = marryRadioGroup.checkedRadioButtonId
        val marryRadioButton = marryRadioGroup.findViewById<View>(marryRadioButtonID) as RadioButton
        val isMarry = marryRadioButton.text as String

        if (personIncome.isBlank()) {
            Toast.makeText(this, "អ្នកមិនបានបញ្ចូលប្រាក់បៀវត្សទេ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (personIncome.startsWith("0")) {
            Toast.makeText(this, "ការបញ្ចូលប្រាក់បៀវត្សមិនត្រឹមត្រូវ", Toast.LENGTH_SHORT).show();
            return;
        }

        var income = 0f
        var benefitTax = 0f;
        var benefitAfterTax = 0f

        if (currency == "ប្រាក់រៀល") {
            income = personIncome.toFloat()
            if (!personBenefit.isBlank() && personBenefit.toFloat() > 0f) {
                benefitTax = (personBenefit.toFloat() * benefitTaxPercentage)
                benefitAfterTax = personBenefit.toFloat() - benefitTax
            }
        }

        if (currency == "ប្រាក់ដុល្លារ") {
            income = personIncome.toFloat() * taxExchange
            if (!personBenefit.isBlank() && personBenefit.toFloat() > 0f) {
                benefitTax = (personBenefit.toFloat() * benefitTaxPercentage) * taxExchange
                benefitAfterTax = (personBenefit.toFloat() * taxExchange) - benefitTax
            }
        }

        var marryTax = 0f
        if (isMarry == "មាន") {
            marryTax = taxExchange * taxDependent
        }

        var childTax = 0f
        if (!personChild.isBlank()) {
            if (personChild.toInt() > 15) {
                Toast.makeText(this, "ការបំពេញកូនក្នុងបន្ទុកមិនត្រឹមត្រូវទេ", Toast.LENGTH_SHORT)
                    .show();
                return;
            }
            childTax = (taxDependent * taxExchange) * personChild.toInt()
        }

        val benefit = if (personBenefit.isBlank()) {
            0f;
        } else {
            personBenefit.toFloat()
        }

        var totalTax = 0f
        var afterTax = 0f
        if (income > 1200000f) {
            totalTax = taxCalculator(income) + benefitTax
            afterTax = (income - taxCalculator(income)) + benefitTax
        } else {
            totalTax = benefitTax
            afterTax = taxCalculator(income)
        }

        editTaxResult.setText(currencyFormat(totalTax) + " ៛")

        val result = currencyFormat(afterTax + marryTax + childTax + benefitAfterTax)
        editAfterTax.setText("$result ៛")
    }


    private fun taxCalculator(income: Float): Float {

        var taxAmount = 0f

        if (income <= 1200000) {
            taxAmount = income
        }
        if (income in 1200001.0..2000000.0) {
            taxAmount = (income * 0.05).toFloat()
        }
        if (income in 2000001.0..8500000.0) {
            taxAmount = (income * 0.1).toFloat()
        }
        if (income in 8500001.0..1.25E7) {
            taxAmount = (income * 0.15).toFloat()
        }
        if (income > 12500001) {
            taxAmount = (income * 0.2).toFloat()
        }

        return taxAmount
    }

    private fun buttonReset() {
        val reset = findViewById<View>(R.id.buttonReset) as Button
        reset.setOnClickListener {
            val benefit = findViewById<View>(R.id.editBenefit) as EditText
            val income = findViewById<View>(R.id.editIncome) as EditText
            val child = findViewById<View>(R.id.editChild) as EditText
            val calculateResult = findViewById<View>(R.id.editTextCal) as EditText
            val taxIncome = findViewById<View>(R.id.editTextTax) as EditText
            calculateResult.setText("")
            benefit.setText("")
            child.setText("")
            income.setText("")
            taxIncome.setText("")
        }
    }

    private fun amountCannotStartWithZero() {
        editIncome.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editIncome.text.toString() == "0") {
                    messageToast()
                }
            }

            override fun afterTextChanged(arg0: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })

        editBenefit.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editBenefit.text.toString() == "0") {
                    messageToast()
                }
            }

            override fun afterTextChanged(arg0: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })
    }

    private fun messageToast() {
        Toast.makeText(this, "Not allowed zero starter", Toast.LENGTH_LONG)
            .show()
        editBenefit.setText("")
    }

    private fun currencyFormat(amount: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(amount)
    }
}*/
