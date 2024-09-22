package com.tifd.projectcomposed

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MyScreen() {
    var text by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isFormFilled = inputText.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.height(16.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.cat_icon_besar),
                contentDescription = "Icon Profile",
                modifier = Modifier.size(70.dp),
//                colorFilter = ColorFilter.tint(Color.Black),
                contentScale = ContentScale.Inside// Optional: Tint the image if needed
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it},
                label = { Text("Masukkan nama kucing")},
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                Toast.makeText(context, "Long Press Detected!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                shape = RoundedCornerShape(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FilledCardExample(
            text = "Meow meow",
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { text = inputText},
            enabled = isFormFilled,
            modifier = Modifier.pointerInput(Unit){
                detectTapGestures(
                    onLongPress = {
                        Toast.makeText(context, "Long Press Detected!", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            colors = if (isFormFilled) ButtonDefaults.buttonColors()
            else
                ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                )
            ) {
            Text("Meow!")
        }

    }
}
@Composable
fun FilledCardExample(
    text: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .size(width = 500.dp, height = 100.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE), // Background color
                contentColor = Color.White, // Text color
                disabledContainerColor = Color.Gray, // Background color when disabled
            disabledContentColor = Color.LightGray // Text color when disabled
    )
    ) {
        Text("Log In")
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectComposeDTheme {
        MyScreen()
    }
}
