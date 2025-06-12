package com.seumelhorcaminho.todolist.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seumelhorcaminho.todolist.R
import com.seumelhorcaminho.todolist.domain.Category

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawerContent(
    categories: List<Category>,
    onCategorySelected: (Category?) -> Unit,
    onStaticItemSelected: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    onAddCategoryClick: () -> Unit,
    onCategoryLongPress: (Category) -> Unit
) {
    val mainItems = listOf(
        DrawerItem(R.drawable.ic_today_check, "Hoje", { onStaticItemSelected("Hoje") }),
        DrawerItem(R.drawable.ic_calendar, "Próximos", { onStaticItemSelected("Próximos") }),
    )

    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
        Column {
            // Seção de itens estáticos
            mainItems.forEach { item ->
                NavigationDrawerItem(
                    icon = { Icon(painterResource(id = item.iconResId), contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = false,
                    onClick = {
                        item.action()
                        onCloseDrawer()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Item para "Todas as Tarefas"
            NavigationDrawerItem(
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Todas as Tarefas") },
                label = { Text("Todas as Tarefas") },
                selected = false,
                onClick = {
                    onCategorySelected(null)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            categories.forEach { category ->
                NavigationDrawerItem(
                    icon = {
                        Text(
                            text = category.emoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 2.dp, end = 12.dp)
                        )
                    },
                    label = { Text(category.name) },
                    selected = false,
                    onClick = { },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .combinedClickable(
                            onClick = {
                                onCategorySelected(category)
                                onCloseDrawer()
                            },
                            onLongClick = {
                                onCategoryLongPress(category)
                            }
                        )
                )
            }

            // Botão de Adicionar Categoria
            Button(
                onClick = { onAddCategoryClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF21262C),
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
        }
    }
}

data class DrawerItem(
    val iconResId: Int,
    val label: String,
    val action: () -> Unit,
)