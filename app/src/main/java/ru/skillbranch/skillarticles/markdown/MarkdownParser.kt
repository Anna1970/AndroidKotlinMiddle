package ru.skillbranch.skillarticles.markdown

import android.util.Log
import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    //group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP =
        "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val BLOCK_CODE_GROUP = "(^[`]{3}[^`].*?[\\w\\s]+.+[`]{3}\$)"//"(^[`]{3}((.+)\\s)*.+[`]{3}\$)"
    private const val ORDER_LIST_GROUP = "(^[\\d+]\\. .+$)"

    //result regex
    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
            "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP" +
            "|$BLOCK_CODE_GROUP|$ORDER_LIST_GROUP" //optionally

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    /**
     * parse markdown text to elements
     */
    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    /**
     * clear markdown text to string without markdown characters
     */
    fun clear(string: String?): String? {
        if (string.isNullOrEmpty()) return null
        var str:String = string!!

        do {
            val oldstr:String = str
            val  markdownText = parse(str)
            str = ""
            markdownText.elements.forEach {
                str += it.text.toString()
            }
        } while (str != oldstr)

        return  str.trimIndent()
    }

    /**
     * find markdown elements in markdown text
     */
    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            //if something is found then everything before - TEXT
            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }

            //found text
            var text : CharSequence

            //group range for iterate by groups
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                //NOT FOUND -> BREAK
                -1 -> break@loop

                //UNORDERED LIST
                1 -> {
                    //text without "*. "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    //find inner elements
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)

                    //next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    //text without "{#} "
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)

                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //QUOTE
                3 -> {
                    //text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelement = findElements(text)
                    val element = Element.Quote(text, subelement)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //ITALIC
                4 -> {
                    //text without "*{}*" and "_{}_"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelement = findElements(text)
                    val element = Element.Italic(text, subelement)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //BOLD
                5 -> {
                    //text without "**{}**" and "__{}__"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelement = findElements(text)
                    val element = Element.Bold(text, subelement)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //STRIKE
                6 -> {
                    //text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements = findElements(text)
                    val element = Element.Strike(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //RULE
                7 -> {
                    //text without "***" insert empty character
                    val element = Element.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //INLINE
                8 -> {
                    //text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val element = Element.InlineCode(text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //LINK
                9 -> {
                    //full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title:String, link:String) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    val element = Element.Link(link, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //10 -> BLOCK CODE - optionally
                10 -> {
                    //text without "```{}```"

                    text = string.subSequence(startIndex.plus(3), endIndex.plus(-3))

                    val multistring = text.lines()
                    //lateinit var element:Element.BlockCode

                    multistring.forEachIndexed {index: Int, str: String ->
                        val subelements = findElements(str)
                        val element = when {
                            multistring.count() == 1 -> Element.BlockCode(Element.BlockCode.Type.SINGLE, str, subelements)
                            index == 0 -> Element.BlockCode(Element.BlockCode.Type.START, str + "\n", subelements)
                            index == multistring.lastIndex -> Element.BlockCode(Element.BlockCode.Type.END, str , subelements)
                            else -> Element.BlockCode(Element.BlockCode.Type.MIDDLE, str + "\n", subelements)
                        }
                        parents.add(element)
                    }
                    lastStartIndex = endIndex
                }

                //11 -> NUMERIC LIST
                11 -> {
                    text = string.subSequence(startIndex, endIndex)

                    //find inner elements
                    val order = "^[\\d]+\\.".toRegex().find(text)!!.value
                    text = string.subSequence(startIndex.plus(order.length + 1), endIndex)
                    val subelement = findElements(text)

                    val element = Element.OrderedListItem(
                        order,
                        text,
                        subelement
                    )
                    parents.add(element)
                    lastStartIndex = endIndex
                }

            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }

        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element() {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ", //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence, //for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }
}