package com.darkempire78.opencalculator

import java.math.BigInteger
import kotlin.math.*

class Calculator {

    fun factorial(number: Double): Double {
        var factorial = BigInteger("1")
        for (i in 1..number.toInt()) {
            factorial *= i.toBigInteger()
        }
        return factorial.toDouble()
    }
    fun evaluate(equation: String, isDegreeModeActivated: Boolean): Double {
        // https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < equation.length) equation[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < equation.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return +parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus
                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    if (!eat(')'.code)) throw RuntimeException("Missing ')'")
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = equation.substring(startPos, pos).toDouble()
                } else if (eat('e'.code)) {
                    x = exp(1.0)
                } else if (eat('π'.code)) {
                        x = Math.PI
                } else if (ch >= 'a'.code && ch <= 'z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func: String = equation.substring(startPos, pos)
                    if (eat('('.code)) {
                        x = parseExpression()
                        if (!eat(')'.code)) throw RuntimeException("Missing ')' after argument to $func")
                    } else {
                        x = parseFactor()
                    }
                    when (func) {
                        "sqrt" -> x = sqrt(x)
                        "ln" -> x = ln(x)
                        "logten" -> x = log10(x)
                        "exp" -> x = exp(x)
                        "factorial" -> x = factorial(x)
                        "sin" -> x = if (isDegreeModeActivated) {
                            sin(Math.toRadians(x))
                        } else {
                            sin(x)
                        }
                        "cos" -> x = if (isDegreeModeActivated) {
                            cos(Math.toRadians(x))
                        } else {
                            cos(x)
                        }
                        "tan" -> x = if (isDegreeModeActivated) {
                            tan(Math.toRadians(x))
                        } else {
                            tan(x)
                        }
                        "arcsin" -> x = if (isDegreeModeActivated) {
                            asin(Math.toRadians(x))
                        } else {
                            asin(x)
                        }
                        "arccos" -> x = if (isDegreeModeActivated) {
                            acos(Math.toRadians(x))
                        } else {
                            acos(x)
                        }
                        "arctan" -> x = if (isDegreeModeActivated) {
                            atan(Math.toRadians(x))
                        } else {
                            atan(x)
                        }
                        else -> x = Double.NaN
                    }
                } else {
                    x = Double.NaN
                }
                if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation
                return x
            }
        }.parse()
    }
}