package com.example.myapplication1

import kotlin.math.pow
import kotlin.math.sqrt

class Calculate {

    fun getResult (str: String):Double  {

        val lexemes = lexAnalyze(str)
        val lexemeBuffer = LexemeBuffer(lexemes)
        return (expr(lexemeBuffer))
    }

    fun lexAnalyze(expText: String): List<Lexeme> {
        val lexemes = ArrayList<Lexeme>()
        var pos = 0
        while (pos < expText.length) {
            var c = expText[pos]
            when (c) {
                '(' -> {
                    lexemes.add(Lexeme(LexemeType.LEFT_BRACKET, c))
                    pos++
                    continue
                }
                ')' -> {
                    lexemes.add(Lexeme(LexemeType.RIGHT_BRACKET, c))
                    pos++
                    continue
                }
                '^' -> {
                    lexemes.add(Lexeme(LexemeType.DEGREE, c))
                    pos++
                    continue
                }
                '+' -> {
                    lexemes.add(Lexeme(LexemeType.OP_PLUS, c))
                    pos++
                }
                '-' -> {
                    lexemes.add(Lexeme(LexemeType.OP_MINUS, c))
                    pos++
                    continue
                }
                '*' -> {
                    lexemes.add(Lexeme(LexemeType.OP_MUL, c))
                    pos++
                    continue
                }
                '/' -> {
                    lexemes.add(Lexeme(LexemeType.OP_DIV, c))
                    pos++
                    continue
                }
                '√' -> {
                    lexemes.add(Lexeme(LexemeType.NUMBER, "1"))
//                    lexemes.add(Lexeme(LexemeType.OP_MUL, "*"))
                    lexemes.add(Lexeme(LexemeType.ROOT, c))
                    pos++
                    continue
                }
                else -> if (c <= '9' && c >= '0' || c == '.') {
                    val sb = StringBuilder()
                    do {
                        sb.append(c)
                        pos++
                        if (pos >= expText.length) {
                            break
                        }
                        c = expText[pos]
                    } while (c <= '9' && c >= '0' || c == '.')
                    lexemes.add(Lexeme(LexemeType.NUMBER, sb.toString()))
                } else {
                    if (c != ' ') {
                        throw RuntimeException("Unexpected character: $c")
                    }
                    pos++
                }
            }
        }
        lexemes.add(Lexeme(LexemeType.EOF, ""))
        return lexemes
    }

    fun expr(lexemes: LexemeBuffer): Double {
        val lexeme = lexemes.next()
        if (lexeme.type == LexemeType.EOF) {
            return 0.0
        } else {
            lexemes.back()
            return plusminus(lexemes)
        }
    }

    fun plusminus(lexemes: LexemeBuffer): Double {
        var value:Double = multdiv(lexemes)
        while (true) {
            val lexeme = lexemes.next()
            when (lexeme.type) {
                LexemeType.OP_PLUS -> value += multdiv(lexemes)
                LexemeType.OP_MINUS -> value -= multdiv(lexemes)
                LexemeType.EOF, LexemeType.RIGHT_BRACKET -> {
                    lexemes.back()
                    return value
                }
                else -> throw RuntimeException(
                    "Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.pos
                )
            }
        }
    }

    fun multdiv(lexemes: LexemeBuffer): Double {
        var value:Double = degree(lexemes)
        while (true) {
            val lexeme = lexemes.next()
            when (lexeme.type) {
                LexemeType.OP_MUL -> value *= degree(lexemes)
                LexemeType.OP_DIV -> value /= degree(lexemes)
                LexemeType.EOF, LexemeType.RIGHT_BRACKET, LexemeType.OP_PLUS, LexemeType.OP_MINUS -> {
                    lexemes.back()
                    return value
                }
                else -> throw RuntimeException(
                    ("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.pos)
                )
            }
        }
    }

    fun degree(lexemes: LexemeBuffer): Double {
        var value: Double = root(lexemes)
        while (true) {
            val lexeme = lexemes.next()
            when (lexeme.type) {
                LexemeType.DEGREE -> value = value.pow(root(lexemes))
                LexemeType.EOF, LexemeType.RIGHT_BRACKET, LexemeType.OP_PLUS, LexemeType.OP_MINUS, LexemeType.OP_MUL, LexemeType.OP_DIV -> {
                    lexemes.back()
                    return value
                }
                else -> throw RuntimeException(
                    ("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.pos)
                )
            }
        }
    }

    fun root(lexemes: LexemeBuffer): Double {
        var value: Double = factor(lexemes)
        while (true) {
            val lexeme = lexemes.next()
            when (lexeme.type) {
                LexemeType.ROOT -> value *= sqrt(factor(lexemes))
                LexemeType.EOF, LexemeType.RIGHT_BRACKET, LexemeType.OP_PLUS, LexemeType.OP_MINUS, LexemeType.DEGREE,  LexemeType.OP_MUL, LexemeType.OP_DIV -> {
                    lexemes.back()
                    return value
                }
                else -> throw RuntimeException(
                    ("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.pos)
                )
            }
        }
    }


    fun factor(lexemes: LexemeBuffer): Double {
        var lexeme = lexemes.next()
        when (lexeme.type) {
            LexemeType.NUMBER -> return lexeme.value.toDouble()
            LexemeType.LEFT_BRACKET -> {
                val value:Double = plusminus(lexemes)
                lexeme = lexemes.next()
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw RuntimeException(
                        ("Unexpected token: " + lexeme.value
                                + " at position: " + lexemes.pos)
                    )
                }
                return value
            }
            else -> throw RuntimeException(
                ("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.pos)
            )
        }
    }

    enum class LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET, ROOT, DEGREE, OP_PLUS, OP_MINUS, OP_MUL, OP_DIV, NUMBER, EOF
    }

    class Lexeme {
        var type: LexemeType
        var value: String

        constructor(type: LexemeType, value: String) {
            this.type = type
            this.value = value
        }

        constructor(type: LexemeType, value: Char) {
            this.type = type
            this.value = value.toString()
        }

        override fun toString(): String {
            return ("Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}')
        }
    }

    class LexemeBuffer(var lexemes: List<Lexeme>) {
        var pos = 0
            private set

        operator fun next(): Lexeme {
            return lexemes[pos++]
        }

        fun back() {
            pos--
        }

    }
}