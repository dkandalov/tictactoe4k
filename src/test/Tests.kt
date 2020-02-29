
import Player.*
import kotlincommon.test.*
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ClientFilters.FollowRedirects
import org.http4k.testing.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*

@ExtendWith(ApprovalTest::class)
class GameFrontendTests {
    private val frontend = newGameFrontend(newGameApp(Game().move(1, 1)))
        .with(FollowRedirects())

    @Test fun `get game state`(approver: Approver) {
        val response = frontend(Request(GET, "/")).expectOK()
        approver.assertApproved(response)
    }

    @Test fun `players make moves`(approver: Approver) {
        frontend(Request(GET, "/move/0/1")).expectOK()
        frontend(Request(GET, "/move/2/0")).expectOK()

        approver.assertApproved(frontend(Request(GET, "/")).expectOK())
    }
}

class GameAppTests {
    private val gameApp = newGameApp(Game())

    @Test fun `get game state`() {
        val response = gameApp(Request(GET, "/game")).expectOK()
        response.bodyString() shouldEqual "{\"moves\":[],\"winner\":null}"
    }

    @Test fun `players make moves`() {
        gameApp(Request(PUT, "/game?x=0&y=1")).expectOK()
        gameApp(Request(PUT, "/game?x=2&y=0")).expectOK()

        val response = gameApp(Request(GET, "/game")).expectOK()
        gameLens(response) shouldEqual Game(moves = listOf(
            Move(0, 1, X),
            Move(2, 0, O)
        ))
    }
}

class GameTests {
    // ...
}

private fun Response.expectOK(): Response {
    status shouldEqual OK
    return this
}
