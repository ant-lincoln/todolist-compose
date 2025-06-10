package com.seumelhorcaminho.todolist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seumelhorcaminho.todolist.R

data class DrawerItem(
    val iconResId: Int?,
    val emoji: String?,
    val label: String,
    val action: () -> Unit,
    val isCategory: Boolean = false
)


@Composable
fun AppDrawerContent(
    onItemSelected: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    onAddCategoryClick: () -> Unit
) {
    val mainItems = listOf(
        DrawerItem(R.drawable.ic_today_check, null, "Hoje", { onItemSelected("Hoje") }),
        DrawerItem(R.drawable.ic_calendar, null, "Próximos", { onItemSelected("Próximos") }),
    )

    val categoryItems = listOf(
        DrawerItem(null, "📚", "Estudos", { onItemSelected("Estudos") }, true),
        DrawerItem(null, "💼", "Trabalho", { onItemSelected("Trabalho") }, true),
        DrawerItem(null, "🛍️", "Compras", { onItemSelected("Compras") }, true),
        DrawerItem(null, "🩺", "Saúde", { onItemSelected("Saúde") }, true),
        DrawerItem(null, "🏅", "Treino", { onItemSelected("Treino") }, true),
        DrawerItem(null, "🎵", "Música", { onItemSelected("Música") }, true),
    )

    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
        Column(modifier = Modifier.fillMaxHeight()) {

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            //Itens de categoria
            categoryItems.forEach { item ->
                NavigationDrawerItem(
                    icon = {
                        if (item.isCategory && item.emoji != null) {
                            Text(
                                text = item.emoji,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 2.dp, end = 12.dp)
                            )
                        } else if (item.iconResId != null) {
                            Icon(
                                painterResource(id = item.iconResId),
                                contentDescription = item.label
                            )
                        } else {
                            Spacer(Modifier.width(24.dp + 12.dp))
                        }


                    },
                    label = { Text(item.label) },
                    selected = false,
                    onClick = {
                        item.action()
                        onCloseDrawer()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)

                )
            }

            Button(
                onClick = {
                    onAddCategoryClick()
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_add_category),
                    contentDescription = "Add categoria"
                )
                Spacer(Modifier.width(8.dp))
                Text("Add categoria", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        }
    }
}