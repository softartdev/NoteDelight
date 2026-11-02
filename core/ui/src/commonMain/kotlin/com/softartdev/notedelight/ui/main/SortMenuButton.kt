package com.softartdev.notedelight.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.notedelight.model.NoteSort
import com.softartdev.notedelight.presentation.main.MainAction
import com.softartdev.notedelight.presentation.main.NoteListResult
import com.softartdev.notedelight.ui.TooltipIconButton
import com.softartdev.notedelight.util.MAIN_SORT_BUTTON_TAG
import com.softartdev.notedelight.util.sortOptionTestTag
import notedelight.core.ui.generated.resources.Res
import notedelight.core.ui.generated.resources.sort
import org.jetbrains.compose.resources.stringResource

@Composable
fun SortMenuButton(
    noteListResult: NoteListResult,
    onAction: (MainAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val successState = noteListResult as? NoteListResult.Success ?: return
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        TooltipIconButton(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            onClick = { expanded = true },
            testTag = MAIN_SORT_BUTTON_TAG,
            label = stringResource(Res.string.sort),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SortMenuItems(
                currentSort = successState.noteSort,
                onSortSelected = { noteSort ->
                    expanded = false
                    onAction(MainAction.OnSortChange(noteSort))
                },
            )
        }
    }
}

@Composable
internal fun SortMenuItems(
    currentSort: NoteSort,
    onSortSelected: (NoteSort) -> Unit,
) {
    SortOption.entries.forEach { option ->
        val selected: Boolean = option.noteSort == currentSort
        DropdownMenuItem(
            modifier = Modifier.testTag(sortOptionTestTag(option.noteSort)),
            text = { Text(text = stringResource(option.labelRes)) },
            onClick = { onSortSelected(option.noteSort) },
            trailingIcon = if (selected) {
                { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
            } else {
                null
            },
        )
    }
}

@Preview
@Composable
fun PreviewSortMenu() = Surface {
    Column {
        SortMenuItems(
            currentSort = NoteSort(),
            onSortSelected = {},
        )
    }
}
