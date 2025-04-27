package riqinho.com.github.cryptomonitor.model

class Ticker(
    val high: String,
    val low: String,
    val vol: String,
    val last: String,
    val buy: String,
    val sell: String,
    val date: Long
)

class TickerResponse(
    val ticker: Ticker
)
