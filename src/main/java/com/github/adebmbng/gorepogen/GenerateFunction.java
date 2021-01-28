package com.github.adebmbng.gorepogen;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

public class GenerateFunction extends EditorAction {

    public GenerateFunction() {
        this(new UpHandler());
    }

    protected GenerateFunction(EditorActionHandler defaultHandler) {
        super(defaultHandler);
    }

    private static class UpHandler extends EditorWriteActionHandler {
        private UpHandler() {
        }

        @Override
        public void executeWriteAction(Editor editor, @Nullable Caret caret, DataContext dataContext) {
            Document document = editor.getDocument();

            String extension = Objects.requireNonNull(FileDocumentManager.getInstance().getFile(document)).getExtension();
            if (!(extension != null && extension.toLowerCase().equals("go"))){
                return;
            }

            if (!document.isWritable()) {
                return;
            }

            CaretModel caretModel = editor.getCaretModel();
            SelectionModel selectionModel = editor.getSelectionModel();

            int selectedLineNumber = document.getLineNumber(selectionModel.getSelectionEnd());

            TextRange lineRange = new TextRange(
                    document.getLineStartOffset(selectedLineNumber),
                    document.getLineEndOffset(selectedLineNumber)
            );

            String selectedLine = document.getText().substring(lineRange.getStartOffset(), lineRange.getEndOffset()).trim();

            String[] params = selectedLine.split("\\.");
            if (params.length != 2){
                return;
            }
            if (!document.getText().contains("db *gorm.DB")){
                return;
            }

            String pattern;
            switch (params[1]) {
                case "Create":
                    pattern = "return r.db.Create(&%s).Error";
                    break;
                default:
                    return;
            }

            document.replaceString(
                    lineRange.getStartOffset(),
                    lineRange.getEndOffset(),
                    String.format(pattern, params[0])
            );

            caretModel.moveToOffset(caretModel.getOffset()-2);
        }
    }
}
