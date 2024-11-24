package com.msx7.nas.syncphotos

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import androidx.lifecycle.ViewModel
import com.msx7.nas.syncphotos.ui.theme.SyncPhotosTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                        GreetingPreview()
                    }

                }
            }
        }
    }
}

private val _uiState = MutableStateFlow(LoginModel())
val uiState = _uiState.asStateFlow()

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    if (_uiState.value.isLogin) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = true
            ),
            onDismissRequest = {
                _uiState.update {
                    it.copy(
                        isLogin = false
                    )
                }

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
                uiState.value.username,
                onValueChange = { a ->
                    if (a.length < 32) _uiState.update { it.copy(username = a) }
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
                uiState.value.password,
                onValueChange = { a ->
                    if (a.length < 16) _uiState.update { it.copy(password = a) }
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
                    .clickable {
                        _uiState.update { it.copy(isLogin = true) }
                    }

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

// 定义界面状态
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// ViewModel 实现
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChanged -> {
                _uiState.update { it.copy(username = event.username) }
            }
            is LoginEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is LoginEvent.OnLoginClicked -> {
                login()
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // 模拟网络请求
            delay(2000)
            val success = _uiState.value.username == "user" && _uiState.value.password == "pass"
            if (success) {
                _uiState.update { it.copy(isLoading = false) }
                // 处理登录成功
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "登录失败") }
            }
        }
    }
}
