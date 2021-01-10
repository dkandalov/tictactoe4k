
import Player.*
import org.http4k.client.*
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.filter.ServerFilters.CatchAll
import org.http4k.format.Jackson.auto
import org.http4k.lens.*
import org.http4k.routing.*
import org.http4k.server.*
import org.http4k.template.*

fun main() {
    newBackend(Game()).asServer(ApacheServer(port = 1234)).start()

    val backendClient = SetBaseUriFrom(Uri.of("http://localhost:1234")).then(OkHttp())
    newFrontend(backendClient).asServer(ApacheServer(port = 8080)).start()

    println("Started on http://localhost:8080")
}

val gameLens = Body.auto<Game>().toLens()
val xLens = Query.int().required("x")
val yLens = Query.int().required("y")

fun newFrontend(backend: HttpHandler): HttpHandler {
    val htmlRenderer = HandlebarsTemplates().HotReload("src/test")
    return routes(
        "/" bind GET to {
            val game = gameLens(backend(Request(GET, "/game")))
            Response(OK).body(htmlRenderer(game.toGameView()))
        },
        "/move/{x}/{y}" bind GET to { request ->
            val x = request.path("x")
            val y = request.path("y")
            backend(Request(POST, "/game?x=$x&y=$y"))
            Response(SEE_OTHER).header("Location", "/")
        }
    ).withFilter(PrintRequestAndResponse().then(CatchAll()))
}

private fun Game.toGameView() = GameView(
    rows = (0..2).map { x ->
        (0..2).map { y ->
            val player = moves.find { it.x == x && it.y == y }?.player?.name
            CellView(x, y, player)
        }
    },
    winner = winner?.name
)

class GameView(
    val rows: List<List<CellView>>,
    val winner: String?
): ViewModel

class CellView(val x: Int, val y: Int, val player: String?)


fun newBackend(initialGame: Game): HttpHandler {
    var game = initialGame
    return routes(
        "/game" bind GET to {
            gameLens.inject(game, Response(OK))
        },
        "/game" bind POST to { request ->
            val x = xLens.extract(request)
            val y = yLens.extract(request)

            game = game.makeMove(x, y)

            Response(OK)
        },
    ).withFilter(PrintRequestAndResponse().then(CatchAll()))
}

data class Game(val moves: List<Move> = emptyList()) {
    val winner: Player? = findWinner()

    fun makeMove(x: Int, y: Int): Game {
        if (winner != null) return this
        val player = if (moves.lastOrNull()?.player != X) X else O
        return copy(moves = moves + Move(x, y, player))
    }

    private fun findWinner(): Player? {
        return enumValues<Player>().find { player ->
            moves.containsAll((0..2).map { Move(it, 0, player) }) ||
            moves.containsAll((0..2).map { Move(it, 1, player) }) ||
            moves.containsAll((0..2).map { Move(it, 2, player) }) ||
            moves.containsAll((0..2).map { Move(0, it, player) }) ||
            moves.containsAll((0..2).map { Move(1, it, player) }) ||
            moves.containsAll((0..2).map { Move(2, it, player) }) ||
            moves.containsAll((0..2).map { Move(it, it, player) }) ||
            moves.containsAll((0..2).map { Move(it, 2 - it, player) })
        }
    }
}

data class Move(val x: Int, val y: Int, val player: Player)

enum class Player { X, O }
