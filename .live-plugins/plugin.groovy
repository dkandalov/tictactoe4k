import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

import static liveplugin.PluginUtil.*

registerAction("tictactoe4kProjectPopup", "ctrl shift K") { AnActionEvent event ->
    def project = event.project
    def popupMenuDescription = [
            "import auto" : { pasteImportAuto(project) },
            "body"        : { pasteBodyStyle(project) },
            "body content": { pasteBody(project) },
    ]
    showPopupMenu(popupMenuDescription, "")
}

static pasteImportAuto(Project project) {
    def document = currentDocumentIn(project)
    def editor = currentEditorIn(project)
    runDocumentWriteAction(project, document) {
        document.insertString(editor.caretModel.offset, "import org.http4k.format.Jackson.auto")
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
                """{{#each rows~}}
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
{{~/if~}}""").trim()
    }
}

if (!isIdeStartup) show("Reloaded http4k popup")
