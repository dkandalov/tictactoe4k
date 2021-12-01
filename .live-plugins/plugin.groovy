import com.intellij.execution.filters.ConsoleInputFilterProvider
import com.intellij.execution.filters.InputFilter
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import org.jetbrains.annotations.NotNull

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
        val nextPlayer = if (moves.lastOrNull()?.player == Player.X) Player.O else Player.X
        return copy(moves = moves + Move(x, y, nextPlayer))
    }

    private fun findWinner(): Player? =
        enumValues<Player>().find { player ->
            moves.containsAll((0..2).map { Move(x = it, y = 0, player) }) ||
            moves.containsAll((0..2).map { Move(x = it, y = 1, player) }) ||
            moves.containsAll((0..2).map { Move(x = it, y = 2, player) }) ||
            moves.containsAll((0..2).map { Move(x = 0, y = it, player) }) ||
            moves.containsAll((0..2).map { Move(x = 1, y = it, player) }) ||
            moves.containsAll((0..2).map { Move(x = 2, y = it, player) }) ||
            moves.containsAll((0..2).map { Move(x = it, y = it, player) }) ||
            moves.containsAll((0..2).map { Move(x = it, y = 2 - it, player) })
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

static registerConsoleFilter(Disposable disposable, InputFilter inputFilter) {
    def inputFilterProvider = new ConsoleInputFilterProvider() {
        @Override InputFilter[] getDefaultFilters(@NotNull Project project) {
            [inputFilter]
        }
    }
    def extensionPoint = Extensions.rootArea.getExtensionPoint(ConsoleInputFilterProvider.INPUT_FILTER_PROVIDERS)
    extensionPoint.registerExtension(inputFilterProvider, LoadingOrder.FIRST, disposable)
    inputFilterProvider
}

registerConsoleFilter(pluginDisposable, new InputFilter() {
    @Override List<Pair<String, ConsoleViewContentType>> applyFilter(String consoleText, ConsoleViewContentType contentType) {
        if (consoleText.startsWith("/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk")) {
            [new Pair("", ConsoleViewContentType.SYSTEM_OUTPUT)]
        } else if (consoleText.startsWith("/Users/dima/Library/Java/JavaVirtualMachines/openjdk-15.0.2")) {
            [new Pair("", ConsoleViewContentType.SYSTEM_OUTPUT)]
        } else if (consoleText.contains("Process finished with exit code")) {
            [new Pair("", ConsoleViewContentType.SYSTEM_OUTPUT)]
        } else {
            null
        }
    }
})

if (!isIdeStartup) show("Reloaded tictactoe4k actions")
