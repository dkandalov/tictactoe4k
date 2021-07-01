import com.intellij.openapi.project.Project

import static liveplugin.PluginUtil.*

registerAction("tictactoe4kProjectPopup-1", "ctrl shift K, 1", null, "", pluginDisposable) {
    pasteImportAuto(it.project)
}
registerAction("tictactoe4kProjectPopup-2", "ctrl shift K, 2", null, "", pluginDisposable) {
    pasteGameCode(it.project)
}
//registerAction("tictactoe4kProjectPopup-3", "ctrl shift K, 3", null, "", pluginDisposable) {
//    pasteBodyStyle(it.project)
//}
registerAction("tictactoe4kProjectPopup-3", "ctrl shift K, 3", null, "", pluginDisposable) {
    pasteGameViewCode(it.project)
}
registerAction("tictactoe4kProjectPopup-4", "ctrl shift K, 4", null, "", pluginDisposable) {
    pasteBody(it.project)
}

static pasteImportAuto(Project project) {
    def document = currentDocumentIn(project)
    def editor = currentEditorIn(project)
    runDocumentWriteAction(project, document) {
        document.insertString(editor.caretModel.offset, "import org.http4k.format.Moshi.autoBody")
    }
}

static pasteGameCode(Project project) {
    def document = currentDocumentIn(project)
    def editor = currentEditorIn(project)
    runDocumentWriteAction(project, document) {
        document.insertString(editor.caretModel.offset,
                """    val winner: Player? = findWinner()

    fun makeMove(x: Int, y: Int): Game {
        if (winner != null) return this
        val nextPlayer = if (moves.lastOrNull()?.player != Player.X) Player.X else Player.O
        return copy(moves = moves + Move(x, y, nextPlayer))
    }

    private fun findWinner(): Player? =
        enumValues<Player>().find { player ->
            moves.containsAll((0..2).map { Move(it, 0, player) }) ||
            moves.containsAll((0..2).map { Move(it, 1, player) }) ||
            moves.containsAll((0..2).map { Move(it, 2, player) }) ||
            moves.containsAll((0..2).map { Move(0, it, player) }) ||
            moves.containsAll((0..2).map { Move(1, it, player) }) ||
            moves.containsAll((0..2).map { Move(2, it, player) }) ||
            moves.containsAll((0..2).map { Move(it, it, player) }) ||
            moves.containsAll((0..2).map { Move(it, 2 - it, player) })
        }\n"""
        )
    }
}

static pasteBodyStyle(Project project) {
    def document = currentDocumentIn(project)
    def editor = currentEditorIn(project)
    runDocumentWriteAction(project, document) {
        document.insertString(
                editor.caretModel.offset,
                """<meta charset="UTF-8">\n<body style="font-family: 'Menlo',serif; font-size: 70px;">\n</body>"""
        )
    }
}

static pasteBody(Project project) {
    def document = currentDocumentIn(project)
    def editor = currentEditorIn(project)
    runDocumentWriteAction(project, document) {
        document.insertString(
                editor.caretModel.offset,
                """<meta charset="UTF-8">
<body style="font-family: 'Menlo',serif; font-size: 70px;">
{{#each rows~}}
    <p>
        {{#each this}}
            {{~#if player~}}
                {{player}}
            {{~else~}}
                <a href="/move/{{x}}/{{y}}">_</a>
            {{~/if~}}
            &nbsp;
        {{/each}}
    </p>
{{~/each}}
{{#if winner~}}
    {{winner}} wins!! 🥳
{{~/if~}}
</body>""")
    }
}

static pasteGameViewCode(Project project) {
    def document = currentDocumentIn(project)
    def editor = currentEditorIn(project)
    runDocumentWriteAction(project, document) {
        document.insertString(editor.caretModel.offset,
                """class GameView(val rows: List<List<CellView>>, val winner: String?) : ViewModel

class CellView(val x: Int, val y: Int, val player: String?)

private fun Game.toGameView() = GameView(
    rows = (0..2).map { x ->
        (0..2).map { y ->
            val player = moves.find { it.x == x && it.y == y }?.player?.name
            CellView(x, y, player)
        }
    },
    winner = winner?.name
)
"""
        )
    }
}

if (!isIdeStartup) show("Reloaded tictactoe4k popup")
