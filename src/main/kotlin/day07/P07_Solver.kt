package day07

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P07_Solver().solve(INPUT_VARIANT.REAL)
}

class P07_Solver : BaseSolver() {

    // answer: 251136060
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val hands = inputLines
            .map {
                val elements = it.split(" ")
                val hand = elements[0].map { CardType[it]!! }
                HandAndBid(hand, elements[1].toInt(), determineHandType(hand))
            }
            .sortedWith(compareBy<HandAndBid> { it.handType.strength }
                .thenBy { it.hand[0].value1 }
                .thenBy { it.hand[1].value1 }
                .thenBy { it.hand[2].value1 }
                .thenBy { it.hand[3].value1 }
                .thenBy { it.hand[4].value1 })
        hands.forEachIndexed { index, handAndBit -> handAndBit.rank = (index + 1).toLong() }

        return hands.map { it.bid * it.rank }.sum()
    }

    // answer: 249400220
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val hands = inputLines
            .map {
                val elements = it.split(" ")
                val hand = elements[0].map { CardType[it]!! }
                HandAndBid(hand, elements[1].toInt(), determineHandType(hand, true))
            }
            .sortedWith(compareBy<HandAndBid> { it.handType.strength }
                .thenBy { it.hand[0].value2 }
                .thenBy { it.hand[1].value2 }
                .thenBy { it.hand[2].value2 }
                .thenBy { it.hand[3].value2 }
                .thenBy { it.hand[4].value2 })
        hands.forEachIndexed { index, handAndBit -> handAndBit.rank = (index + 1).toLong() }

        return hands.map { it.bid * it.rank }.sum()
    }

    fun determineHandType(hand: List<CardType>, replaceJoker:Boolean = false): HandType {
        val mappedHand = mutableMapOf<CardType, Int>()
        hand.forEach {
            if (!mappedHand.containsKey(it)) {
                mappedHand[it] = 0
            }
            mappedHand[it] = mappedHand[it]?.plus(1)!!
        }

        if (replaceJoker) {
            // replace the Joker by the card type which occurs most in the current hand
            if (mappedHand.containsKey(CardType.J)) {
                val jokerStandIn: CardType? =
                    mappedHand.entries.filter { it.key != CardType.J }.maxByOrNull { it.value }?.key
                if (jokerStandIn != null) {
                    mappedHand[jokerStandIn] = mappedHand[jokerStandIn]!! + mappedHand[CardType.J]!!
                    mappedHand.remove(CardType.J)
                }
            }
        }

        return when (mappedHand.size) {
            1 -> HandType.FIVE_KIND
            2 ->
                if (mappedHand.filter { it.value == 4 }.count() == 1) {
                    HandType.FOUR_KIND
                } else {
                    HandType.FULL_HOUSE
                }

            3 ->
                if (mappedHand.filter { it.value == 3 }.count() == 1) {
                    HandType.THREE_KIND
                } else {
                    HandType.TWO_PAIR
                }

            4 -> HandType.ONE_PAIR
            else -> HandType.HIGH_CARD
        }
    }
}

data class HandAndBid(val hand:List<CardType>, val bid:Int, val handType:HandType, var rank: Long = 0)


enum class CardType(val label:Char, val value1:Int, val value2:Int) {
    A('A', 14, 14),
    K('K', 13, 13),
    Q('Q', 12, 12),
    J('J', 11, 1),
    T('T', 10, 10),
    NINE('9', 9, 9),
    EIGHT('8', 8, 8),
    SEVEN('7', 7, 7),
    SIX('6', 6, 6),
    FIVE('5', 5, 5),
    FOUR('4', 4, 4),
    THREE('3', 3, 3),
    TWO('2', 2, 2);

    companion object {
        private val map:Map<Char, CardType> = CardType.values().associateBy { it.label }
        operator fun get(label: Char) = map[label]
    }
}

enum class HandType (val strength:Int){
    FIVE_KIND(7),
    FOUR_KIND(6),
    FULL_HOUSE(5),
    THREE_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1)
}


