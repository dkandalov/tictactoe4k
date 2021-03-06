
import Player.*
import datsok.*
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ClientFilters.FollowRedirects
import org.http4k.testing.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*

@ExtendWith(ApprovalTest::class)
class FrontendTests {
    private val frontend = FollowRedirects().then(
        newFrontend(newBackend(Game()))
    )

    @Test fun `state of an empty game`(approver: Approver) {
        val response = frontend(Request(GET, "/")).expectOK()
        approver.assertApproved(response)
    }

    @Test fun `players take turns on each move`(approver: Approver) {
        frontend(Request(GET, "/move/0/1")).expectOK()
        frontend(Request(GET, "/move/2/0")).expectOK()
        val response = frontend(Request(GET, "/move/1/1")).expectOK()
        approver.assertApproved(response)
    }

    @Test fun `player X wins`(approver: Approver) {
        val frontend = newFrontend(newBackend(finishedGame))
        approver.assertApproved(frontend(Request(GET, "/")).expectOK())
    }
}


class BackendTests {
    private val backend = newBackend(Game())

    @Test fun `state of an empty game`() {
        val response = backend(Request(GET, "/game")).expectOK()
        response.bodyString() shouldEqual "{\"moves\":[]}"
    }

    @Test fun `players take turns on each move`() {
        backend(Request(POST, "/game?x=0&y=1")).expectOK()
        backend(Request(POST, "/game?x=2&y=0")).expectOK()
        backend(Request(POST, "/game?x=1&y=1")).expectOK()

        val response = backend(Request(GET, "/game")).expectOK()
        gameLens.extract(response) shouldEqual Game(moves = listOf(
            Move(0, 1, X),
            Move(2, 0, O),
            Move(1, 1, X),
        ))
    }

    @Test fun `player X wins`() {
        val backend = newBackend(finishedGame)
        gameLens(backend(Request(GET, "/game"))).winner shouldEqual X
    }
}

class GameTests {
    // ...
}

private val finishedGame = Game()
    .makeMove(0, 0).makeMove(1, 0)
    .makeMove(0, 1).makeMove(1, 1)
    .makeMove(0, 2)

private fun Response.expectOK(): Response {
    status shouldEqual OK
    return this
}
