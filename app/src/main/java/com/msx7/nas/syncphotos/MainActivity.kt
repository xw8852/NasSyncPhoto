package com.msx7.nas.syncphotos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.TextFieldDecorationBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import com.msx7.nas.syncphotos.data.LocalStorage
import com.msx7.nas.syncphotos.model.LoginModel
import com.msx7.nas.syncphotos.ui.theme.SyncPhotosTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (loginJump()) {
            return
        }
        setContent {
            val focusManager = LocalFocusManager.current
            SyncPhotosTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
                ) { _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF84fab0), Color(0xFF8fd3f4))
                                ),
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        GreetingPreview(vm)
                    }

                }
            }
        }
    }

    private fun loginJump(): Boolean {
        if (LocalStorage.instance.isLogin()) {
            this.startActivity(Intent(this, AlbumActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
            return true
        }
        return false
    }

    val vm = LoginModel()
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview(vm: LoginModel = LoginModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    if (vm.isLogin) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = true
            ),
            onDismissRequest = {

            },
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
    SyncPhotosTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                vm.loginUiState.userName,
                onValueChange = { a ->
                    vm.updateUserName(a)
                },
                singleLine = true,
                placeholder = {
                    Text("请输入账号", color = Color(0xFF999999))
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Box(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                vm.loginUiState.password,
                onValueChange = { a ->
                    vm.updatePassword(a)
                },
                singleLine = true,
                placeholder = {
                    Text("请输入密码", color = Color(0xFF999999))
                },
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(color = Color(0xFF333333)),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    capitalization = KeyboardCapitalization.None
                ),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            Box(modifier = Modifier.height(30.dp))
            //background-image: linear-gradient(to top, #a8edea 0%, #fed6e3 100%);
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .shadow(3.dp, shape = RoundedCornerShape(100.dp))
                    .clickable {
                        scope.launch { vm.login(context) }
                    }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFa8edea), Color(0xFFfed6e3)
                            ),
                            start = Offset(10f, 100f)
                        ),
                        shape = RoundedCornerShape(100.dp)
                    )
                    .width(160.dp)
                    .padding(vertical = 20.dp)


            ) {
                Text(
                    "登录",
                    style = TextStyle(
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                        color = Color(0xDD333333),
                        textAlign = TextAlign.Center
                    ),
                )
            }
        }

    }
}
