package com.sandello.ndscalculator

import java.util.*
import kotlin.math.floor

/**
 * Класс для преобразования double-числа в рубли-копейки прописью
 * @author Segr88
 */
object MoneyInWords {
    private val digits1 = arrayOf(arrayOf("одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"), arrayOf("один", "два")) //dig[0] - female, dig[1] - male
    private val digits10 = arrayOf("десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
            "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать")
    private val digits20 = arrayOf("двадцать", "тридцать", "сорок", "пятьдесят",
            "шестьдесят", "семьдесят", "восемьдесят", "девяносто")
    private val digits100 = arrayOf("сто", "двести", "триста", "четыреста", "пятьсот",
            "шестьсот", "семьсот", "восемьсот", "девятьсот")
    private val currencyUnit = arrayOf("рубль", "рубля", "рублей")
    private val tokenMoney = arrayOf("копейка", "копейки", "копеек")
    private val digit = arrayOf(arrayOf("тысяча", "тысячи", "тысяч"), arrayOf("миллион", "миллиона", "миллионов"))
    private val leWord = arrayOf(arrayOf("копейка", "копейки", "копеек", "0"), arrayOf("рубль", "рубля", "рублей", "1"), arrayOf("тысяча", "тысячи", "тысяч", "0"), arrayOf("миллион", "миллиона", "миллионов", "1"), arrayOf("миллиард", "миллиарда", "миллиардов", "1"), arrayOf("триллион", "триллиона", "триллионов", "1"))

    //рекурсивная функция преобразования целого числа num в рубли
    @ExperimentalStdlibApi
    private fun num2words(num: Long, level: Int): String {
        val words = StringBuilder(50)
        if (num == 0L) words.append("ноль ") //исключительный случай
        val sex = leWord[level][3].indexOf("1") + 1 //не красиво конечно, но работает
        val h = (num % 1000).toInt() //текущий трехзначный сегмент
        var d = h / 100 //цифра сотен
        if (d > 0) words.append(digits100[d - 1]).append(" ")
        var n = h % 100
        d = n / 10 //цифра десятков
        n %= 10 //цифра единиц
        when (d) {
            0 -> {
            }
            1 -> words.append(digits10[n]).append(" ")
            else -> words.append(digits20[d - 2]).append(" ")
        }
        if (d == 1) n = 0 //при двузначном остатке от 10 до 19, цифра едициц не должна учитываться
        when (n) {
            0 -> {
            }
            1, 2 -> words.append(digits1[sex][n - 1]).append(" ")
            else -> words.append(digits1[0][n - 1]).append(" ")
        }
        when (n) {
            1 -> words.append(leWord[level][0])
            2, 3, 4 -> words.append(leWord[level][1])
            else -> if (h != 0 || h == 0 && level == 1) //если трехзначный сегмент = 0, то добавлять нужно только "рублей"
                words.append(leWord[level][2])
            else {
                val iw: Int = when (n) {
                    1 -> 0
                    2, 3, 4 -> 1
                    else -> 2
                }
                words.append(leWord[level][iw])
            }
        }
        val nextNum = num / 1000
        return if (nextNum > 0) {
            (num2words(nextNum, level + 2) + " " + words.toString()).trim { it <= ' ' }
        } else {
            words.toString().trim { it <= ' ' }
        }
    }

    //функция преобразования вещественного числа в рубли-копейки
    //при значении money более 50-70 триллионов рублей начинает искажать копейки, осторожней при работе такими суммами
//    @ExperimentalStdlibApi
//    fun inWords(money: Double): String {
//        var token = String.format("%.2f", money)
//        token = token.substring(token.length - 2, token.length) //значение копеек в строке
//        val iw: Int
//        iw = when (token.substring(1)) {
//            "1" -> 0
//            "2", "3", "4" -> 1
//            else -> 2
//        }
//        val num = floor(money).toLong()
//        return if (num < 1000000000000L) {
//            return num2words(num, 1).capitalize(Locale.ROOT) + " " + num2words(token.toLong(), 0)
//        } else "Слишком длинное число"
//    }

}