package com.sandello.ndscalculator

import kotlin.math.floor

/**
 * Класс для преобразования double-числа в рубли-копейки прописью
 * @author Segr88
 */
object MoneyInWords {
    private val dig1 = arrayOf(arrayOf("одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"), arrayOf("один", "два")) //dig[0] - female, dig[1] - male
    private val dig10 = arrayOf("десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
            "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать")
    private val dig20 = arrayOf("двадцать", "тридцать", "сорок", "пятьдесят",
            "шестьдесят", "семьдесят", "восемьдесят", "девяносто")
    private val dig100 = arrayOf("сто", "двести", "триста", "четыреста", "пятьсот",
            "шестьсот", "семьсот", "восемьсот", "девятьсот")
    private val leWord = arrayOf(arrayOf("копейка", "копейки", "копеек", "0"), arrayOf("рубль", "рубля", "рублей", "1"), arrayOf("тысяча", "тысячи", "тысяч", "0"), arrayOf("миллион", "миллиона", "миллионов", "1"), arrayOf("миллиард", "миллиарда", "миллиардов", "1"), arrayOf("триллион", "триллиона", "триллионов", "1"))

    //рекурсивная функция преобразования целого числа num в рубли
    private fun num2words(num: Long, level: Int): String {
        val words = StringBuilder(50)
        if (num == 0L) words.append("ноль ") //исключительный случай
        val sex = leWord[level][3].indexOf("1") + 1 //не красиво конечно, но работает
        val h = (num % 1000).toInt() //текущий трехзначный сегмент
        var d = h / 100 //цифра сотен
        if (d > 0) words.append(dig100[d - 1]).append(" ")
        var n = h % 100
        d = n / 10 //цифра десятков
        n %= 10 //цифра единиц
        when (d) {
            0 -> {
            }
            1 -> words.append(dig10[n]).append(" ")
            else -> words.append(dig20[d - 2]).append(" ")
        }
        if (d == 1) n = 0 //при двузначном остатке от 10 до 19, цифра едициц не должна учитываться
        when (n) {
            0 -> {
            }
            1, 2 -> words.append(dig1[sex][n - 1]).append(" ")
            else -> words.append(dig1[0][n - 1]).append(" ")
        }
        when (n) {
            1 -> words.append(leWord[level][0])
            2, 3, 4 -> words.append(leWord[level][1])
            else -> if (h != 0 || h == 0 && level == 1) //если трехзначный сегмент = 0, то добавлять нужно только "рублей"
                words.append(leWord[level][2])
        }
        val nextNum = num / 1000
        return if (nextNum > 0) {
            (num2words(nextNum, level + 1) + " " + words.toString()).trim { it <= ' ' }
        } else {
            words.toString().trim { it <= ' ' }
        }
    }

    //функция преобразования вещественного числа в рубли-копейки
    //при значении money более 50-70 триллионов рублей начинает искажать копейки, осторожней при работе такими суммами
    fun inWords(money: Double): String {
        if (money < 0.0) return "error: отрицательное значение"
        val sm = String.format("%.2f", money)
        val sKop = sm.substring(sm.length - 2, sm.length) //значение копеек в строке
        val iw: Int
        iw = when (sKop.substring(1)) {
            "1" -> 0
            "2", "3", "4" -> 1
            else -> 2
        }
        val num = floor(money).toLong()
        return if (num < 1000000000000000L) {
            num2words(num, 1) + " " + sKop + " " + leWord[0][iw]
        } else "error: слишком много рублей " + sKop + " " + leWord[0][iw]
    }
}