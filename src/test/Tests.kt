import Player.*
import datsok.*
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.*
import org.http4k.testing.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*

@ExtendWith(ApprovalTest::class)
class FrontendTests {
    private val frontend =
        ClientFilters.FollowRedirects().then(newFrontend(newBackend()))

    @Test fun `state of an empty game`(approver: Approver) {
        val response = frontend(Request(GET, "/")).expectOK()
        approver.assertApproved(response)
    }

    @Test fun `players take turn on each move`(approver: Approver) {
        frontend(Request(GET, "/move/0/1")).expectOK()
        frontend(Request(GET, "/move/1/0")).expectOK()
        frontend(Request(GET, "/move/2/2")).expectOK()

        val response = frontend(Request(GET, "/")).expectOK()
        approver.assertApproved(response)
    }
}

class BackendTests {
    private val backend = newBackend()

    @Test fun `state of an empty game`() {
        val response = backend(Request(GET, "/game")).expectOK()
        response.bodyString() shouldEqual "{\"moves\":[]}"
    }

    @Test fun `players take turn on each move`() {
        backend(Request(POST, "/game?x=0&y=1")).expectOK()
        backend(Request(POST, "/game?x=1&y=0")).expectOK()
        backend(Request(POST, "/game?x=2&y=2")).expectOK()

        val response = backend(Request(GET, "/game")).expectOK()
        gameLens.extract(response) shouldEqual Game(
            listOf(
                Move(0, 1, X),
                Move(1, 0, O),
                Move(2, 2, X),
            )
        )
    }
}

private fun Response.expectOK(): Response {
    this.status shouldEqual OK
    return this
}

class GameTests {
    // ...
}