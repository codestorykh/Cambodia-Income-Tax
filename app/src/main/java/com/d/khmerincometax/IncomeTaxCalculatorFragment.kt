@file:JvmName("IncomeTaxCalculatorFragmentKt")

package com.d.khmerincometax

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import java.text.DecimalFormat
import java.text.NumberFormat


class IncomeTaxCalculatorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_income_tax_calculation, container, false)
        val txtView = root.findViewById<View>(R.id.txt_marquee) as TextView
        txtView.isSelected = true

        val cal = root.findViewById<View>(R.id.buttonCal) as Button
        amountCannotStartWithZero(root)
        buttonReset(root)
        cal.setOnClickListener { calculate(root) }

        setHasOptionsMenu(true)

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController()
        )
                || super.onOptionsItemSelected(item)
    }

    private fun calculate(root: View) {

        val taxDependent = 3.75f
        val taxExchange = 4000f
        val benefitTaxPercentage = 0.2f

        val editBenefit = root.findViewById<View>(R.id.editBenefit) as EditText
        val editIncome = root.findViewById<View>(R.id.editIncome) as EditText
        val editChild = root.findViewById<View>(R.id.editChild) as EditText
        val editAfterTax = root.findViewById<View>(R.id.editTextCal) as EditText
        val editTaxResult = root.findViewById<View>(R.id.editTextTax) as EditText

        val personBenefit = editBenefit.text.toString()
        val personIncome = editIncome.text.toString()
        val personChild = editChild.text.toString()

        //Converting String to float

        val currencyType = root.findViewById<View>(R.id.currency_type) as RadioGroup
        val currencyRadioButtonID = currencyType.checkedRadioButtonId
        val currencyRadioButton =
            currencyType.findViewById<View>(currencyRadioButtonID) as RadioButton
        val currency = currencyRadioButton.text as String


        val marryRadioGroup = root.findViewById<View>(R.id.is_married) as RadioGroup
        val marryRadioButtonID = marryRadioGroup.checkedRadioButtonId
        val marryRadioButton = marryRadioGroup.findViewById<View>(marryRadioButtonID) as RadioButton
        val isMarry = marryRadioButton.text as String

        if (personIncome.isBlank()) {
            Toast.makeText(activity, "អ្នកមិនបានបញ្ចូលប្រាក់បៀវត្សទេ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (personIncome.startsWith("0")) {
            Toast.makeText(activity, "ការបញ្ចូលប្រាក់បៀវត្សមិនត្រឹមត្រូវ", Toast.LENGTH_SHORT)
                .show();
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
                Toast.makeText(
                    activity,
                    "ការបំពេញកូនក្នុងបន្ទុកមិនត្រឹមត្រូវទេ",
                    Toast.LENGTH_SHORT
                )
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
            return taxAmount = income
        }
        if (income in 1200001.0..2000000.0) {
            return taxAmount = (income * 0.05).toFloat()
        }
        if (income in 2000001.0..8500000.0) {
            return taxAmount = (income * 0.1).toFloat()
        }
        if (income in 8500001.0..1.25E7) {
            return taxAmount = (income * 0.15).toFloat()
        }
        if (income > 12500001) {
            return taxAmount = (income * 0.2).toFloat()
        }
        return taxAmount
    }

    private fun buttonReset(root: View) {
        val reset = root.findViewById<View>(R.id.buttonReset) as Button
        reset.setOnClickListener {
            val benefit = root.findViewById<View>(R.id.editBenefit) as EditText
            val income = root.findViewById<View>(R.id.editIncome) as EditText
            val child = root.findViewById<View>(R.id.editChild) as EditText
            val calculateResult = root.findViewById<View>(R.id.editTextCal) as EditText
            val taxIncome = root.findViewById<View>(R.id.editTextTax) as EditText
            calculateResult.setText("")
            benefit.setText("")
            child.setText("")
            income.setText("")
            taxIncome.setText("")
        }
    }

    private fun amountCannotStartWithZero(root: View) {
        root.findViewById<EditText>(R.id.editIncome).addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (root.findViewById<EditText>(R.id.editIncome).text.toString() == "0") {
                    messageToast(root)
                }
            }

            override fun afterTextChanged(arg0: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })

        root.findViewById<EditText>(R.id.editBenefit).addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (root.findViewById<EditText>(R.id.editBenefit).text.toString() == "0") {
                    messageToast(root)
                }
            }

            override fun afterTextChanged(arg0: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })
    }

    private fun messageToast(root: View) {
        Toast.makeText(activity, "Not allowed zero starter", Toast.LENGTH_LONG)
            .show()
        (root.findViewById<EditText>(R.id.editBenefit)).setText("")
    }

    private fun currencyFormat(amount: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(amount)
    }
}