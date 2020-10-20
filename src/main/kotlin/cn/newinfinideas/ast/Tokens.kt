package cn.newinfinideas.ast

import com.github.h0tk3y.betterParse.lexer.regexToken

val numeric = regexToken("nm","""\w[\wxXaAbBcCdDeEfF'.]+""")
val linefeed = regexToken("lf", "[\\r\\n]\\s*")
val spacing = regexToken("ws","[^\\S\\r\\n]+", ignore = true)
val comment = regexToken("co", "(?:/\\*[\\s\\S]*?\\*/)|(?://.*)", ignore = true)