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
    val filters = PrintRequestAndResponse().then(CatchAll())
    filters.then(newGameApp(Game())).asServer(Jetty(port = 1234)).start()

    val gameAppClient = OkHttp().with(SetBaseUriFrom(Uri.of("http://localhost:1234")))
    newGameFrontend(gameAppClient).asServer(Jetty(port = 8080)).start()
}

val gameLens: BiDiBodyLens<Game> = Body.auto<Game>().toLens()
val xLens = Query.int().required("x")
val yLens = Query.int().required("y")

class CellView(val x: Int, val y: Int, val player: String?)
class GameView(val rows: List<List<CellView>>, val winner: String?): ViewModel

private fun Game.toView() = GameView(
    rows = (0..2).map { x ->
        (0..2).map { y ->
            val player = moves.find { it.x == x && it.y == y}?.player?.name
            CellView(x, y, player)
        }
    },
    winner = winner?.name
)

fun newGameFrontend(gameApp: HttpHandler): HttpHandler {
    val renderer = HandlebarsTemplates().HotReload("src/test")
    return routes(
        "/" bind GET to {
            val game = gameLens(gameApp(Request(GET, "/game")))
            Response(OK).body(renderer(game.toView()))
        },
        "/move/{x}/{y}" bind GET to { request ->
            val x = request.path("x")
            val y = request.path("y")
            gameApp(Request(PUT, "/game?x=$x&y=$y"))
            Response(SEE_OTHER).header("Location", "/")
        }
    )
}

fun newGameApp(initialGame: Game): HttpHandler {
    var game = initialGame
    return routes(
        "/game" bind GET to {
            Response(OK).with(gameLens of game)
        },
        "/game" bind PUT to { request ->
            val x = xLens(request)
            val y = yLens(request)
            game = game.move(x, y)
            Response(OK)
        }
    )
}

enum class Player { X, O }

data class Move(val x: Int, val y: Int, val player: Player)

data class Game(val moves: List<Move> = emptyList()) {
    val winner: Player? = findWinner()

    fun move(x: Int, y: Int): Game {
        val player = if (moves.lastOrNull()?.player == X) O else X
        val newMove = Move(x, y, player)
        return copy(moves = moves + newMove)
    }

    private fun findWinner(): Player? {
        return Player.values().find { player ->
            (0..2).all { moves.contains(Move(it, 0, player)) } ||
            (0..2).all { moves.contains(Move(it, 1, player)) } ||
            (0..2).all { moves.contains(Move(it, 2, player)) } ||
            (0..2).all { moves.contains(Move(0, it, player)) } ||
            (0..2).all { moves.contains(Move(1, it, player)) } ||
            (0..2).all { moves.contains(Move(2, it, player)) } ||
            (0..2).all { moves.contains(Move(it, it, player)) } ||
            (0..2).all { moves.contains(Move(it, 2 - it, player)) }
        }
    }
}

